package com.qurba.android.ui.products.view_models

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.CommetnsPayload
import com.qurba.android.network.models.request_models.CommetnsRequest
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment
import com.qurba.android.ui.comments.views.EditDeleteOverlayFragment.CommentActionEvents
import com.qurba.android.ui.comments.views.OverlayFragment
import com.qurba.android.ui.customization.views.CustomizProductActivity
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.ui.offers.views.OfferDetailsActivity
import com.qurba.android.ui.order_now.view_models.OrderNowViewModel
import com.qurba.android.ui.place_details.view_models.PagerAgentViewModel
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.ui.products.views.ProductDetailsActivity
import com.qurba.android.ui.profile.views.UserMealsActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addProductRequestData
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

class ProductItemViewModel(
    private val activity: BaseActivity,
    private val product: ProductData?, position: Int
) : BaseItemViewModel(activity), ClearCartCallback {

    private val cartViewModel: PagerAgentViewModel
    private val position: Int
    var isLiked = ObservableField<Boolean>()
    var countsTxt = ObservableField<String>()
    var commentText: ObservableField<String> = ObservableField<String>("")
    var commentsCount: ObservableField<String> = ObservableField<String>("")
    var likeCount: ObservableField<String> = ObservableField<String>("")
    var sharesCount: ObservableField<String> = ObservableField<String>("")
    var isHaveLikesCount: ObservableField<Boolean?> = ObservableField<Boolean?>(false)
    var isHaveCommentsCount: ObservableField<Boolean?> = ObservableField<Boolean?>(false)
    var isHaveSharesCount: ObservableField<Boolean?> = ObservableField<Boolean?>(false)
    private lateinit var likeProductSubscriber: Subscriber<Response<LikeProductPayload>>
    private val sharedPref = SharedPreferencesManager.getInstance()
    private var addCommentCallBack: CommentsCallBack? = null
    private var commentActionEvents: CommentActionEvents? = null
    private var isEdit = false
    private var commentId: String? = null

    @get:Bindable
    var isProgress = false

    fun setAddCommentCallBack(addCommentCallBack: CommentsCallBack?) {
        this.addCommentCallBack = addCommentCallBack
    }

    fun setEdit(edit: Boolean) {
        isEdit = edit
    }

    fun setCommentId(commentId: String?) {
        this.commentId = commentId
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

    fun setPlaceInfo(placeInfo: PlaceModel?) {
        if (product?.placeInfo?._id == null) product?.placeInfo = placeInfo
    }

    @get:Bindable
    val isHaveComments: Boolean
        get() = product?.recentComment == null || product.recentComment._id == null || product.recentComment.user == null

    @get:Bindable
    val isFixedPrice: Boolean
        get() = if (product?.pricing == null) false else product.pricing.equals(
            "FIXED",
            ignoreCase = true
        )

    @get:Bindable
    val commentCounts: String
        get() = if (product?.commentsCount!! > 0) NumberFormat.getInstance().format(product.commentsCount)  + "" else ""

    @get:Bindable
    val likesCount: String
        get() = if (product?.likesCount!! > 0) NumberFormat.getInstance().format(product.likesCount) + "" else ""

    @get:Bindable
    val recentComment: String
        get() = if (product?.productCommentsSample?.isEmpty() == false) {
            product.productCommentsSample?.get(product.productCommentsSample.size - 1)!!.comment
        } else ""

    @get:Bindable
    val placeAndBranchName: String
        get() = if ((SharedPreferencesManager.getInstance().language == "en")) {
            product?.placeInfo?.name?.en + " - " + product?.placeInfo?.branchName?.en
        } else {
            product?.placeInfo?.name?.ar + " - " + product?.placeInfo?.branchName?.ar
        }

    //        return product.getPlaceInfo().getCategories().isEmpty() ? "" :
//                product.getPlaceInfo().getCategories().get(0).getName().getEn();
    @get:Bindable
    val placeCategory: String
        get() =//        return product.getPlaceInfo().getCategories().isEmpty() ? "" :
            //                product.getPlaceInfo().getCategories().get(0).getName().getEn();
            ""

    @get:Bindable
    val name: String?
        get() = product?.name?.en

    @get:Bindable
    val description: String
        get() = if (product?.description != null && product?.description.en != null) product?.description.en else ""

    @get:Bindable
    val isHaveDescription: Boolean
        get() = product?.description == null || product?.description.en == null

    //    @Bindable
    //    public String getCounts() {
    //        return countsTxt;
    //    }
    fun setCountsTxt() {
        var count = ""
        if (product == null) return
        if (product?.likesCount!! > 0) count += product.likesCount.toString() + " " + activity.getString(
            R.string.likes
        )
        if (product?.commentsCount!! > 0) count += (if (count.isNotEmpty()) " • " else "") + product.commentsCount + " " + activity.getString(
            R.string.comments
        )
        if (product?.sharesCount!! > 0) count += (if (count.isNotEmpty()) " • " else "") + product.sharesCount + " " + activity.getString(
            R.string.shares
        )
        //        if (product.getOrdersCount() > 0)
//            count += (!count.isEmpty() ? " • " : "") + product.getOrdersCount() + " " + activity.getString(R.string.orders);
        countsTxt.set(count)
    }

    @get:Bindable
    val productPrice: String
        get() = if (sharedPref.language.equals("ar", ignoreCase = true)) NumberFormat.getInstance()
            .format(
                product?.price?.toFloat()?.let {
                    Math.round(it).toLong()
                }) + " " + activity.getString(R.string.currency) else activity.getString(R.string.currency)
            .toString() + " " + NumberFormat.getInstance()
            .format(product?.price?.toFloat()?.let { Math.round(it).toLong() })

    @get:Bindable
    val customization: String?
        get() {
            return if (product?.category == null) "" else product?.category?.name?.en
        }

    @get:Bindable
    val isDiscountOnMenu: Boolean
        get() = product?.isDiscountOnMenu == true

    @get:Bindable
    val isOrderable: Boolean
        get() = product?.isOrderable == true

    @get:Bindable
    val isDeliveringToArea: Boolean
        get() {
            return if (activity is PlaceDetailsActivity) SharedPreferencesManager.getInstance().selectedCachedArea.case > 1 else product?.isDeliveringToArea == true
        }

    @get:Bindable
    val isPlaceOpen: Boolean
        get() = product?.isPlaceOpen == true

    @get:Bindable
    val availability: String?
        get() {
            return if (product?.availability != null) product.availability.en else ""
        }

    @get:Bindable
    val productBeforePrice: String
        get() {
            if (product?.beforePrice == 0f) return ""
            return if (sharedPref.language.equals(
                    "ar",
                    ignoreCase = true
                )
            ) NumberFormat.getInstance()
                .format(product?.beforePrice?.toDouble()) + " " + activity.getString(R.string.currency) else activity.getString(
                R.string.currency
            ) + " " + NumberFormat.getInstance().format(product?.beforePrice?.toDouble())
        }

    fun v(): View.OnClickListener {
        return View.OnClickListener { v: View? -> }
    }

    fun showClearCartDialog(isFromOrderNow: Boolean) {
        activity.showClearCartDialog(sharedPref.cart.placeModel?.name?.en, this)
    }

    fun setOrderNowViewModel(orderNowViewModel: OrderNowViewModel?) {}

    fun opendDeliveryAreaActivity(): View.OnClickListener {
        return label@ View.OnClickListener { v: View ->

            if (product?.placeInfo == null || product?.placeInfo?._id == null) {
                logging(
                    activity, Constants.USER_PRODUCT_ORDER_FAIL, LEVEL_ERROR,
                    "User ordering an product action failed",
                    "product's place is null or place's id is null " + product?.placeInfo?._id
                )
                return@OnClickListener
            }

            logging(
                activity, Constants.USER_PRODUCT_ORDER_SUCCESS, LEVEL_INFO,
                "User ordering an product action success", ""
            )
            val cart = sharedPref.getCart(product?.placeInfo?._id)
            if (sharedPref?.cart != null && cart == null) {
                (v.context as BaseActivity).showClearCartDialog(
                    sharedPref.cart.placeModel?.name?.en,
                    this
                )
                return@OnClickListener
            }
            if (activity is PlaceDetailsActivity &&
                sharedPref.selectedCachedArea.case > 1
            ) {
                //not supported
                activity.showUnSupportedAreaDialog(
                    {}, (product?.placeInfo?.name?.en
                            + " " + activity.getString(R.string.not_deliver_hint) + " "
                            + sharedPref.selectedCachedArea.area?.name?.en)
                )
                return@OnClickListener
            }
            navigateToCustomizeProductActivity()
        }
    }

    fun openPlaceDetails(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val intent: Intent = Intent(activity, PlaceDetailsActivity::class.java)
            if ((SharedPreferencesManager.getInstance().language == "en")) {
                intent.putExtra("place_name", product?.placeInfo?.name?.en)
            } else {
                intent.putExtra("place_name", product?.placeInfo?.name?.ar)
            }
            intent.putExtra("unique_name", product?.placeInfo?.uniqueName)
            intent.putExtra("place_id", product?.placeInfo?._id)
            //            intent.putExtra("product-tab-name", product.getPlaceInfo().getCategories().get(0).getProductsTapName());
            intent.putExtra(Constants.ORDER_TYPE, Constants.PRODUCTS)
            intent.putExtra(Constants.PLACE, Gson().toJson(product?.placeInfo))
            activity.startActivity(intent)
        }
    }

    private fun navigateToCustomizeProductActivity() {
        val intent: Intent
        if (product?.isHassections == true || !product?.sections.isNullOrEmpty()) {
            intent = Intent(QurbaApplication.getContext(), CustomizProductActivity::class.java)
            intent.putExtra(Constants.PRODUCTS, Gson().toJson(product))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            QurbaApplication.getContext().startActivity(intent)
        } else {
            product?.hashKey = product?._id
            addProductToCart(product!!)
        }
    }

    fun likeDisLikeProduct(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            if (SharedPreferencesManager.getInstance().token == null || SharedPreferencesManager.getInstance().isGuest) {
                activity.showLoginDialog()
            } else {
                v?.isEnabled = false
                if (!isLiked.get()!!) {
                    addFavoriteOffer(product?._id, v)
                } else {
                    removeFavoriteOffer(product?._id, v)
                }
            }
        }
    }

    fun shareProduct(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            shareProduct(product?._id)
            val sendIntent: Intent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                product?.shareLink
            )
            sendIntent.type = "text/plain"
            activity.startActivity(sendIntent)
        }
    }

    fun commentClick(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            val commentsPopupFragment: OverlayFragment = OverlayFragment()
            val bundle: Bundle = Bundle()
            bundle.putString("product_id", product?._id)
            bundle.putString("product_name", product?.name?.en)
            product?.likesCount?.let { bundle.putInt("like_count", it) }
            product?.sharesCount?.let { bundle.putInt("share_count", it) }
            commentsPopupFragment.setAddCommentCallBack(addCommentCallBack)
            commentsPopupFragment.arguments = bundle
            commentsPopupFragment.show(
                (v.context as BaseActivity).supportFragmentManager,
                "CommentsPopupFragment"
            )
        }
    }

    fun addComment(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            if (sharedPref.isGuest) activity.showLoginDialog() else {
                isProgress = true
                if (isEdit) {
                    updateProductComment()
                } else {
                    addProductComment()
                }
            }
        }
    }

    fun opendProductDetailsActivity(position: Int): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val intent: Intent = Intent(activity, ProductDetailsActivity::class.java)
            intent.putExtra(Constants.PRODUCT_ID, product?._id)
            intent.putExtra("favorite", product?.isProductLikedByUser)
            SharedPreferencesManager.getInstance().productPosition = position
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, 1000)
        }
    }

    fun addFavoriteOffer(id: String?, v: View?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.instance.likeProduct(id)
            product?.isProductLikedByUser = true
            isLiked.set(true)
            product?.likesCount = product?.likesCount!! + 1
            likeCount.set(product?.getLocalizedLikesCount(activity))
            isHaveLikesCount.set(product?.likesCount > 0)
            setCountsTxt()
            likeProductSubscriber = object : Subscriber<Response<LikeProductPayload>>() {
                override fun onCompleted() {
                    v?.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    product.isProductLikedByUser = false
                    isLiked.set(false)
                    product.likesCount = product.likesCount - 1
                    likeCount.set(product?.getLocalizedLikesCount(activity))
                    isHaveLikesCount.set(product.likesCount > 0)
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        activity.getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNext(response: Response<LikeProductPayload>) {
                    if (response.code() != 200) {
                        product.isProductLikedByUser = false
                        isLiked.set(false)
                        product.likesCount = product.likesCount - 1
                        likeCount.set(product?.getLocalizedLikesCount(activity))
                        isHaveLikesCount.set(product.likesCount > 0)
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
                .subscribe(likeProductSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                activity.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeFavoriteOffer(id: String?, v: View?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.instance.dislikeProduct(id)
            product?.isProductLikedByUser = false
            isLiked.set(false)
            product?.likesCount = product?.likesCount!! - 1
            likeCount.set(product?.getLocalizedLikesCount(activity))
            isHaveLikesCount.set(product.likesCount > 0)
            setCountsTxt()
            likeProductSubscriber = object : Subscriber<Response<LikeProductPayload>>() {
                override fun onCompleted() {
                    v?.isEnabled = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        activity.getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    product.isProductLikedByUser = true
                    isLiked.set(true)
                    product.likesCount = product.likesCount + 1
                    likeCount.set(product?.getLocalizedLikesCount(activity))
                    isHaveLikesCount.set(product.likesCount > 0)
                    setCountsTxt()
                }

                override fun onNext(response: Response<LikeProductPayload>) {
                    if (response.code() != 200) {
                        product.isProductLikedByUser = true
                        isLiked.set(true)
                        product.likesCount = product.likesCount + 1
                        likeCount.set(product?.getLocalizedLikesCount(activity))
                        isHaveLikesCount.set(product.likesCount > 0)
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
                .subscribe(likeProductSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                activity.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun shareProduct(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.instance.shareProduct(id)
            product?.sharesCount = product?.sharesCount!! + 1
            isHaveSharesCount.set(product.sharesCount > 0)
            sharesCount.set(product.localizedSharesCount + " " + activity.getString(R.string.shares))
            val addFavoriteSubscriber: Subscriber<Response<StringOnlyResponse>> =
                object : Subscriber<Response<StringOnlyResponse>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        product.sharesCount = product.sharesCount - 1
                        isHaveSharesCount.set(product.sharesCount > 0)
                        sharesCount.set(product.localizedSharesCount + " " + activity.getString(R.string.shares))
                    }

                    override fun onNext(response: Response<StringOnlyResponse>) {
                        if (response.code() == 200) {
                            //notifyItemChanged(position);
                        } else {
//                        Toast.makeText(QurbaApplication.getContext(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                            product.likesCount = product.sharesCount - 1
                            isHaveSharesCount.set(product.sharesCount > 0)
                            sharesCount.set(
                                product.localizedSharesCount + " " + activity.getString(
                                    R.string.shares
                                )
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

    fun commentLongPress(): OnLongClickListener {
        return OnLongClickListener { v: View ->
            if (sharedPref.user != null && product?.recentComment?.user?._id.equals(
                    sharedPref.user._id,
                    ignoreCase = true
                )
            ) {
                val overlayFragment: EditDeleteOverlayFragment = EditDeleteOverlayFragment()
                overlayFragment.setCommentActionEvents((commentActionEvents)!!)
                val bundle: Bundle = Bundle()
                bundle.putSerializable(Constants.COMMENTS, product?.recentComment)
                overlayFragment.arguments = bundle
                overlayFragment.show(
                    (v.context as BaseActivity).supportFragmentManager,
                    "EditDeleteOverlayFragment"
                )
            }
            false
        }
    }

    fun replyClickListener(): OnTouchListener {
        return label@ OnTouchListener { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                replyClick(product?.recentComment)
                true
            }
            false
        }
    }

    fun replyClick(commentModel: CommentModel?) {
        val commentsPopupFragment = OverlayFragment()
        val bundle = Bundle()
        bundle.putString("product_id", product?._id)
        bundle.putString("product_name", product?.name?.en)
        product?.likesCount?.let { bundle.putInt("like_count", it) }
        product?.sharesCount?.let { bundle.putInt("share_count", it) }
        bundle.putSerializable(Constants.COMMENTS, commentModel)
        bundle.putString(Constants.COMMENT_ID, commentModel?._id)
        bundle.putBoolean(Constants.IS_REPLY, true)
        commentsPopupFragment.setAddCommentCallBack(addCommentCallBack)
        commentsPopupFragment.arguments = bundle
        commentsPopupFragment.show(activity.supportFragmentManager, "CommentsPopupFragment")
    }

    fun addProductComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            if (!checkInputsValidation()) return
            logging(
                QurbaApplication.getContext(),
                Constants.USER_ADD_PRODUCT_COMMENT_ATTEMPT,
                LEVEL_INFO, "User attempt to add product comment", ""
            )
            val commetnsPayload = CommetnsPayload()
            commetnsPayload.comment = commentText.get().toString()
            val commetnsRequest = CommetnsRequest(commetnsPayload)
            APICalls.instance.addProductComments(product?._id, commetnsRequest)
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
                            Constants.USER_ADD_PRODUCT_COMMENT_SUCCESS,
                            LEVEL_ERROR,
                            "Failed to add product comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<AddCommentPayload>) {
                        if (response.code() == 200) {
                            commentText.set("")
                            addCommentCallBack?.onCommentAdded(
                                response.body()?.payload?.get(
                                    response.body()?.payload!!.size - 1
                                ), false
                            )
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_PRODUCT_COMMENT_SUCCESS,
                                LEVEL_INFO, "Successfully add product comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to add product comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_ADD_PRODUCT_COMMENT_FAIL,
                                LEVEL_ERROR,
                                "Failed to add product comment",
                                e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg
                                    ?: QurbaApplication.getContext()
                                        .getString(R.string.something_wrong), Toast.LENGTH_SHORT
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

    fun updateProductComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            if (!checkInputsValidation()) return
            logging(
                QurbaApplication.getContext(),
                Constants.USER_UPDATE_PRODUCT_COMMENT_ATTEMPT,
                LEVEL_INFO, "User attempt to update product comment", ""
            )
            val commetnsPayload = CommetnsPayload()
            commetnsPayload.comment = commentText.get().toString()
            val commetnsRequest = CommetnsRequest(commetnsPayload)

            val commentModel = CommentModel()
            commentModel.comment = commentText.get().toString()
            commentModel._id = commentId

            APICalls.instance.updateProductComments(product?._id, commentId, commetnsRequest)
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
                            Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to update product comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<UpdateCommentPayload>) {
                        if (response.code() == 200) {
                            commentText.set("")
                            setCommentId("")
                            setEdit(false)
                            addCommentCallBack?.onCommentUpdated(commentModel, false)
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_PRODUCT_COMMENT_SUCCESS,
                                LEVEL_INFO, "Successfully update product comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to update product comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_UPDATE_PRODUCT_COMMENT_FAIL,
                                LEVEL_ERROR,
                                "Failed to update product comment",
                                e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg
                                    ?: QurbaApplication.getContext()
                                        .getString(R.string.something_wrong), Toast.LENGTH_SHORT
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

    fun deleteProductComment() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_DELETE_PRODUCT_COMMENT_ATTEMPT,
                LEVEL_INFO, "User attempt to delete product comment", ""
            )
            APICalls.instance.deleteProductComment(product?._id, commentId)
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
                            Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                            LEVEL_ERROR,
                            "Failed to delete product comment",
                            throwable.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<UpdateCommentPayload>) {
                        if (response.code() == 200 || response.code() == 204) {
                            addCommentCallBack?.onCommentDeleted(false, 1)
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_PRODUCT_COMMENT_SUCCESS,
                                LEVEL_INFO, "Successfully delete product comment", ""
                            )
                            return
                        }
                        var errorMsg: String? = null
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                                LEVEL_ERROR, "Failed to delete product comment", errorMsg
                            )
                        } catch (e: Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_DELETE_PRODUCT_COMMENT_FAIL,
                                LEVEL_ERROR,
                                "Failed to delete product comment",
                                e.stackTrace.toString()
                            )
                        } finally {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                errorMsg
                                    ?: QurbaApplication.getContext()
                                        .getString(R.string.something_wrong), Toast.LENGTH_SHORT
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

    private fun updateCartView() {
        val pagerAgentViewModel = cartViewModel
        var f = 0
        var n = 0
        if (sharedPref.cart == null) {
            if (activity is PlaceDetailsActivity || activity is UserMealsActivity) {
                pagerAgentViewModel.isAddedToCart = false
            } else {
                (activity as HomeActivityKotlin).updateCart()
            }
            return
        }
        for (cartItems in sharedPref.cart.cartItems) {
            f += cartItems.price * cartItems.quantity
            n += cartItems.quantity
        }
        if (activity is PlaceDetailsActivity || activity is UserMealsActivity) {
            pagerAgentViewModel.setQuantityValue(n)
            pagerAgentViewModel.setPriceValue(f)
            val bl = f > 0.0f
            pagerAgentViewModel.isAddedToCart = bl
        } else {
            (activity as HomeActivityKotlin).updateCart()
        }
        if (sharedPref.cart.cartItems.isEmpty()) {
            sharedPref.clearCart()
        }
    }

    override fun clearCart() {
        navigateToCustomizeProductActivity()
    }

    fun setCommentActionEvents(commentActionEvents: CommentActionEvents?) {
        this.commentActionEvents = commentActionEvents
    }

    init {
        setCountsTxt()
        isLiked.set(product?.isProductLikedByUser)
        this.position = position
        isHaveLikesCount.set(product?.likesCount ?: 0 > 0)
        isHaveCommentsCount.set(product?.commentsCount ?: 0 > 0)
        isHaveSharesCount.set(product?.sharesCount ?: 0 > 0)
        likeCount.set(product?.getLocalizedLikesCount(activity))
        commentsCount.set(product?.localizedCommentsCount + " " + activity.getString(R.string.comments))
        sharesCount.set(product?.localizedSharesCount + " " + activity.getString(R.string.shares))
        cartViewModel = ViewModelProvider(activity).get(PagerAgentViewModel::class.java)
        updateCartView()
    }
}
