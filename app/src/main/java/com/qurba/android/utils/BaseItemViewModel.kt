package com.qurba.android.utils

import android.app.Application
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import com.mazenrashed.logdnaandroidclient.models.Line
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.AddAddressPayload
import com.qurba.android.network.models.request_models.AddAddressRequestModel
import com.qurba.android.network.models.request_models.OrderNowRequest
import com.qurba.android.network.models.request_models.VoteRequestModel
import com.qurba.android.ui.home.views.HomeActivityKotlin
import com.qurba.android.ui.offers.views.OfferDetailsActivity
import com.qurba.android.ui.place_details.view_models.PagerAgentViewModel
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.ui.profile.views.UserOffersActivity
import com.qurba.android.utils.extenstions.addOfferRequestData
import com.qurba.android.utils.extenstions.addProductRequestData
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

open class BaseItemViewModel(private val activity: BaseActivity) : ViewModel(), Observable {

    private val propertyChangeRegistry = PropertyChangeRegistry()
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()
    private val sharedPref = SharedPreferencesManager.getInstance()
    private val cartViewModel: PagerAgentViewModel =
        ViewModelProvider(activity).get(PagerAgentViewModel::class.java)
    private var response: Response<BaseModel>? = null

    override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    fun notifyDataChanged() {
        propertyChangeRegistry.notifyCallbacks(this, 0, null)
    }

    fun addOfferToCart(offersModel: OffersModel): Boolean {
        if (SystemUtils.isNetworkAvailable(activity)) {

            if (!sharedPref.checkIfLimitQtyReached(activity, offersModel._id))
                return false

            sharedPref.copyOrUpdate(offersModel)
            updateCartView()

            if (activity is OfferDetailsActivity)
                Toast.makeText(
                    activity,
                    activity.getString(R.string.added_to_cart),
                    Toast.LENGTH_SHORT
                ).show()

            logging(
                activity!!,
                Constants.USER_ADD_OFFER_TO_CART_ATTEMPT, LEVEL_INFO,
                "User trying to add offer to his cart ",
            )

            //lifecycleScope
            activity?.lifecycleScope.launchWhenStarted {
                try {
                    response = APICalls.instance.addOffer(addOfferRequestData(offersModel))
                } catch (exception: Exception) {
                    sharedPref.removeItemFromCart(offersModel._id)
                    updateCartView()
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity,
                        Constants.USER_ADD_OFFER_TO_CART_FAIL, LEVEL_ERROR,
                        "User failed to add to his cart ", exception.message
                    )
                }
                //for ui handling
                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true)
                        logging(
                            activity,
                            Constants.USER_ADD_OFFER_TO_CART_SUCCESS, LEVEL_INFO,
                            "User successfully add offer to his cart ", ""
                        )
                    else {
                        sharedPref.removeItemFromCart(offersModel._id)
                        updateCartView()
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_ADD_OFFER_TO_CART_FAIL,
                                "User failed offer to add to his cart "
                            )
                        }
                    }
                }
            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
            return false
        }
        return true
    }

    fun addProductToCart(productData: ProductData) {
        if (SystemUtils.isNetworkAvailable(activity)) {

            if (!sharedPref.checkIfLimitQtyReached(activity, productData._id))
                return

            sharedPref.copyOrUpdate(productData)
            updateCartView()

            logging(
                activity!!,
                Constants.USER_ADD_PRODUCT_TO_CART_ATTEMPT, Line.LEVEL_INFO,
                "User trying to add product to his cart ",
            )

            //lifecycleScope
            activity?.lifecycleScope?.launchWhenStarted {
                try {
                    response = APICalls.instance.addProduct(addProductRequestData(productData))
                } catch (exception: Exception) {
                    SharedPreferencesManager.getInstance().removeItemFromCart(productData._id)
                    updateCartView()
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        activity!!,
                        Constants.USER_ADD_PRODUCT_TO_CART_FAIL, Line.LEVEL_ERROR,
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
                        SharedPreferencesManager.getInstance().removeItemFromCart(productData._id)
                        updateCartView()
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_ADD_PRODUCT_TO_CART_FAIL,
                                "User failed to product add to his cart "
                            )
                        }
                    }
                }

            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }


    private fun updateCartView() {
        val pagerAgentViewModel = cartViewModel
        var price = 0
        var quant = 0
        if (sharedPref.cart == null) {
            if (activity is PlaceDetailsActivity || activity is UserOffersActivity) {
                pagerAgentViewModel.isAddedToCart = false
            } else {
                (activity as HomeActivityKotlin).updateCart()
            }
            return
        }
        for (cartItems in sharedPref.cart.cartItems) {
            price += cartItems.price * cartItems.quantity
            quant += cartItems.quantity
        }

//        if(quant > 10){
//            activity.showToastMsg(activity.getString(R.string.max_quantity_hint))
//            return false
//        }

        if (activity is PlaceDetailsActivity || activity is UserOffersActivity) {
            pagerAgentViewModel.setQuantityValue(quant)
            pagerAgentViewModel.setPriceValue(price)
            val bl = price > 0.0f
            pagerAgentViewModel.isAddedToCart = bl
        } else if (activity is HomeActivityKotlin) {
            activity.updateCart()
        }
        if (sharedPref.cart.cartItems.isEmpty()) {
            sharedPref.clearCart()
        }
    }

    init {
        cartViewModel.init(activity)
    }
}