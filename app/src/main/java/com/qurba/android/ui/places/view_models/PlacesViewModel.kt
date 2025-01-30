package com.qurba.android.ui.places.view_models

import android.app.Application
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.PlaceModel
import com.qurba.android.network.models.response_models.NearbyPlacesResponseModel
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class PlacesViewModel(application: Application) : BaseViewModel(application) {

    val map: MutableMap<String, Any> = HashMap()
    var activity: BaseActivity? = null
        get() {
            if (field == null) {
                field = BaseActivity()
            }
            return field
        }

    var placesObservable: MutableLiveData<List<PlaceModel>>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set
    private var searchOffersObservable: MutableLiveData<List<PlaceModel>>? = null
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    private var isLoading = false

    @get:Bindable
    var isDataFound = false
        set(dataFound) {
            field = dataFound
            notifyDataChanged()
        }

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    private fun setRequestParameters(search: String): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()

        val addAddressModel = sharedPreferencesManager.selectedCachedArea
        val areaId =
            if (addAddressModel.area!!.id == null) addAddressModel.area!!._id else addAddressModel.area!!.id

        if (areaId != null) map["area"] = areaId

        if (!search.isNullOrEmpty())
            map["search"] = search

        if (sharedPreferencesManager.placeFilterModel != null) {
            if (sharedPreferencesManager.placeFilterModel.categoryModels.isNotEmpty()) {
                for (category in sharedPreferencesManager.placeFilterModel.categoryModels)
                    map["category[]"] = category._id
            }
            if (sharedPreferencesManager.placeFilterModel.canDeliver != null) map["can_deliver"] =
                true
            if (sharedPreferencesManager.placeFilterModel.activeNow != null) map["active_now"] =
                true
        }

        return map
    }

    fun getPlaces(page: Int, search: String) {

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_GET_PLACES_ATTEMPT,
                LEVEL_INFO, "User trying to retrieve places list data ", ""
            )

            if (page == 1) {
                isDataFound = true
                setLoading(true)
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = APICalls.instance
                        .getPlaces(page, setRequestParameters(search))

                    if (response.code() == 200) {
                        setLoading(false)
                        val placesResponseModel = response.body()
                        placesObservable?.postValue(placesResponseModel?.payload?.docs)
                        if (placesResponseModel!!.payload.docs.isEmpty() && page == 1) isDataFound =
                            false
                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_GET_PLACES_SUCCESS,
                            LEVEL_INFO, "User able to retrieve places list data ", ""
                        )
                    }
                } catch (e: Exception) {
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_GET_PLACES_FAIL,
                        LEVEL_ERROR, "Failed to retrieve places list data ", e.stackTrace.toString()
                    )
                    activity?.showToastMsg(getApplication<Application>().getString(R.string.something_wrong) + " " + e.localizedMessage)
                }
            }
        } else {
            activity?.showToastMsg(QurbaApplication.getContext().getString(R.string.no_connection))
        }
    }

    fun searchOffers(query: String?) {
//        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
//
//            Observable<Response<SearchOffersResponseModel>> call = APICalls.getInstance().searchOffers(query, true, 1);
//            searchSubscriber = new Subscriber<Response<SearchOffersResponseModel>>() {
//                @Override
//                public void onCompleted() {
//                    setLoading(false);
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    setLoading(false);
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onNext(Response<SearchOffersResponseModel> response) {
//                    setLoading(false);
//                    SearchOffersResponseModel searchOffersResponseModel = response.body();
//                    if (response.code() == 200) {
//                        getOffersSearchObservable().postValue(searchOffersResponseModel.getPayload().getDocs());
//                    } else {
//                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            };
//            call.subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(searchSubscriber);
//        } else {
//            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
//        }
    }

    val offersSearchObservable: MutableLiveData<List<PlaceModel>>?
        get() {
            if (searchOffersObservable == null) {
                searchOffersObservable = MutableLiveData()
            }
            return searchOffersObservable
        }
}