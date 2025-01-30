package com.qurba.android.ui.offers.view_models

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.facebook.appevents.AppEventsConstants
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFacebookEventsLogging
import com.qurba.android.network.models.BaseModel
import com.qurba.android.network.models.CartModel
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.OffersModel
import com.qurba.android.network.models.response_models.DeliveryValidationPayload
import com.qurba.android.network.models.response_models.OfferDetailsResponseModel
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.cart.views.CartActivity
import com.qurba.android.ui.comments.views.OverlayFragment
import com.qurba.android.ui.customization.views.CustomizOffersActivity
import com.qurba.android.ui.place_details.views.GalleryPreviewActivity
import com.qurba.android.ui.place_details.views.ImageScrollActivity
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addOfferRequestData
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class OfferDetailsViewModel(application: Application) : BaseViewModel(application),
    ClearCartCallback, CommentsCallBack, SocialLoginCallBack {
    private var offerDetailsSubscriber: Subscriber<Response<OfferDetailsResponseModel>>? = null
    var offerObservable: MutableLiveData<OffersModel>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    @get:Bindable
    var isReadyToOrder = false
        get() = field && !offersModel.isDeliveringToArea
        set(readyToOrder) {
            field = readyToOrder
            notifyDataChanged()
        }

    private var response: Response<BaseModel>? = null
    private var isLoading = false
    private var isFromOrderNow = false
    private var offersModel = OffersModel()
    private var removeFavoriteSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    private var addFavoriteSubscriber: Subscriber<Response<StringOnlyResponse>>? = null
    private var deliveryAreaPayObservable: MutableLiveData<DeliveryValidationPayload>? = null
    private var activity: BaseActivity? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
    var isHaveCommentsCount: ObservableField<Boolean> = ObservableField<Boolean>(false)
    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    fun setFromOrderNow(fromOrderNow: Boolean) {
        isFromOrderNow = fromOrderNow
    }

    @get:Bindable
    val name: String
        get() = if (offersModel.title != null) offersModel.title.en else ""

    @get:Bindable
    val isVariablePrice: Boolean
        get() = offersModel.pricing == null || offersModel.pricing.equals(
            "fixed",
            ignoreCase = true
        )

    lateinit var likeBtn: View

    @get:Bindable
    val price: String
        get() = NumberFormat.getInstance()
            .format(offersModel.price.toLong()) + " " + activity!!.getString(R.string.currency)

    @get:Bindable
    val beforePrice: String
        get() = if (offersModel.beforePrice == 0f) "" else NumberFormat.getInstance()
            .format(offersModel.beforePrice.toDouble()) + " " + activity!!.getString(R.string.currency)

    @get:Bindable
    val termsAndConditions: String?
        get() =
            if (offersModel.termsAndConditions != null) offersModel?.termsAndConditions else ""

    @get:Bindable
    val message: String
        get() = if (offersModel.availability != null) offersModel.availability.en else ""

    @get:Bindable
    val endDate: String
        get() = if (offersModel.endDate != null) activity!!.getString(R.string.valid_till) + " " + DateUtils.getShortDate(
            offersModel.endDate
        ) else ""

    @get:Bindable
    val orderTitle: String
        get() {
            if (offersModel.type == null) return ""
            return if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                    ignoreCase = true
                )
            ) activity!!.getString(R.string.menu) else activity!!.getString(R.string.order_hint)
        }

    @get:Bindable
    val offerType: String
        get() = if (offersModel.type == null) "" else if (offersModel.type == Constants.OFFER_TYPE_DISCOUNT) {
            NumberUtils.getFinaleDiscountValue(offersModel.discountRatio) + " " + activity!!.getString(
                R.string.discount
            )
        } else if (offersModel.type == Constants.OFFER_TYPE_FREE_ITEMS) {
            activity!!.getString(R.string.free_items)
        } else if (offersModel.type.equals(
                Constants.OFFER_TYPE_DISCOUNT_ON_ITEMS,
                ignoreCase = true
            )
        ) NumberUtils.getFinaleDiscountValue(offersModel.discountRatio) + " " + activity!!.getString(
            R.string.on_items
        ) else if (offersModel.type.equals(
                Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                ignoreCase = true
            )
        ) NumberUtils.getFinaleDiscountValue(offersModel.discountRatio) + " " + activity!!.getString(
            R.string.on_menu
        ) else activity!!.getString(R.string.free_delivery_hint)

    @get:Bindable
    val isHaveCounts: Boolean
        get() = (offersModel.likesCount > 0) || (offersModel.commentsCount > 0) || (offersModel.sharesCount > 0) || (offersModel.ordersCount > 0)

    @get:Bindable
    val isHaveLikeCounts: Boolean
        get() = offersModel.likesCount > 0

    @get:Bindable
    val likeCounts: String
        get() = NumberFormat.getInstance().format(offersModel.likesCount.toLong())

    @get:Bindable
    val isHaveCommtsCounts: Boolean
        get() = !offersModel.comments.isNullOrEmpty()

    @get:Bindable
    val isHaveGallery: Boolean
        get() = !offersModel.galleryUrls.isNullOrEmpty()

    @get:Bindable
    val isHaveShareCounts: Boolean
        get() = offersModel.sharesCount > 0

    @get:Bindable
    val isHaveOrdersCounts: Boolean
        get() = offersModel.ordersCount > 0

    @get:Bindable
    val commentsCounts: String
        get() = offersModel.localizedCommentsCount

    @get:Bindable
    val shareCounts: String
        get() = offersModel.localizedSharesCount

    @get:Bindable
    val ordersCounts: String
        get() = NumberFormat.getInstance().format(offersModel.ordersCount.toLong())

    @get:Bindable
    val description: String
        get() = offersModel.description

    @get:Bindable
    val placeName: String
        get() = if (offersModel.placeId.name != null) activity!!.getString(R.string.view_all_offers_at)
            .toString() + " " + offersModel.placeId.name.en else ""

    @get:Bindable
    val isOpenNow: Boolean
        get() = offersModel.isPlaceOpen

    @get:Bindable
    val isOfferAvailable: Boolean
        get() = offersModel.isOfferAvailable

    @get:Bindable
    val isDeliveringToArea: Boolean
        get() = offersModel.isDeliveringToArea

    @get:Bindable
    val isHaveSections: Boolean
        get() = offersModel.isHasSections

    @get:Bindable
    val isLikedByUser: Boolean
        get() = offersModel.isLikedByUser

    @get:Bindable
    val otherOffersTitle: String
        get() = activity!!.getString(R.string.other_offers_from)
            .toString() + " " + offersModel.placeId.name.en

    @get:Bindable
    val placeAndBranchName: String
        get() {
            return if ((SharedPreferencesManager.getInstance().language == "en")) offersModel.placeId.name.en + " - " + offersModel.placeId.branchName.en else offersModel.placeId.name.ar + " - " + offersModel.placeId.branchName.ar
        }

    @get:Bindable
    val placeCategory: String
        get() = if (offersModel.placeId.categories == null || offersModel.placeId.categories.isEmpty()) "" else offersModel.placeId.categories.get(
            0
        ).name.en

    @get:Bindable
    val isOrderable: Boolean
        get() = offersModel.isOrderable

    @get:Bindable
    val isDiscountOnMenu: Boolean
        get() {
            return if (offersModel.type == null) false else offersModel.type.equals(
                Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                ignoreCase = true
            )
        }

    @get:Bindable
    val availability: String
        get() = if (offersModel.availability != null) offersModel.availability.en else ""

    fun getOfferDetails(_id: String?) {
        setLoading(true)
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            val call = getInstance().getOfferDetails(_id!!)

            logging(
                QurbaApplication.getContext(),
                Constants.USER_RETRIEVE_OFFER_DETAILS_ATTEMPT,
                LEVEL_INFO, "User trying to retrieve offer details", ""
            )

            offerDetailsSubscriber = object : Subscriber<Response<OfferDetailsResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_RETRIEVE_OFFER_DETAILS_FAIL,
                        LEVEL_ERROR, "Failed to retrieve offer details", e.stackTrace.toString()
                    )
                }

                override fun onNext(response: Response<OfferDetailsResponseModel>) {
                    val getOffer = response.body()
                    if (response.code() == 200) {
                        setLoading(false)
                        offerObservable!!.postValue(getOffer!!.payload)
                        offersModel = getOffer.payload
                        notifyDataChanged()
                        Log.d("offer_details", response.body().toString())
                        logViewContentEvent(offersModel)
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_RETRIEVE_OFFER_DETAILS_SUCCESS,
                            LEVEL_INFO, "Successfully retrieve offer details", ""
                        )
                    } else {
                        if (activity is CustomizOffersActivity) activity?.finish()
                        response.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_RETRIEVE_OFFER_DETAILS_FAIL,
                                "Failed to retrieve offer details "
                            )
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(offerDetailsSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    @get:Bindable
    val offerPrice: String?
        get() {
            if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                    ignoreCase = true
                )
            ) return activity?.getString(R.string.menu)
            if (offersModel.price.toFloat() != 0f) {
                when (sharedPref.language) {
                    "ar" -> return NumberFormat.getInstance()
                        .format(((offersModel.price * 100) / 100).toLong()) + " " + activity?.getString(
                        R.string.currency
                    )
                    else -> return activity?.getString(R.string.currency)
                        .toString() + " " + NumberFormat.getInstance()
                        .format(((offersModel.price * 100) / 100).toLong())
                }
            }
            return ""
        }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    fun likeDisLikeOffer(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            likeBtn = v

            if (SharedPreferencesManager.getInstance().token == null || SharedPreferencesManager.getInstance().isGuest) {
                activity!!.showLoginDialog(SocialLoginCallBack { onLoginFinished() })
            } else {
                setActions()
            }
            (v as FloatingActionButton).hide()
            v.show()
            //            ((FloatingActionButton)v).setSize(FloatingActionButton.SIZE_NORMAL);
            v.invalidate()
            v.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun setActions() {
        if (!offersModel.isLikedByUser) {
            likeOffer(offersModel._id)
        } else {
            unlikeOffer(offersModel._id)
        }
    }

    fun likeOffer(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            offersModel.likesCount = offersModel.likesCount + 1
            offersModel.isLikedByUser = true
            likeBtn.isEnabled = false

            notifyDataChanged()
            logging(
                QurbaApplication.getContext(),
                Constants.USER_LIKE_OFFER_ATTEMPT,
                LEVEL_INFO, "User trying to like offer", ""
            )
            val call = getInstance().likeOffer(id!!)
            addFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {
                    likeBtn.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_LIKE_OFFER_SUCCESS,
                        LEVEL_ERROR, "Failed to like offer", e.stackTrace.toString()
                    )
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_LIKE_OFFER_SUCCESS,
                            LEVEL_INFO, "User successfully liked offer", ""
                        )
                    } else {
                        offersModel.likesCount = offersModel.likesCount - 1
                        offersModel.isLikedByUser = false
                        notifyDataChanged()
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            val errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_LIKE_OFFER_FAIL,
                                LEVEL_ERROR, "Failed to like offer", errorMsg
                            )
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg,
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_LIKE_OFFER_FAIL,
                                LEVEL_ERROR, "Failed to like offer", e.stackTrace.toString()
                            )
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext().getString(R.string.something_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                "No internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun unlikeOffer(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_UNLIKE_OFFER_ATTEMPT,
                LEVEL_INFO, "User trying to unlike offer", ""
            )
            offersModel.likesCount = offersModel.likesCount - 1
            offersModel.isLikedByUser = false
            likeBtn.isEnabled = false

            notifyDataChanged()
            val call = getInstance().unlikeOffer(id!!)
            removeFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {
                    likeBtn.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_UNLIKE_OFFER_FAIL,
                        LEVEL_ERROR, "Failed to unlike offer", e.stackTrace.toString()
                    )
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_UNLIKE_OFFER_SUCCESS,
                            LEVEL_INFO, "User successfully unliked offer", ""
                        )
                    } else {
                        offersModel.likesCount = offersModel.likesCount + 1
                        offersModel.isLikedByUser = true
                        notifyDataChanged()
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            val errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UNLIKE_OFFER_FAIL,
                                LEVEL_ERROR, "Failed to unlike offer", errorMsg
                            )
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg,
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UNLIKE_OFFER_FAIL,
                                LEVEL_ERROR, "Failed to unlike offer", e.stackTrace.toString()
                            )
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext().getString(R.string.something_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(removeFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                "No internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun shareOffer(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = getInstance().shareOffer(id!!)
            logging(
                QurbaApplication.getContext(),
                Constants.USER_SHARE_OFFER_ATTEMPT,
                LEVEL_INFO, "User trying to share offer", ""
            )
            addFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        offersModel.sharesCount = offersModel.sharesCount + 1
                        notifyDataChanged()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_SHARE_OFFER_SUCCESS,
                            LEVEL_INFO, "User successfully share offer", ""
                        )
                    } else {
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            val errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_SHARE_OFFER_FAIL,
                                LEVEL_ERROR, "Failed to share offer", errorMsg
                            )
                            //                            Toast.makeText(QurbaApplication.getContext(), errorMsg
//                                    , Toast.LENGTH_LONG).show();
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_SHARE_OFFER_FAIL,
                                LEVEL_ERROR, "Failed to share offer", e.stackTrace.toString()
                            )

//                            Toast.makeText(QurbaApplication.getContext(),
//                                    QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addFavoriteSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openPlaceDetailsActivity(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            openPlacePage(1)
        }
    }

    fun openAllOffers(): View.OnClickListener {
        return View.OnClickListener {
            openPlacePage(0)
        }
    }

    private fun openPlacePage(type: Int) {
        val intent: Intent = Intent(getApplication(), PlaceDetailsActivity::class.java)
        if ((SharedPreferencesManager.getInstance().language == "en")) {
            intent.putExtra(
                "place_name",
                offersModel.placeId.name.en + " - " + offersModel.placeId.branchName.en
            )
        } else {
            intent.putExtra(
                "place_name",
                offersModel.placeId.name.ar + " - " + offersModel.placeId.branchName.ar
            )
        }
        intent.putExtra("unique_name", offersModel.placeId.uniqueName)
        intent.putExtra("place_id", offersModel.placeId._id)
        //            intent.putExtra("product-tab-name", offersModel.getPlaceId().getCategories().get(0).getProductsTapName());
        intent.putExtra(Constants.PLACE, Gson().toJson(offersModel.placeId))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        if (type == 0) intent.putExtra(Constants.ORDER_TYPE, Constants.OFFERS)
        activity?.startActivity(intent)
    }

    fun opendDeliveryAreaActivity(): View.OnClickListener {
        return View.OnClickListener { v: View ->

            if (offersModel?.placeId == null || offersModel?.placeId?._id == null) {
                logging(
                    QurbaApplication.getContext(), Constants.USER_OFFER_ORDER_FAIL, LEVEL_ERROR,
                    "User ordering an offer action failed",
                    "offer's place is null or place's id is null " + offersModel?.placeId?._id
                )
                return@OnClickListener
            }

            if (offersModel.type.equals(
                    Constants.OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU,
                    ignoreCase = true
                )
            ) {
                val intent: Intent = Intent(v.context, PlaceDetailsActivity::class.java)
                if ((SharedPreferencesManager.getInstance().language == "en")) {
                    intent.putExtra("place_name", offersModel.placeId.name.en)
                } else {
                    intent.putExtra("place_name", offersModel.placeId.name.ar)
                }
                intent.putExtra("unique_name", offersModel.placeId.uniqueName)
                intent.putExtra("place_id", offersModel.placeId._id)
                //                intent.putExtra("product-tab-name", offersModel.getPlaceId().getCategories().get(0).getProductsTapName());
                intent.putExtra(Constants.ORDER_TYPE, Constants.PRODUCTS)
                intent.putExtra(Constants.PLACE, Gson().toJson(offersModel.placeId))
                logging(
                    (activity)!!, Constants.USER_OFFER_ORDER_SUCCESS, LEVEL_INFO,
                    "User ordering an offer action success", ""
                )
                v.context.startActivity(intent)
                return@OnClickListener
            }
            val cart = sharedPref.getCart(offersModel.placeId._id)
            if (sharedPref?.cart != null && cart == null) {
                activity!!.showClearCartDialog(sharedPref.cart.placeModel?.name?.en, this)
                return@OnClickListener
            }
            //            if (realmController.getCart() != null && realmController.getCart(offersModel.getPlaceId().get_id()) != null) {
//                //realmController.copyOrUpdate(offersModel);
//                navigateToCustomizeOfferActivity();
//                return;
//            }
            navigateToCustomizeOfferActivity()
        }
    }

    fun openImagesPreview() {
        val images = ArrayList<String>()
        images.addAll(offersModel.galleryUrls)
        images.add(offersModel.getPictureUrl())
        val intent = Intent(activity, GalleryPreviewActivity::class.java)
        intent.putStringArrayListExtra("images", images)
        activity!!.startActivity(intent)
    }

    override fun clearCart() {
        sharedPref.clearCart()
        navigateToCustomizeOfferActivity()
    }

    fun commentClick(): View.OnClickListener {
        return View.OnClickListener { openCommentsOverlay(false, String()) }
    }

    fun openCommentsOverlay(isReply: Boolean, vararg commentsId: String?) {
        val commentsPopupFragment = OverlayFragment()
        val bundle = Bundle()
        bundle.putString("offer_id", offersModel._id)
        bundle.putString("offer_name", offersModel.title.en)
        bundle.putInt("like_count", offersModel.likesCount)
        bundle.putInt("share_count", offersModel.sharesCount)
        bundle.putString(Constants.COMMENT_ID, if (commentsId.size > 0) commentsId[0] else "")
        bundle.putBoolean(Constants.IS_REPLY, isReply)
        bundle.putString("includeDelimiter", if (commentsId.size > 1) commentsId[1] else "")
        commentsPopupFragment.setAddCommentCallBack(this)
        commentsPopupFragment.arguments = bundle
        commentsPopupFragment.show(activity!!.supportFragmentManager, "CommentsPopupFragment")
    }

    fun shareOffer(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val sendIntent: Intent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                offersModel.deepLink
            )
            sendIntent.type = "text/plain"
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            QurbaApplication.getContext().startActivity(sendIntent)
            shareOffer(offersModel._id)
        }
    }

    private fun navigateToCustomizeOfferActivity() {
        val intent: Intent
        if ((offersModel.sections != null
                    && !offersModel.sections.isEmpty()) ||
            offersModel.sectionGroups != null && !offersModel.sectionGroups.isEmpty()
        ) {
            intent = Intent(QurbaApplication.getContext(), CustomizOffersActivity::class.java)
            intent.putExtra(Constants.IS_FROM_DETAILS, true)
            intent.putExtra(Constants.OFFERS, Gson().toJson(offersModel))

//            if (!isFromOrderNow) {
//                intent = new Intent(QurbaApplication.getContext(), PlaceDetailsActivity.class);
//                if (SharedPreferencesManager.getInstance().getLanguage().equals("en")) {
//                    intent.putExtra("place_name", offersModel.getPlaceId().getName().getEn());
//                } else {
//                    intent.putExtra("place_name", offersModel.getPlaceId().getName().getAr());
//                }
//                intent.putExtra("unique_name", offersModel.getPlaceId().getUniqueName());
//                intent.putExtra("place_id", offersModel.getPlaceId().get_id());
//                intent.putExtra("product-tab-name", offersModel.getPlaceId().getCategories().get(0).getProductsTapName());
//                intent.putExtra(Constants.IS_ORDERING, true);
//            } else
        } else {
            offersModel.hashKey = offersModel._id
            if (!sharedPref.checkIfLimitQtyReached(activity, offersModel._id)) return
            addOfferToCart(offersModel)
//            ViewModelProvider(activity!!).get(BaseItemViewModel::class.java).addOfferToCart(offersModel)
            intent = Intent(QurbaApplication.getContext(), CartActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        QurbaApplication.getContext().startActivity(intent)
        logging(
            activity!!, Constants.USER_OFFER_ORDER_SUCCESS, LEVEL_INFO,
            "User ordering an offer action success", ""
        )
    }

    override fun onCommentAdded(commentModel: CommentModel, isReply: Boolean) {
        if (isReply) offersModel.recentComment =
            commentModel else if (offersModel.recentComment != null && commentModel._id.equals(
                offersModel.recentComment._id,
                ignoreCase = true
            )
        ) offersModel.recentComment.recentReply = commentModel
        offersModel.commentsCount = offersModel.commentsCount + 1
        //        commentsCount.set(offersModel.getCommentsCount() + "");
        isHaveCommentsCount.set(offersModel.commentsCount > 0)
        notifyDataChanged()
        //  commentsAdapter.addLastComment(commentModel);
    }

    override fun onCommentUpdated(commentModel: CommentModel, isReply: Boolean) {
        //in case of updating other comment rather than recent comment
        if (offersModel.recentComment == null || !commentModel._id.equals(
                offersModel.recentComment._id,
                ignoreCase = true
            )
        ) return
        if (isReply) {
            offersModel.recentComment.recentReply.comment = commentModel.comment
        } else {
            offersModel.recentComment.comment = commentModel.comment
        }
        notifyDataChanged()
        // commentsAdapter.updateLastComment(commentModel);
    }

    override fun onCommentDeleted(isReply: Boolean, deletedCommentsCount: Int) {
        if (isReply) offersModel.recentComment.recentReply = null else offersModel.recentComment =
            null
        offersModel.commentsCount = offersModel.commentsCount - 1
        //        commentsCount.set(offersModel.getCommentsCount() + "");
        isHaveCommentsCount.set(offersModel.commentsCount > 0)
        notifyDataChanged()
        //commentsAdapter.deleteComment(null);
    }

    override fun onLoginFinished() {
        setActions()
    }

    private fun addOfferToCart(offersModel: OffersModel) {
        if (SystemUtils.isNetworkAvailable(activity)) {

            sharedPref.copyOrUpdate(offersModel)
            logging(
                activity!!,
                Constants.USER_ADD_OFFER_TO_CART_ATTEMPT, Line.LEVEL_INFO,
                "User trying to add offer to his cart ", ""
            )
            //lifecycleScope
            activity?.lifecycleScope?.launchWhenStarted {
                try {
                    response = APICalls.instance.addOffer(addOfferRequestData(offersModel))
                } catch (exception: Exception) {
                    sharedPref.removeItemFromCart(offersModel._id)
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity!!,
                        Constants.USER_ADD_OFFER_TO_CART_FAIL, LEVEL_ERROR,
                        "User failed to add to his cart ", exception.message
                    )
                }
                //for ui handling
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true)
                        logging(
                            activity!!,
                            Constants.USER_ADD_OFFER_TO_CART_SUCCESS, LEVEL_INFO,
                            "User successfully add offer to his cart ", ""
                        )
                    else {
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_ADD_OFFER_TO_CART_FAIL,
                                "User failed to get offer details "
                            )
                        }
                    }
                }
            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

    fun logViewContentEvent(offersModel: OffersModel) {
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Constants.OFFERS)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, offersModel._id)
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP")
        standardFacebookEventsLogging(
            QurbaApplication.getContext(),
            AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
            offersModel.price.toDouble(),
            params
        )
    }
}