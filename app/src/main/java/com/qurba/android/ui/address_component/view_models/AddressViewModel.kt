package com.qurba.android.ui.address_component.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.models.AddAddressModel
import com.qurba.android.network.models.response_models.NearestAreaResponseModel
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AddressViewModel(application: Application) : BaseViewModel(application) {

    private var adderessResponse: Response<NearestAreaResponseModel>? = null
    private lateinit var subscriber: Subscriber<Response<NearestAreaResponseModel>>
    private var deliveryObservable: MutableLiveData<AddAddressModel>? = null
    private lateinit var activity: BaseActivity

    fun setActivity(activity: BaseActivity) {
        this.activity = activity
    }

    fun getNearestAddress(latLng: LatLng?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            viewModelScope.launch(Main) {
                delay(500)

                QurbaLogger.logging(activity, Constants.USER_RETRIEVE_NEAREST_AREA_ATTEMPT, LEVEL_INFO,
                    "User attempt to retrieve the nearest area ", "");

                try {
                    adderessResponse = APICalls.instance.getNearestLocations(latLng?.latitude.toString() + "", latLng?.longitude.toString() + "")
                } catch (e: Exception) {
                    QurbaLogger.logging(activity, Constants.USER_RETRIEVE_NEAREST_AREA_SUCCESS, LEVEL_ERROR,
                        "User failed to retrieve the nearest area "
                                + e.localizedMessage);
                    activity.showToastMsg(activity.getString(R.string.something_wrong))
                }

                if (adderessResponse?.isSuccessful == true) {
                    val nearestAreaResponseModel = adderessResponse?.body()
                    getDeliveryResponse()?.postValue(nearestAreaResponseModel?.payload)
                    QurbaLogger.logging(activity, Constants.USER_RETRIEVE_NEAREST_AREA_SUCCESS, LEVEL_INFO,
                        "User successfully retrieve the nearest area ", "");

                } else {
                    getDeliveryResponse()?.postValue(null)
                    adderessResponse?.errorBody()?.string()?.let {
                        showErrorMsg(
                            it,
                            Constants.USER_RETRIEVE_NEAREST_AREA_FAIL,
                            "User failed to retrieve the nearest area "
                        )
                    }
                }
            }
        } else {
            activity.showToastMsg(activity.getString(R.string.no_connection))
        }
    }

    fun getDeliveryResponse(): MutableLiveData<AddAddressModel>? {
        if (deliveryObservable == null) {
            deliveryObservable = MutableLiveData()
        }
        return deliveryObservable
    }
}