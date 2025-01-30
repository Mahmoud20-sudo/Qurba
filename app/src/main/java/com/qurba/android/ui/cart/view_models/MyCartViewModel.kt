package com.qurba.android.ui.cart.view_models

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.BaseModel
import com.qurba.android.network.models.CartModel
import com.qurba.android.network.models.request_models.*
import com.qurba.android.ui.add_edit_address.views.AddAddressActivity
import com.qurba.android.ui.auth.views.SignUpActivity
import com.qurba.android.ui.checkout.views.CheckoutActivity
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Response
import java.lang.ref.WeakReference
import java.text.NumberFormat

class MyCartViewModel(application: Application) : BaseViewModel(application), SocialLoginCallBack {

    private var responseCallback: Response<BaseModel>? = null
    private var isItemsCleared = false
    private var totalPrice = 0
    var note: ObservableField<String?> = ObservableField<String?>("")
    private val sharedPref = SharedPreferencesManager.getInstance()


    var placeName: ObservableField<String?> = ObservableField<String?>("")
    var placeBranch: ObservableField<String?> = ObservableField<String?>("")


    private var quantityJob: Job? = null
    private var deleteJob: Job? = null
    private var noteJob: Job? = null

    private var isLoading = false


    private lateinit var activity: WeakReference<BaseActivity>

    fun setActivity(activity: BaseActivity) {
        this.activity = WeakReference<BaseActivity>(activity)
    }

    fun setCart(cartModel: CartModel){
        checkIfFreeDeliveryCanceled()
        updateCartData()
        placeName.set(cartModel.placeModel?.name?.en + " - " + cartModel.placeModel.branchName.en)
    }

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    @Bindable
    fun isItemsCleared(): Boolean {
        return isItemsCleared
    }

    @Bindable
    fun getTotalPrice(): String? {
        return if (SharedPreferencesManager.getInstance().language.equals(
                "ar",
                ignoreCase = true
            )
        ) NumberFormat.getInstance()
            .format(totalPrice.toLong()) + " " + activity?.get()
            ?.getString(R.string.currency) else activity?.get()?.getString(
            R.string.currency
        ) + " " + NumberFormat.getInstance().format(totalPrice.toLong())
    }

    fun updateCartData() {
        if (sharedPref.cart == null) return
        var totalPrice = 0
        for (offersModel in sharedPref.cart.cartItems) {
            totalPrice += offersModel.quantity * offersModel.price
        }
        note.set(sharedPref?.cart?.specialNote)
        this.totalPrice = totalPrice
        notifyDataChanged()
    }

    fun checkIfFreeDeliveryCanceled() {
        sharedPref.checkIfFreeDeliveryCanceled()
    }

    fun setItemsCleared(itemsCleared: Boolean) {
        isItemsCleared = itemsCleared
        notifyDataChanged()
    }

    fun closeCart(): View.OnClickListener? {
        return View.OnClickListener { v: View ->
            val intent = Intent(v.context, PlaceDetailsActivity::class.java)
            intent.putExtra("unique_name", sharedPref?.cart?.unique_name)
            intent.putExtra("place_id", sharedPref?.cart?.id)
//            intent.putExtra("product-tab-name", cartObj?.placeProductsName)
            intent.putExtra(Constants.ORDER_TYPE, Constants.PRODUCTS)
            //            intent.putExtra(Constants.PLACE, cartObj.getPlaceName());
            intent.putExtra(Constants.IS_ORDERING, true)
            intent.putExtra(Constants.PLACE, Gson().toJson(sharedPref?.cart?.placeModel))
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.get()?.startActivity(intent)
            activity?.get()?.finish()
            logging(
                QurbaApplication.getContext(), Constants.USER_CART_ADD_ITEM_SUCCESS, LEVEL_INFO,
                "User add more items in their cart action success", ""
            )
        }
    }

    fun checkout(): View.OnClickListener? {
        return View.OnClickListener { v: View? ->
            val userDataModel = SharedPreferencesManager.getInstance().user
            if (SharedPreferencesManager.getInstance().token == null || SharedPreferencesManager.getInstance().isGuest) {
                activity?.get()?.showLoginDialog(SocialLoginCallBack { onLoginFinished() })
            } else if (userDataModel.isMobileVerified && userDataModel.mobileNumber.isNotEmpty()) {
                navigateToConfirmScreen()
            } else {
                onLoginFinished()
            }
        }
    }

    fun openPlaceDetails(): View.OnClickListener? {
        return View.OnClickListener { activity?.get()?.finish() }
    }

    fun updateCartSpecialRequest() {
        val cartObj = sharedPref.cart
        cartObj?.specialNote = note?.get()
        SharedPreferencesManager.getInstance().setCart(cartObj)
    }

    fun navigateToConfirmScreen() {
        val intent = Intent(
            getApplication(), if (SharedPreferencesManager.getInstance().selectedCachedArea.label
                == null
            ) AddAddressActivity::class.java else CheckoutActivity::class.java
        )
        intent.putExtra(Constants.PLACE_ID, sharedPref?.cart?.id)
        //                QurbaApplication.currentActivity().finish();
        intent.putExtra(Constants.IS_ORDERING, true)
        activity?.get()?.startActivityForResult(intent, 51120)
        logging(
            QurbaApplication.getContext(), Constants.USER_CART_CHECK_OUT_SUCCESS, LEVEL_INFO,
            "User selecting cart checkout action success", ""
        )
    }

    override fun onLoginFinished() {
        when {
            SharedPreferencesManager.getInstance().user == null -> return
            SharedPreferencesManager.getInstance().user.isMobileVerified -> {
                navigateToConfirmScreen()
            }
            else -> {
                val intent = Intent(getApplication(), SignUpActivity::class.java)
                intent.putExtra(Constants.IS_ORDERING, true)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                QurbaApplication.getContext().startActivity(intent)
                //QurbaApplication.currentActivity().finish();
            }
        }
    }

    fun updateCartItemQty(_id: String, quantity: Int, type: String, callback: (Boolean) -> Unit) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            setLoading(true)

            val cartUpdateModel = CartUpdateModel()
            val cartUpdatePayload = CartUpdatePayload()
            cartUpdatePayload.quantity = quantity
            cartUpdateModel.payload = cartUpdatePayload

            quantityJob?.cancel()

            quantityJob = viewModelScope.launch(Main) {
                try {
                    responseCallback =
                        if (type.equals(Constants.OFFERS, false)) APICalls.instance.updateOffer(
                            _id,
                            cartUpdateModel
                        ) else APICalls.instance.updateProduct(_id, cartUpdateModel)
                } catch (exception: Exception) {
                    callback(false)
                    setLoading(false)
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_UPDATE_CART_ITEM_QTY_FAIL,
                        LEVEL_ERROR,
                        "User failed to update his cart item quantity ",
                        exception.stackTraceToString()
                    )
                }

                setLoading(false)

                if (responseCallback?.isSuccessful == true) {
                    callback(true)
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_UPDATE_CART_ITEM_QTY_SUCCESS, LEVEL_INFO,
                        "User successfully update his cart item quantity ", ""
                    )
                } else {
                    callback(false)
                    responseCallback?.errorBody()?.let {
                        showErrorMsg(
                            it.string(),
                            Constants.USER_UPDATE_CART_ITEM_QTY_FAIL,
                            "User failed to update his cart item quantity "
                        )
                    }
                }
            }

        } else {
            callback(false)
            showNoInternetToast()
        }
    }

    fun deleteItemFromCart(_id: String, type: String, callback: (Boolean) -> Unit) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            deleteJob?.cancel()

            deleteJob = viewModelScope.launch(Main) {
                try {
                    responseCallback = if (type.equals(
                            Constants.OFFERS,
                            false
                        )
                    ) APICalls.instance.deleteOfferFromCart(_id) else APICalls.instance.deleteProductFromCart(
                        _id
                    )
                } catch (exception: Exception) {
                    callback(false)
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_DELETE_ITEM_FROM_CART_SUCCESS, LEVEL_ERROR,
                        "User failed to delete item from his cart ", exception.stackTraceToString()
                    )
                }
                if (responseCallback?.isSuccessful == true) {
                    callback(true)
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_DELETE_ITEM_FROM_CART_SUCCESS, LEVEL_INFO,
                        "User successfully delete his cart ", ""
                    )
                } else {
                    callback(false)
                    responseCallback?.errorBody()?.let {
                        showErrorMsg(
                            it.string(),
                            Constants.USER_DELETE_ITEM_FROM_CART_FAIL,
                            "User failed to delete item from his cart "
                        )
                    }
                }
            }
        } else {
            callback(false)
            showNoInternetToast()
        }
    }

    private fun showNoInternetToast() {
        activity?.get()?.getString(R.string.no_connection)
            ?.let { activity?.get()?.showToastMsg(it) }
    }

    fun updateCartNote(query: String?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
//            if(note?.get().isNullOrEmpty()) return

            noteJob?.cancel()

            updateCartSpecialRequest()

            val cartUpdateModel = CartNoteUpdateModel()
            val cartUpdatePayload = CartNoteUpdatePayload()
            cartUpdatePayload.note = query ?: ""
            cartUpdateModel.payload = cartUpdatePayload

            noteJob = viewModelScope.launch(Main) {
                try {
                    delay(500)
                    responseCallback = APICalls.instance.updateNote(cartUpdateModel)
                } catch (exception: Exception) {
//                    exception.message?.let { activity?.showToastMsg(it) }
                }

                if (responseCallback?.isSuccessful == true) {
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_UPDATE_CART_NOTE_SUCCESS, LEVEL_INFO,
                        "User successfully updating his cart note", ""
                    )
                } else {
                    note.set("")
                    updateCartSpecialRequest()
                    responseCallback?.errorBody()?.let {
                        showErrorMsg(
                            it.string(),
                            Constants.USER_UPDATE_CART_NOTE_FAIL,
                            "User failed to update his cart note "
                        )
                    }
                }
            }
        } else {
            note.set("")
            updateCartSpecialRequest()
            showNoInternetToast()
        }
    }

}
