package com.qurba.android.utils

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
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
import com.qurba.android.network.models.response_models.LoginResponseModel
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

open class BaseViewModel(application: Application) : AndroidViewModel(application), Observable {

    private var responseCallback: Response<BaseModel>? = null
    private var response: Response<Void>? = null
    private var showLoadingDialog: MutableLiveData<Boolean>? = null
    private var isLoading = false
    private val propertyChangeRegistry = PropertyChangeRegistry()
    private var cartPayload: Response<CartPayload>? = null
    private var userModel: Response<LoginResponseModel>? = null

    open fun showLoadingDialog(): MutableLiveData<Boolean>? {
        if (showLoadingDialog == null) showLoadingDialog = MutableLiveData()
        return showLoadingDialog
    }

    override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        propertyChangeRegistry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        propertyChangeRegistry.add(callback)
    }

    fun notifyDataChanged() {
        propertyChangeRegistry.notifyCallbacks(this, 0, null)
    }

    @Bindable
    open fun isLoading(): Boolean {
        return isLoading
    }

    open fun voteArea(addAddressModel: AddAddressModel?, vararg placeId: String?) {
        var addAddressModel = addAddressModel
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            SharedPreferencesManager.getInstance().setAreaVoting(true)
            if (addAddressModel == null) addAddressModel =
                SharedPreferencesManager.getInstance().getSelectedCachedArea()
            if (addAddressModel?.case == 1 || addAddressModel?.case == 0) //vote only for case 2 and 3
                return
            val orderNowRequest = OrderNowRequest()
            orderNowRequest.country =
                if (addAddressModel?.country!!._id == null) addAddressModel?.country!!.name.en else addAddressModel?.country!!._id
            orderNowRequest.city =
                if (addAddressModel?.city!!._id == null) addAddressModel?.city!!.name.en else addAddressModel?.city!!._id
            orderNowRequest.area =
                if (addAddressModel?.area!!._id == null) addAddressModel?.area!!.name.en else addAddressModel?.area!!._id
            orderNowRequest.case = addAddressModel.case
            orderNowRequest.place = if (placeId.size == 0) null else placeId[0]
            if (addAddressModel?.area!!.location != null) //some area returned without coordinates
                orderNowRequest.coordinates = Coordinates(
                    addAddressModel?.area!!.location.coordinates[1],
                    addAddressModel?.area!!.location.coordinates[0]
                )
            val voteRequestModel = VoteRequestModel()
            voteRequestModel.payload = orderNowRequest
            logging(
                QurbaApplication.getContext(), Constants.USER_VOTE_AREA_ATTEMPT, LEVEL_INFO,
                "User trying to vote for unsupported area", ""
            )
            val call = getInstance().voteArea(voteRequestModel)
            val statsSubscriber: Subscriber<Response<JSONObject>> =
                object : Subscriber<Response<JSONObject>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_VOTE_AREA_FAIL,
                            LEVEL_ERROR,
                            "User failed to vote for unsupported area",
                            e.localizedMessage
                        )
                    }

                    override fun onNext(response: Response<JSONObject>) {
                        if (response.code() == 200) {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_VOTE_AREA_SUCCESS,
                                LEVEL_INFO,
                                "User successfully voting for unsupported area",
                                ""
                            )
                        } else {
                            logging(
                                QurbaApplication.getContext(),
                                Constants.USER_VOTE_AREA_FAIL,
                                LEVEL_ERROR,
                                "User failed to vote for unsupported area",
                                ""
                            )
                        }
                    }
                }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statsSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getCart(activity: BaseActivity, callback: (CartModel?) -> Unit) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            //lifecycleScope
            try {
                CoroutineScope(IO).launch(Main) {

                    cartPayload = APICalls.instance.getCart()

                    if (cartPayload?.isSuccessful == true) {
                        if (cartPayload?.body()?.payload?.cartErrors != null)
                            for (error in cartPayload?.body()?.payload!!.cartErrors)
                                activity.showToastMsg(error.en)

                        if (!cartPayload?.body()?.payload?.offers.isNullOrEmpty()
                            || !cartPayload?.body()?.payload?.products.isNullOrEmpty()
                        ) {
                            SharedPreferencesManager.getInstance()
                                .setCart(cartPayload?.body()?.payload, true)
                        }
                        logging(
                            getApplication<Application>().applicationContext,
                            Constants.USER_GET_CART_SUCCESS, LEVEL_INFO,
                            "User successfully getting his cart ", ""
                        )
                        callback(cartPayload?.body()?.payload)
                    } else {
                        cartPayload?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_GET_CART_FAIL,
                                "User failed to get his cart "
                            )
                        }
                    }
                }
            } catch (exception: Exception) {
//                    exception.message?.let { activity?.showToastMsg(it) }
                logging(
                    getApplication<Application>().applicationContext,
                    Constants.USER_GET_CART_FAIL, LEVEL_ERROR,
                    "User failed to get his cart ", exception.message
                )
            }
        } else {
            activity.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

    fun clearCart(activity: BaseActivity, callback: (Boolean?) -> Unit?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            CoroutineScope(IO).launch(Main) {
                val cart = SharedPreferencesManager.getInstance().getCart();
                SharedPreferencesManager.getInstance().clearCart();

                try {
                    response = APICalls.instance.deleteCart()
                } catch (exception: Exception) {
                    callback(false)
                    SharedPreferencesManager.getInstance().setCart(cart)
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_CLEAR_CART_FAIL, LEVEL_ERROR,
                        "User failed to get his cart ", exception.message
                    )
                }

                if (response?.isSuccessful == true) {
                    callback(true)
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_CLEAR_CART_SUCCESS, LEVEL_INFO,
                        "User failed to get his cart ", ""
                    )

                } else {
                    SharedPreferencesManager.getInstance().setCart(cart)
                    callback(false)
                    response?.errorBody()?.string()?.let {
                        showErrorMsg(
                            it,
                            Constants.USER_CLEAR_CART_FAIL,
                            "User failed to clear his cart "
                        )
                    }
                }
            }
        } else {
            callback(false)
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

    fun updateCartArea(activity: BaseActivity, _id: String, callback: (Boolean?) -> Unit) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            val addressRequestModel = AddAddressRequestModel()
            val addAddressPayload = AddAddressPayload()
            addAddressPayload.area = _id
            addressRequestModel.payload = addAddressPayload

            CoroutineScope(IO).launch(Main) {
                try {
                    cartPayload = APICalls.instance.updateCartArea(addressRequestModel)
                } catch (exception: Exception) {
                    callback(false)
                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_UPDATE_CART_AREA_FAIL, LEVEL_ERROR,
                        "User failed to update his cart area ", exception.stackTraceToString()
                    )
                }

                if (cartPayload?.isSuccessful == true) {
                    callback(true)
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_UPDATE_CART_AREA_SUCCESS, LEVEL_INFO,
                        "User successfully update his cart area ", ""
                    )
                } else {
                    callback(false)
                    cartPayload?.errorBody()?.let {
                        showErrorMsg(
                            it.string(),
                            Constants.USER_UPDATE_CART_AREA_FAIL,
                            "User failed to update his cart area "
                        )
                    }
                }
            }
        } else {
            callback(false)
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

    fun getProfile(activity: BaseActivity, callback: (Boolean?) -> Unit) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            if (SharedPreferencesManager.getInstance().user == null)
                return

            logging(
                QurbaApplication.getContext(),
                Constants.USER_GET_PROFILE_ATTEMPT, LEVEL_INFO,
                "User trying to get his profile ",
            )

            CoroutineScope(IO).launch(Main) {
                try {
                    userModel = APICalls.instance.getProfile()
                } catch (exception: Exception) {
//                    exception.message?.let { activity?.showToastMsg(it) }
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_GET_PROFILE_FAIL, LEVEL_ERROR,
                        "User failed to get his profile ", exception.message
                    )
                }

                if (userModel?.isSuccessful == true) {
                    val userDataModel = userModel?.body()?.payload
//                        SharedPreferencesManager.getInstance().token = userDataModel?.jwt
                    SharedPreferencesManager.getInstance().user = userDataModel
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_LOGIN_GUEST_SUCCESS, LEVEL_INFO,
                        "User successfully get his profile ", ""
                    )
                    callback(true)
                } else
                    userModel?.errorBody()?.string()?.let {
                        showErrorMsg(
                            it,
                            Constants.USER_GET_PROFILE_FAIL,
                            "User failed to get his profile "
                        )
                    }
            }
        } else {
            activity?.showToastMsg(activity!!.getString(R.string.no_connection))
        }
    }

}