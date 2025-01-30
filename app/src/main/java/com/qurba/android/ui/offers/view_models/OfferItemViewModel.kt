package com.qurba.android.ui.offers.view_models

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.databinding.*
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.*
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.ui.comments.views.OverlayFragment
import com.qurba.android.ui.customization.views.CustomizOffersActivity
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.ui.offers.views.OfferDetailsActivity
import com.qurba.android.ui.order_now.view_models.OrderNowViewModel
import com.qurba.android.ui.place_details.view_models.PagerAgentViewModel
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.ui.profile.views.UserMealsActivity
import com.qurba.android.ui.profile.views.UserOffersActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addOfferRequestData
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*

class OfferItemViewModel(
    private val activity: BaseActivity,
    private val offersModel: OffersModel,
    private val isCard: Boolean) : BaseItemViewModel(activity), ClearCartCallback {
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()
    private lateinit var addFavoriteSubscriber: Subscriber<Response<StringOnlyResponse>>
    private lateinit var removeFavoriteSubscriber: Subscriber<Response<StringOnlyResponse>>

    var isFavourite: ObservableField<Boolean> = ObservableField()
    private var orderNowViewModel: OrderNowViewModel? = null
    var countsTxt: ObservableField<String> = ObservableField()
    var likeCount: ObservableField<String> = ObservableField<String>("")
    var commentsCount: ObservableField<String> = ObservableField<String>("")
    var sharesCount: ObservableField<String> = ObservableField<String>("")
    var isHaveLikesCount: ObservableField<Boolean> = ObservableField<Boolean>(false)
    var isHaveCommentsCount: ObservableField<Boolean> = ObservableField<Boolean>(false)
    var isHaveSharesCount: ObservableField<Boolean> = ObservableField<Boolean>(false)
    var commentText: ObservableField<String> = ObservableField<String>("")
    private val sharedPref: SharedPreferencesManager = SharedPreferencesManager.getInstance()
    private var commentActionEvents: CommentActionEvents? = null
    private var addCommentCallBack: CommentsCallBack? = null
    private var isEdit: Boolean = false
    private var commentId: String? = null

    @get:Bindable
    var isProgress: Boolean = false
        set(progress) {
            field = progress
            notifyDataChanged()
        }

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack?) {
        this.addCommentCallBack = addCommentCallBack
    }

    fun setCommentActionEvents(commentActionEvents: CommentActionEvents?) {
        this.commentActionEvents = commentActionEvents
    }

    fun setPlaceInfo(placeInfo: PlaceModel?) {
        offersModel.placeId = placeInfo
    }

    @get:Bindable
    val isReadyToOrder: Boolean
        get() = offersModel.isOrderable

    @get:Bindable
    val isHaveComments: Boolean
        get() = ((offersModel.recentComment
                == null)) || (offersModel.recentComment._id == null
                ) || (offersModel.recentComment.user == null)

    @get:Bindable
    val availability: String
        get() {
            return if (offersModel.availability != null) offersModel.availability.en else ""
        }

    @get:Bindable
    val title: String
        get() {
            return offersModel.title.en
        }

    @get:Bindable
    val description: String
        get() {
            return offersModel.description
        }

    @get:Bindable
    val type: String
        get() {
            if ((offersModel.type == Constants.OFFER_TYPE_DISCOUNT)) {
                return activity.getString(R.string.discount)
            } else if ((offersModel.type == Constants.OFFER_TYPE_FREE_ITEMS)) {
                return activity.getString(R.string.items)
            } else if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS,
                    ignoreCase = true
                )
            ) return activity.getString(R.string.on_items) else if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                    ignoreCase = true
                )
            ) return activity.getString(R.string.on_menu) else return activity.getString(R.string.delivery)
        }

    @get:Bindable
    val isDiscountOnMenu: Boolean
        get() {
            return offersModel.type.equals(
                Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                ignoreCase = true
            )
        }

    @get:Bindable
    val isHaveEndDate: Boolean
        get() {
            return offersModel.endDate != null
        }

    @get:Bindable
    val endDate: String
        get() {
            return if (offersModel.endDate != null) activity.getString(R.string.valid_till)
                .toString() + " " + DateUtils.getShortDate(offersModel.endDate) else ""
        }

    @get:Bindable
    val offerPrice: String
        get() {
            if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                    ignoreCase = true
                )
            ) return activity.getString(R.string.menu)
            if (offersModel.price.toFloat() != 0f) {
                when (sharedPref.language) {
                    "ar" -> return NumberFormat.getInstance()
                        .format(((offersModel.price * 100) / 100).toLong()) + " " + activity.getString(
                        R.string.currency
                    )
                    else -> return activity.getString(R.string.currency)
                        .toString() + " " + NumberFormat.getInstance()
                        .format(((offersModel.price * 100) / 100).toLong())
                }
            }
            return ""
        }

    @get:Bindable
    val isVariablePrice: Boolean
        get() {
            return offersModel.pricing == null || offersModel.pricing.equals(
                "fixed",
                ignoreCase = true
            )
        }

    @get:Bindable
    val isMatchParent: Boolean
        get() {
            return offerOldPrice.isEmpty() && !isVariablePrice
        }

    @get:Bindable
    val offerOldPrice: String
        get() {
            if (offersModel.beforePrice == 0f) return ""
            return if (sharedPref.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) NumberFormat.getInstance()
                .format(((offersModel.beforePrice.toInt() * 100) / 100).toLong()) + " " + activity.getString(
                R.string.currency
            ) else activity.getString(R.string.currency)
                .toString() + " " + NumberFormat.getInstance()
                .format(((offersModel.beforePrice.toInt() * 100) / 100).toLong())
        }

    @get:Bindable
    val placeAndBranchName: String
        get() {
            if (activity is PlaceDetailsActivity) return offersModel.description
            return offersModel.placeId.name.en + " - " + offersModel.placeId.branchName.en
        }

    @get:Bindable
    val isSponsored: Boolean
        get() {
            return offersModel.isSponsored
        }

    @Bindable
    fun getDiscount(): String { //DISCOUNT_ON_ENTIRE_MENU
        when (offersModel.type) {
            Constants.OFFER_TYPE_DISCOUNT -> return NumberUtils.getFinaleDiscountValue(offersModel.discountRatio) + " " + activity.getString(
                R.string.discount
            )
            Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS -> return NumberUtils.getFinaleDiscountValue(
                offersModel.discountRatio
            ) + " " + activity.getString(R.string.discount_on_items)
            Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU -> return NumberUtils.getFinaleDiscountValue(
                offersModel.discountRatio
            ) + " " + activity.getString(R.string.discount_on_menu)
            Constants.OFFER_TYPE_FREE_DELIVERY -> return if (sharedPref.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) (activity.getString(R.string.delivery)
                .toString() + " " + activity.getString(R.string.free)) else (activity.getString(R.string.free)
                .toString() + " " + (activity.getString(R.string.delivery)))
            else -> return if (sharedPref.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) activity.getString(R.string.items)
                .toString() + " " + activity.getString(R.string.free) else activity.getString(R.string.free)
                .toString() + " " + activity.getString(R.string.items)
        }
    }

    //DISCOUNT_ON_ENTIRE_MENU
    @get:Bindable
    val offerType: String
        get() { //DISCOUNT_ON_ENTIRE_MENU
            when (offersModel.type) {
                Constants.OFFER_TYPE_DISCOUNT -> return NumberUtils.getFinaleDiscountValue(
                    offersModel.discountRatio
                ) + "\n" + activity.getString(R.string.discount)
                Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS -> return NumberUtils.getFinaleDiscountValue(
                    offersModel.discountRatio
                ) + "\n" + activity.getString(R.string.on_items)
                Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU -> return NumberUtils.getFinaleDiscountValue(
                    offersModel.discountRatio
                ) + "\n" + activity.getString(R.string.on_menu)
                Constants.OFFER_TYPE_FREE_DELIVERY -> return if (sharedPref.language.equals(
                        "ar",
                        ignoreCase = true
                    )
                ) activity.getString(R.string.delivery)
                    .toString() + "\n" + activity.getString(R.string.free) else activity.getString(R.string.free)
                    .toString() + "\n" + activity.getString(R.string.delivery)
                else -> return if (sharedPref.language.equals(
                        "ar",
                        ignoreCase = true
                    )
                ) activity.getString(R.string.items)
                    .toString() + "\n" + activity.getString(R.string.free) else activity.getString(R.string.free)
                    .toString() + "\n" + activity.getString(R.string.items)
            }
        }

    fun setCountsTxt() {
        var count: String = ""
        if (offersModel.likesCount > 0) count += offersModel.likesCount.toString() + " " + activity.getString(
            R.string.likes
        )
        if (offersModel.commentsCount > 0) count += (if (!count.isEmpty()) " • " else "") + offersModel.commentsCount + " " + activity.getString(
            R.string.comments
        )
        if (offersModel.sharesCount > 0) count += (if (!count.isEmpty()) " • " else "") + offersModel.sharesCount + " " + activity.getString(
            R.string.shares
        )
        //        if (offersModel.getOrdersCount() > 0)
//            count += (!count.isEmpty() ? " • " : "") + offersModel.getOrdersCount() + " " + activity.getString(R.string.orders);
        countsTxt.set(count)
    }

    fun setOrderNowViewModel(orderNowViewModel: OrderNowViewModel?) {
        this.orderNowViewModel = orderNowViewModel
    }

    override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    fun likeOffer(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            if (sharedPref.getToken() == null || sharedPref.isGuest()) {
                activity.showLoginDialog()
            } else {
                v.setEnabled(false)
                if (!offersModel.isLikedByUser()) {
                    likeOffer(offersModel.getId(), v)
                } else {
                    unlikeOffer(offersModel.getId(), v)
                }
            }
        }
    }

    fun commentClick(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            val commentsPopupFragment: OverlayFragment = OverlayFragment()
            val bundle: Bundle = Bundle()
            bundle.putString("offer_id", offersModel.get_id())
            bundle.putString("offer_name", offersModel.getTitle().getEn())
            bundle.putInt("like_count", offersModel.getLikesCount())
            bundle.putInt("share_count", offersModel.getSharesCount())
            commentsPopupFragment.setAddCommentCallBack(addCommentCallBack)
            commentsPopupFragment.setArguments(bundle)
            commentsPopupFragment.show(
                (v.getContext() as BaseActivity).getSupportFragmentManager(),
                "CommentsPopupFragment"
            )
        }
    }

    fun commentLongPress(): OnLongClickListener {
        return OnLongClickListener { v: View ->
            if (sharedPref.getUser() != null && offersModel.getRecentComment().getUser().get_id()
                    .equals(sharedPref.getUser().get_id(), ignoreCase = true)
            ) {
                val overlayFragment: EditDeleteOverlayFragment = EditDeleteOverlayFragment()
                overlayFragment.setCommentActionEvents((commentActionEvents)!!)
                val bundle: Bundle = Bundle()
                bundle.putSerializable(Constants.COMMENTS, offersModel.getRecentComment())
                overlayFragment.setArguments(bundle)
                overlayFragment.show(
                    (v.getContext() as BaseActivity).getSupportFragmentManager(),
                    "EditDeleteOverlayFragment"
                )
            }
            false
        }
    }

    fun replyClickListener(): OnTouchListener {
        return label@ OnTouchListener { v: View?, event: MotionEvent ->
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                replyClick(offersModel.getRecentComment())
                true
            }
            false
        }
    }

    fun replyClick(commentModel: CommentModel) {
        val commentsPopupFragment: OverlayFragment = OverlayFragment()
        val bundle: Bundle = Bundle()
        bundle.putString("offer_id", offersModel._id)
        bundle.putString("offer_name", offersModel.title.en)
        bundle.putInt("like_count", offersModel.likesCount)
        bundle.putInt("share_count", offersModel.sharesCount)
        bundle.putSerializable(Constants.COMMENTS, commentModel)
        bundle.putString(Constants.COMMENT_ID, commentModel._id)
        bundle.putBoolean(Constants.IS_REPLY, true)
        commentsPopupFragment.setAddCommentCallBack(addCommentCallBack)
        commentsPopupFragment.arguments = bundle
        commentsPopupFragment.show(activity.supportFragmentManager, "CommentsPopupFragment")
    }

    fun shareOffer(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            shareOffer(offersModel.getId())
            val sendIntent: Intent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.setType("text/plain")
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, offersModel.getTitle().getEn())
            sendIntent.putExtra(Intent.EXTRA_TEXT, offersModel.getDeepLink())
            activity.startActivity(sendIntent)
        }
    }

    fun likeOffer(id: String?, v: View) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call: Observable<Response<StringOnlyResponse>> = getInstance().likeOffer((id)!!)
            offersModel.isLikedByUser = true
            isFavourite.set(true)
            offersModel.likesCount = offersModel.likesCount + 1
            likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
            isHaveLikesCount.set(offersModel.likesCount > 0)
            setCountsTxt()
            addFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {
                    v.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    offersModel.isLikedByUser = false
                    isFavourite.set(false)
                    offersModel.likesCount = offersModel.likesCount - 1
                    likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
                    isHaveLikesCount.set(offersModel.likesCount > 0)
                    setCountsTxt()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        activity.getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        offersModel.isLikedByUser = false
                        isFavourite.set(false)
                        offersModel.likesCount = offersModel.likesCount - 1
                        likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
                        isHaveLikesCount.set(offersModel.likesCount > 0)
                        setCountsTxt()
                        Toast.makeText(
                            QurbaApplication.getContext(),
                            activity.getString(R.string.something_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                activity.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun unlikeOffer(id: String?, v: View) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call: Observable<Response<StringOnlyResponse>> = getInstance().unlikeOffer((id)!!)
            offersModel.isLikedByUser = false
            isFavourite.set(false)
            offersModel.likesCount = offersModel.likesCount - 1
            likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
            isHaveLikesCount.set(offersModel.likesCount > 0)
            setCountsTxt()
            removeFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {
                    v.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    offersModel.isLikedByUser = true
                    isFavourite.set(true)
                    offersModel.likesCount = offersModel.likesCount + 1
                    likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
                    isHaveLikesCount.set(offersModel.likesCount > 0)
                    setCountsTxt()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        activity.getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        offersModel.isLikedByUser = true
                        isFavourite.set(true)
                        offersModel.likesCount = offersModel.likesCount + 1
                        likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
                        isHaveLikesCount.set(offersModel.likesCount > 0)
                        Toast.makeText(
                            QurbaApplication.getContext(),
                            activity.getString(R.string.something_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(removeFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                activity.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun shareOffer(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call: Observable<Response<StringOnlyResponse>> = getInstance().shareOffer((id)!!)
            offersModel.sharesCount = offersModel.sharesCount + 1
            isHaveSharesCount.set(offersModel.sharesCount > 0)
            sharesCount.set(
                offersModel.localizedSharesCount + (if (isCard) " " + activity.getString(
                    R.string.share
                ) else "")
            )
            addFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    offersModel.sharesCount = offersModel.sharesCount - 1
                    isHaveSharesCount.set(offersModel.sharesCount > 0)
                    sharesCount.set(
                        offersModel.localizedSharesCount + (if (isCard) " " + activity.getString(
                            R.string.share
                        ) else "")
                    )
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        //notifyItemChanged(position);
                    } else {
                        //Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        offersModel.sharesCount = offersModel.sharesCount - 1
                        isHaveSharesCount.set(offersModel.sharesCount > 0)
                        sharesCount.set(
                            offersModel.localizedSharesCount + (if (isCard) " " + activity.getString(
                                R.string.share
                            ) else "")
                        )
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                activity.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openPlaceDetails(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val intent: Intent = Intent(activity, PlaceDetailsActivity::class.java)
            if ((sharedPref.getLanguage() == "en")) {
                intent.putExtra("place_name", offersModel.getPlaceId().getName().getEn())
            } else {
                intent.putExtra("place_name", offersModel.getPlaceId().getName().getAr())
            }
            intent.putExtra("unique_name", offersModel.getPlaceId().getUniqueName())
            intent.putExtra("place_id", offersModel.getPlaceId().get_id())
            //            intent.putExtra("product-tab-name", offersModel.getPlaceId().getCategories().get(0).getProductsTapName());
            intent.putExtra(Constants.ORDER_TYPE, Constants.OFFERS)
            intent.putExtra(Constants.PLACE, Gson().toJson(offersModel.getPlaceId()))
            intent.putExtra(Constants.PLACE, Gson().toJson(offersModel.getPlaceId()))
            activity.startActivity(intent)
        }
    }

    fun opendDeliveryAreaActivity(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            if (offersModel?.placeId == null || offersModel?.placeId?._id == null) {
                logging(
                    activity, Constants.USER_OFFER_ORDER_FAIL, LEVEL_ERROR,
                    "User ordering an offer action failed",
                    "offer's place is null or place's id is null " + offersModel?.placeId?._id
                )
                return@OnClickListener
            }

            logging(
                activity, Constants.USER_OFFER_ORDER_SUCCESS, LEVEL_INFO,
                "User ordering an offer action success", ""
            )
            if (offersModel.getType()
                    .equals(Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU, ignoreCase = true)
            ) {
                if (QurbaApplication.currentActivity() is PlaceDetailsActivity) {
                    (QurbaApplication.currentActivity() as PlaceDetailsActivity).nabigateToMenuTab()
                    return@OnClickListener
                }
                val intent: Intent = Intent(activity, PlaceDetailsActivity::class.java)
                intent.putExtra(Constants.PLACE, Gson().toJson(offersModel.getPlaceId()))
                if ((sharedPref.getLanguage() == "en")) {
                    intent.putExtra("place_name", offersModel.getPlaceId().getName().getEn())
                } else {
                    intent.putExtra("place_name", offersModel.getPlaceId().getName().getAr())
                }
                intent.putExtra("unique_name", offersModel.getPlaceId().getUniqueName())
                intent.putExtra("place_id", offersModel.getPlaceId().get_id())
                intent.putExtra(Constants.ORDER_TYPE, Constants.PRODUCTS)
                activity.startActivity(intent)
                return@OnClickListener
            }
            val cart = sharedPref.getCart(offersModel?.placeId?._id);
            if (sharedPref?.cart != null && cart == null) {
                (v.context as BaseActivity).showClearCartDialog(
                    sharedPref.cart.placeModel?.name?.en,
                    this
                )
                return@OnClickListener
            }

            if (sharedPref.getCart() != null && sharedPref.getCart(offersModel.placeId._id) == null) {
                (v.getContext() as BaseActivity).showClearCartDialog(
                    sharedPref.cart.placeModel.name.en,
                    this
                )
                return@OnClickListener
            }
            if (activity is PlaceDetailsActivity &&
                sharedPref.getSelectedCachedArea().case > 1
            ) {
                //not supported
                activity.showUnSupportedAreaDialog(
                    ClearCartCallback {}, (offersModel.getPlaceId().getName().getEn()
                            + " " + activity.getString(R.string.not_deliver_hint) + " "
                            + sharedPref.getSelectedCachedArea().area!!.getName().getEn())
                )
                return@OnClickListener
            }
            navigateToDeliveryAreaActivity()
        }
    }

    fun opendOffersDetailsActivity(position: Int): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val intent: Intent = Intent(activity, OfferDetailsActivity::class.java)
            intent.putExtra("offer_name", offersModel.getUniqueName())
            intent.putExtra("offer_id", offersModel.get_id())
            intent.putExtra("favorite", offersModel.isLikedByUser())
            intent.putExtra("is-from-ordernow", orderNowViewModel != null)
            sharedPref.setOfferPosition(position)
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, 1000)
        }
    }

    private fun navigateToDeliveryAreaActivity() {
        navigateToCustomizeOfferActivity()
    }

    private fun navigateToCustomizeOfferActivity() {
        val intent: Intent
        if (offersModel.isHasSections || (offersModel.sections != null && offersModel.sections.isNotEmpty())) {
            intent = Intent(QurbaApplication.getContext(), CustomizOffersActivity::class.java)
            intent.putExtra(Constants.OFFERS, Gson().toJson(offersModel))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (activity is OfferDetailsActivity) intent.putExtra(Constants.IS_FROM_DETAILS, true)
            QurbaApplication.getContext().startActivity(intent)
        } else {
            offersModel.hashKey = offersModel._id
            addOfferToCart(offersModel)
        }
    }

    override fun clearCart() {
//        sharedPref.clearCart()
        navigateToCustomizeOfferActivity()
    }

    //
    //    @Override
    //    public void onCommentAdded(CommentModel commentModel) {
    //        if (this.offersModel.getComments().isEmpty()) {
    //            this.offersModel.getComments().add(commentModel);
    //        } else {
    //            this.offersModel.getComments().set(0, commentModel);
    //        }
    //        offersModel.setCommentsCount(1 + offersModel.getCommentsCount());
    //        notifyDataChanged();
    //    }
    fun addComment(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            if (sharedPref.isGuest()) activity.showLoginDialog() else {
                isProgress = true
                if (isEdit) {
                    updateOfferComment()
                } else {
                    addOfferComment()
                }
            }
        }
    }

    fun addOfferComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            if (!checkInputsValidation()) return
            logging(
                QurbaApplication.getContext(),
                Constants.USER_ADD_OFFER_COMMENT_ATTEMPT,
                LEVEL_ERROR, "User attempt to add offer comment", ""
            )
            val commetnsPayload: CommetnsPayload = CommetnsPayload()
            commetnsPayload.comment = commentText.get()!!.trim { it <= ' ' }
            val commetnsRequest: CommetnsRequest = CommetnsRequest(commetnsPayload)
            getInstance().addOfferComment(offersModel._id, commetnsRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Response<AddCommentPayload>>() {
                    override fun onCompleted() {
                        isProgress = false
                    }

                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                        Toast.makeText(
                            activity,
                            QurbaApplication.getContext().getString(R.string.something_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_ADD_OFFER_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to add offer comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<AddCommentPayload>) {
                        if (response.code() == 200) {
                            addCommentCallBack!!.onCommentAdded(
                                response.body()!!.payload.get(
                                    response.body()!!.payload.size - 1
                                ), false
                            )
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                                LEVEL_ERROR, "Successfully add offer comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error: String = response.errorBody()!!.string()
                            val jObjError: JSONObject = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error").get("en").toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to add offer comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_ADD_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to add offer comment", e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                if (errorMsg == null) QurbaApplication.getContext()
                                    .getString(R.string.something_wrong) else errorMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            return
        }
        Toast.makeText(
            activity,
            QurbaApplication.getContext().getString(R.string.no_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    fun updateOfferComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            if (!checkInputsValidation()) return
            logging(
                QurbaApplication.getContext(),
                Constants.USER_UPDATE_OFFER_COMMENT_ATTEMPT,
                LEVEL_ERROR, "User attempt to update offer comment", ""
            )
            val commetnsPayload: CommetnsPayload = CommetnsPayload()
            commetnsPayload.comment = commentText.get()!!.trim { it <= ' ' }
            val commetnsRequest = CommetnsRequest(commetnsPayload)
            getInstance().updateOfferComment(offersModel._id, (commentId)!!, commetnsRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Response<UpdateCommentPayload>>() {
                    override fun onCompleted() {
                        isProgress = false
                    }

                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                        Toast.makeText(
                            activity,
                            QurbaApplication.getContext().getString(R.string.something_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_UPDATE_OFFER_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to update offer comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<UpdateCommentPayload>) {
                        if (response.code() == 200) {
                            addCommentCallBack?.onCommentUpdated( response.body()!!.payload, false)

                            setCommentId("")
                            setEdit(false)
                            commentText.set("")
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                                LEVEL_ERROR, "Successfully update offer comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error: String = response.errorBody()!!.string()
                            val jObjError: JSONObject = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error").get("en").toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to update offer comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_OFFER_COMMENT_SUCCESS,
                                LEVEL_ERROR,
                                "Failed to update offer comment",
                                e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                if (errorMsg == null) QurbaApplication.getContext()
                                    .getString(R.string.something_wrong) else errorMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            return
        }
        Toast.makeText(
            activity,
            QurbaApplication.getContext().getString(R.string.no_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    fun deleteOfferComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_DELETE_OFFER_COMMENT_ATTEMPT,
                LEVEL_ERROR, "User attempt to delete offer comment", ""
            )
            getInstance().deleteOfferComment(offersModel._id, (commentId)!!)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Response<UpdateCommentPayload>>() {
                    override fun onCompleted() {}
                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                        Toast.makeText(
                            activity,
                            QurbaApplication.getContext().getString(R.string.something_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_DELETE_OFFER_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to delete offer comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<UpdateCommentPayload>) {
                        if (response.code() == 200 || response.code() == 204) {
                            addCommentCallBack?.onCommentDeleted(false, 1)
                            setCommentId("")
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_OFFER_COMMENT_SUCCESS,
                                LEVEL_ERROR, "Successfully delete offer comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error: String = response.errorBody()!!.string()
                            val jObjError: JSONObject = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error").get("en").toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to delete offer comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_OFFER_COMMENT_FAIL,
                                LEVEL_ERROR,
                                "Failed to delete offer comment",
                                e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                if (errorMsg == null) QurbaApplication.getContext()
                                    .getString(R.string.something_wrong) else errorMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            return
        }
        Toast.makeText(
            activity,
            QurbaApplication.getContext().getString(R.string.no_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun checkInputsValidation(): Boolean {
        if (commentText.get() == null || commentText.get()!!.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(
                activity,
                activity.getString(R.string.add_comment_warning),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun setEdit(edit: Boolean) {
        isEdit = edit
    }

    fun setCommentId(commentId: String?) {
        this.commentId = commentId
    }

    companion object {
        @BindingAdapter("android:layout_height")
        fun setLayoutHeight(view: View, isMatchParent: Boolean) {
            val params: ViewGroup.LayoutParams = view.layoutParams
            params.height =
                if (isMatchParent) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            view.layoutParams = params
        }

        @BindingAdapter("android:layout_marginTop")
        fun setTopMargin(view: View, isSponsored: Boolean) {
            val params: MarginLayoutParams = view.layoutParams as MarginLayoutParams
            val value: Int =
                (if (isSponsored) view.context.resources.getDimension(R.dimen.short_margin) else view.context.resources.getDimension(
                    R.dimen.long_margin
                )).toInt()
            params.topMargin = -value
            view.layoutParams = params
        }
    }

    init {
        isFavourite.set(offersModel.isLikedByUser)
        likeCount.set(if (isCard) offersModel.getLocalizedLikesCount(activity) else offersModel.localizedLikesCount)
        commentsCount.set(
            offersModel.localizedCommentsCount + (if (isCard) " " + activity.getString(
                R.string.comments
            ) else "")
        )
        sharesCount.set(offersModel.localizedSharesCount + (if (isCard) " " + activity.getString(R.string.share) else ""))
        //        ordersCount.set(offersModel.getOrdersCount() + (isList ? " " + activity.getString(R.string.orders) : ""));
        isHaveLikesCount.set(offersModel.likesCount > 0)
        isHaveCommentsCount.set(offersModel.commentsCount > 0)
        isHaveSharesCount.set(offersModel.sharesCount > 0)
        //        isHaveOrdersCount.set(offersModel.getOrdersCount() > 0);
        setCountsTxt()
    }
}