package com.qurba.android.ui.products.view_models

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.facebook.appevents.AppEventsConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.adapters.CommentsAdapter
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFacebookEventsLogging
import com.qurba.android.network.models.BaseModel
import com.qurba.android.network.models.CommentModel
import com.qurba.android.network.models.LikeProductPayload
import com.qurba.android.network.models.ProductData
import com.qurba.android.network.models.response_models.ProductDetailsResponseModel
import com.qurba.android.network.models.response_models.StringOnlyResponse
import com.qurba.android.ui.cart.views.CartActivity
import com.qurba.android.ui.comments.views.OverlayFragment
import com.qurba.android.ui.customization.views.CustomizOffersActivity
import com.qurba.android.ui.customization.views.CustomizProductActivity
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.addProductRequestData
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

class ProductDetailsViewModel(application: Application) : BaseViewModel(application),
    ClearCartCallback, CommentsCallBack, SocialLoginCallBack {
    private var productDetailsSubscriber: Subscriber<Response<ProductDetailsResponseModel>>? = null

    private var response: Response<BaseModel>? = null
    var productObservable: MutableLiveData<ProductData>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    @get:Bindable
    var isReadyToOrder: Boolean = false
        get() = field && !isDeliveringToArea
        set(readyToOrder) {
            field = readyToOrder
            notifyDataChanged()
        }
    private var isLoading: Boolean = false
    private var productData: ProductData = ProductData()
    private var likeProductSubscriber: Subscriber<Response<LikeProductPayload>>? = null
    private var activity: BaseActivity? = null
    private var commentsAdapter: CommentsAdapter? = null
    private val sharedPref = SharedPreferencesManager.getInstance()
//    var commentsCount = ObservableField<String>(NumberFormat.getInstance().format(productData.commentsCount.toLong()))

    fun setCommentsAdapter(commentsAdapter: CommentsAdapter?) {
        this.commentsAdapter = commentsAdapter
    }

    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    @Bindable
    fun getShareCounts(): String? {
        return NumberFormat.getInstance().format(productData.sharesCount.toLong())
    }

    @Bindable
    fun isHaveShareCounts(): Boolean {
        return productData.sharesCount > 0
    }

    @Bindable
    fun isHaveCommentsCount(): Boolean {
        return productData.commentsCount > 0
    }

    @Bindable
    fun isHaveLikeCounts(): Boolean {
        return productData.likesCount > 0
    }

    @Bindable
    fun getLikeCounts(): String? {
        return NumberFormat.getInstance().format(productData.likesCount.toLong())
    }

    @Bindable
    fun getCommentsCount(): String? {
        return NumberFormat.getInstance().format(productData.commentsCount.toLong())
    }

    fun getProductDetails(_id: String?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) { //5f6728d266ce422b58272e88 5f7328bee5754600121e7e7d
            setLoading(true)

            val call: Observable<Response<ProductDetailsResponseModel>> =
                APICalls.instance.getProductDetails(_id)
            logging(
                QurbaApplication.getContext(),
                Constants.USER_RETRIEVE_PRODUCT_DETAILS_ATTEMPT,
                Line.LEVEL_INFO, "User trying to retrieve product details", ""
            )
            productDetailsSubscriber =
                object : Subscriber<Response<ProductDetailsResponseModel>>() {
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
                            Constants.USER_RETRIEVE_PRODUCT_DETAILS_FAIL,
                            LEVEL_ERROR,
                            "Failed to retrieve product details",
                            e.stackTrace.toString()
                        )
                    }

                    override fun onNext(response: Response<ProductDetailsResponseModel>) {
                        if (response.code() == 200) {
                            productData = response.body()!!.payload
                            setLoading(false)

                            productObservable!!.postValue(productData)
                            notifyDataChanged()

                            logViewContentEvent(productData)
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_RETRIEVE_PRODUCT_DETAILS_SUCCESS,
                                Line.LEVEL_INFO, "Successfully retrieve product details", ""
                            )
                        } else {
                            if (activity is CustomizProductActivity) activity?.finish()
                            response.errorBody()?.string()?.let {
                                showErrorMsg(
                                    it,
                                    Constants.USER_RETRIEVE_PRODUCT_DETAILS_FAIL,
                                    "Failed to retrieve product details "
                                )
                            }
                        }
                    }
                }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productDetailsSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @get:Bindable
    val name: String
        get() = if (productData.name != null) productData.name.en else ""

    @get:Bindable
    val productBeforePrice: String
        get() = if (productData.beforePrice == 0f) "" else NumberFormat.getInstance()
            .format(productData.beforePrice) + " " + activity?.getString(R.string.currency)

    @get:Bindable
    val isDiscountOnMenu: Boolean
        get() = productData.isDiscountOnMenu

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    @get:Bindable
    val isLikedByUser: Boolean
        get() = productData.isProductLikedByUser

    @get:Bindable
    val isHaveOrdersCounts: Boolean
        get() = productData.ordersCount > 0

    @get:Bindable
    val isOpenNow: Boolean
        get() = productData.isPlaceOpen

    @get:Bindable
    val isDeliveringToArea: Boolean
        get() = productData.isDeliveringToArea

    @get:Bindable
    val openingTime: String
        get() {
            if (productData.placeInfo == null) return ""
            return if (productData.placeInfo.message != null) productData.placeInfo.message.en else ""
        }

    @Bindable
    fun getAvailability(): String? {
        return if (productData.availability != null) productData.availability.en else ""
    }

    @get:Bindable
    val ordersCounts: String
        get() = NumberFormat.getInstance().format(productData.ordersCount)

    @get:Bindable
    val description: String
        get() = if ((productData.description == null
                    || productData.description.en == null)
        ) "" else productData.description.en

    @get:Bindable
    val isMainProduct: Boolean
        get() {
            return productData.isMainProduct
        }

    @get:Bindable
    val isHasImage: Boolean
        get() {
            return productData.imageURL != null
        }

    @get:Bindable
    val isOrderable: Boolean
        get() {
            return productData.isOrderable
        }

    @get:Bindable
    val isHaveSections: Boolean
        get() {
            return productData.sections == null || productData.sections.isEmpty()
        }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    @get:Bindable
    val price: String
        get() {
            return NumberFormat.getInstance().format(productData.price) + " " + activity?.getString(
                R.string.currency
            )
        }

    @get:Bindable
    val isVariablePrice: Boolean
        get() {
            return productData.pricing == null || productData.pricing.equals(
                "fixed",
                ignoreCase = true
            )
        }

    @get:Bindable
    val placeAndBranchName: String
        get() {
            if (productData.placeInfo == null) return ""
            if ((SharedPreferencesManager.getInstance().language == "en")) return productData.placeInfo.name.en + " - " + productData.placeInfo.branchName.en
            return productData.placeInfo.name.ar + " - " + productData.placeInfo.branchName.ar
        }

    @get:Bindable
    val placeCategory: String?
        get() {
            return productData?.category?.name?.en
        }

    fun openPlaceDetailsActivity(): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent(getApplication(), PlaceDetailsActivity::class.java)
            if ((SharedPreferencesManager.getInstance().language == "en")) {
                intent.putExtra(
                    "place_name",
                    productData.placeInfo.name.en + " - " + productData.placeInfo.branchName.en
                )
            } else {
                val putExtra = intent.putExtra(
                    "place_name", (productData.placeInfo.name.ar + " - " +
                            productData.placeInfo.branchName.ar)
                )
            }
            intent.putExtra("unique_name", productData.placeInfo.uniqueName)
            intent.putExtra("place_id", productData.placeInfo._id)
            intent.putExtra(Constants.PLACE, Gson().toJson(productData.placeInfo))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            getApplication<Application>().startActivity(intent)
        }
    }

    fun opendDeliveryAreaActivity(): View.OnClickListener {
        return View.OnClickListener { v: View ->

            if (productData?.placeInfo == null || productData?.placeInfo?._id == null) {
                logging(
                    QurbaApplication.getContext(), Constants.USER_PRODUCT_ORDER_FAIL, LEVEL_ERROR,
                    "User ordering an product action failed",
                    "product's place is null or place's id is null " + productData?.placeInfo?._id
                )
                return@OnClickListener
            }

            val cart = sharedPref.getCart(productData?.placeInfo?._id)
            if (sharedPref?.cart != null && cart == null) {
                (v.context as BaseActivity).showClearCartDialog(
                    sharedPref.cart.placeModel?.name?.en,
                    this
                )
                return@OnClickListener
            }
            //            if (realmController.getCart() != null && realmController.getCart(productData.getPlaceInfo().get_id()) != null) {
//                //realmController.copyOrUpdate(offersModel);
//                navigateToCustomizeOfferActivity();
//                return;
//            }
            navigateToCustomizeProductActivity()
        }
    }

    override fun clearCart() {
        sharedPref?.clearCart()
        navigateToCustomizeProductActivity()
    }

    private fun navigateToCustomizeProductActivity() {
        val intent: Intent

        if (!productData.sections.isNullOrEmpty() || !productData.sectionGroups.isNullOrEmpty()) {

            intent = Intent(QurbaApplication.getContext(), CustomizProductActivity::class.java)
            intent.putExtra(Constants.IS_FROM_DETAILS, true)
            intent.putExtra(Constants.PRODUCTS, Gson().toJson(productData))

            //            if (SharedPreferencesManager.getInstance().getLanguage().equals("en")) {
//                intent.putExtra("place_name", productData.getPlaceInfo().getName().getEn());
//            } else {
//                intent.putExtra("place_name", productData.getPlaceInfo().getName().getAr());
//            }
//            intent.putExtra("unique_name", productData.getPlaceInfo().getUniqueName());
//            intent.putExtra("place_id", productData.getPlaceInfo().get_id());
//            intent.putExtra("product-tab-name", productData.getPlaceInfo().getCategories().get(0).getProductsTapName());
//
//            intent.putExtra(Constants.IS_ORDERING, true);
        } else {
            productData.hashKey = productData._id
            if (!sharedPref.checkIfLimitQtyReached(activity, productData._id)) return
            addProductToCart(productData)
            intent = Intent(QurbaApplication.getContext(), CartActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        QurbaApplication.getContext().startActivity(intent)
    }

    fun commentClick(): View.OnClickListener {
        return View.OnClickListener {
            openCommentsOverlay(false, String())
        }
    }

    fun openCommentsOverlay(isReply: Boolean, vararg data: String?) {
        val commentsPopupFragment: OverlayFragment = OverlayFragment()
        val bundle: Bundle = Bundle()
        bundle.putString("product_id", productData._id)
        bundle.putString("product_name", productData.name.en)

//        bundle.putInt("comments-page", page.toInt())
        bundle.putInt("like_count", this.productData.likesCount)
        bundle.putInt("share_count", this.productData.sharesCount)

        bundle.putString(Constants.COMMENT_ID, if (data.isNotEmpty()) data[0] else "")
        bundle.putBoolean(Constants.IS_REPLY, isReply)
        bundle.putString("includeDelimiter", if (data.size > 1) data[1] else "")

        commentsPopupFragment.setAddCommentCallBack(this)
        commentsPopupFragment.arguments = bundle
        commentsPopupFragment.show(activity!!.supportFragmentManager, "CommentsPopupFragment")
    }

    fun shareProduct(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                productData.deepLink
            )
            sendIntent.type = "text/plain"
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(sendIntent)
            shareProduct(productData._id)
        }
    }

    fun likeDisLikeProduct(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            if (SharedPreferencesManager.getInstance().token == null || SharedPreferencesManager.getInstance()
                    .isGuest()
            ) {
                activity!!.showLoginDialog(SocialLoginCallBack { onLoginFinished() })
            } else {
                setActions()
            }
            (v as FloatingActionButton).hide()
            v.show()
            v.invalidate()
            v.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun setActions() {
        if (!productData.isProductLikedByUser) {
            addFavoriteProduct(productData._id)
        } else {
            removeFavoriteProduct(productData._id)
        }
    }

    override fun onCommentAdded(commentModel: CommentModel, isReply: Boolean) {
        if (isReply) this.productData.recentComment =
            commentModel else if (productData.recentComment != null && commentModel._id.equals(
                productData.recentComment._id,
                ignoreCase = true
            )
        ) this.productData.recentComment.recentReply = commentModel

        productData.commentsCount = productData.commentsCount + 1
//        commentsCount.set(productData.commentsCount.toString() + "")
//        isHaveCommentsCount.set(productData.commentsCount > 0)

        notifyDataChanged()
    }

    override fun onCommentUpdated(commentModel: CommentModel, isReply: Boolean) {

        //in case of updating other comment rather than recent comment
        if (productData.recentComment == null || !commentModel.get_id()
                .equals(productData.recentComment._id, ignoreCase = true)
        ) return

        if (isReply) {
            productData.recentComment.recentReply.comment = commentModel.comment
        } else {
            productData.recentComment.comment = commentModel.comment
        }

        notifyDataChanged()
    }

    fun shareProduct(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call = APICalls.instance.shareProduct(id)
            logging(
                QurbaApplication.getContext(),
                Constants.USER_SHARE_PRODUCT_ATTEMPT,
                LEVEL_INFO, "User trying to share product", ""
            )
            val addFavoriteSubscriber = object : Subscriber<Response<StringOnlyResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(response: Response<StringOnlyResponse>) {
                    if (response.code() == 200) {
                        productData.sharesCount = productData.sharesCount + 1
                        notifyDataChanged()

                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_SHARE_PRODUCT_SUCCESS,
                            LEVEL_INFO, "User successfully share product", ""
                        )
                    } else {
                        try {
                            val error = response.errorBody()!!.string()
                            val jObjError = JSONObject(error)
                            val errorMsg = jObjError.getJSONObject("error")["en"].toString()
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_SHARE_PRODUCT_FAIL,
                                LEVEL_ERROR, "Failed to share product", errorMsg
                            )
                            //                            Toast.makeText(QurbaApplication.getContext(), errorMsg
//                                    , Toast.LENGTH_LONG).show();
                        } catch (e: java.lang.Exception) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_SHARE_PRODUCT_FAIL,
                                LEVEL_ERROR, "Failed to share product", e.stackTrace.toString()
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


    override fun onCommentDeleted(isReply: Boolean, deletedCommentsount: Int) {
        if (isReply) this.productData.recentComment.recentReply =
            null else this.productData.recentComment = null

        productData.commentsCount = productData.commentsCount - 1
//        commentsCount.set(productData.commentsCount.toString() + "")
//        isHaveCommentsCount.set(productData.getCommentsCount() > 0)

        notifyDataChanged()
    }

    private fun addFavoriteProduct(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call: Observable<Response<LikeProductPayload>> = APICalls.instance.likeProduct(id)
            productData.likesCount = productData.likesCount + 1
            productData.isProductLikedByUser = true

            notifyDataChanged()
            likeProductSubscriber = object : Subscriber<Response<LikeProductPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(response: Response<LikeProductPayload>) {
                    if (response.code() != 200) {
                        productData.likesCount = productData.likesCount - 1

                        productData.isProductLikedByUser = false
                        notifyDataChanged()
                        Toast.makeText(
                            QurbaApplication.getContext(),
                            activity!!.getString(R.string.something_wrong),
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
                activity!!.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeFavoriteProduct(id: String?) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            val call: Observable<Response<LikeProductPayload>> =
                APICalls.instance.dislikeProduct(id)
            productData.likesCount = productData.likesCount - 1
            productData.isProductLikedByUser = false

            notifyDataChanged()
            likeProductSubscriber = object : Subscriber<Response<LikeProductPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNext(response: Response<LikeProductPayload>) {
                    if (response.code() != 200) {
                        productData.likesCount = productData.likesCount + 1
                        productData.isProductLikedByUser = true

                        notifyDataChanged()
                        Toast.makeText(
                            QurbaApplication.getContext(),
                            activity!!.getString(R.string.something_wrong),
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
                activity!!.getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onLoginFinished() {
        setActions()
    }

    fun addProductToCart(productData: ProductData) {
        if (SystemUtils.isNetworkAvailable(activity)) {

            sharedPref.copyOrUpdate(productData)

            logging(
                activity!!,
                Constants.USER_ADD_PRODUCT_TO_CART_ATTEMPT, LEVEL_INFO,
                "User trying to add product to his cart ",
            )

            //lifecycleScope
            activity?.lifecycleScope?.launchWhenStarted {
                try {
                    response = APICalls.instance.addProduct(addProductRequestData(productData))
                } catch (exception: Exception) {
                    SharedPreferencesManager.getInstance().removeItemFromCart(productData._id)
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity!!,
                        Constants.USER_ADD_PRODUCT_TO_CART_FAIL, LEVEL_ERROR,
                        "User failed to add product to his cart ", exception.message
                    )
                }
                //for ui handling
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true)
                        logging(
                            activity!!,
                            Constants.USER_ADD_PRODUCT_TO_CART_SUCCESS, LEVEL_INFO,
                            "User successfully add product to his cart ", ""
                        )
                    else {
                        val error: String? = response?.errorBody()?.string()
                        JSONObject(error)["en"].toString()?.let { activity?.showToastMsg(it) }
                        logging(
                            activity!!,
                            Constants.USER_ADD_PRODUCT_TO_CART_FAIL,
                            LEVEL_ERROR,
                            "User failed to add product to his cart ",
                            JSONObject(error)["en"].toString()
                        )
                    }
                }

            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

    fun logViewContentEvent(product: ProductData) {
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Constants.PRODUCTS)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, product._id)
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP")
        standardFacebookEventsLogging(
            QurbaApplication.getContext(),
            AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
            product.price.toDouble(),
            params
        )
    }

}
