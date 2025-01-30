package com.qurba.android.ui.places.view_models

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.response_models.CategoriesResponseModel
import com.qurba.android.network.models.response_models.GuestLoginResponseModel
import com.qurba.android.utils.*
import retrofit2.Response
import rx.Subscriber
import rx.schedulers.Schedulers
import java.io.IOException

class FilterViewModel(application: Application) : BaseViewModel(application) {

    fun setActivity(activity: BaseActivity) {
        this.activity = activity
    }

    private lateinit var activity: BaseActivity
    private lateinit var subscriber: Subscriber<Response<GuestLoginResponseModel>>
    private var resgisterTokenSubscriber: Subscriber<Response<Void>>? = null
    private var categoriesObservable: MutableLiveData<CategoriesResponseModel>? = null
    private var isLoading = false

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    fun getPlaceCategories() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            setLoading(true)
            logging(QurbaApplication.getContext(),
                    Constants.USER_GET_CATEGORIES_ATTEMPT,
                    LEVEL_INFO, "User attempt to retrieve place categories ", "")
            val call = APICalls.instance.placeCategories
            val subscriber = object : Subscriber<Response<CategoriesResponseModel>>() {
                override fun onCompleted() {
                    setLoading(false)
                }
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(getApplication<Application>().applicationContext,
                            Constants.USER_GET_CATEGORIES_FAIL, LEVEL_ERROR,
                            "User failed to retrieve place categories ", Log.getStackTraceString(e))
                }

                override fun onNext(response: Response<CategoriesResponseModel>) {
                    if (response.code() == 200) {
                        SharedPreferencesManager.getInstance().placeCatgories = response.body()!!.payload
                        getCategoriesObservable()?.postValue(response.body())
                        logging(getApplication<Application>().applicationContext,
                                Constants.USER_GET_CATEGORIES_SUCCESS, LEVEL_INFO,
                                "User failed to retrieve place categories ", "")
                    } else {
                        try {
                            logging(getApplication<Application>().applicationContext,
                                    Constants.USER_GET_CATEGORIES_FAIL, LEVEL_ERROR,
                                    "User failed to retrieve place categories ", response.errorBody()!!.string())
                        } catch (e: IOException) {
                            logging(getApplication<Application>().applicationContext,
                                    Constants.USER_GET_CATEGORIES_FAIL, LEVEL_ERROR,
                                    "User failed to retrieve place categories ", Log.getStackTraceString(e))
                            e.printStackTrace()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                    .subscribe(subscriber)
        }
    }

    fun getCategoriesObservable(): MutableLiveData<CategoriesResponseModel>? {
        if (categoriesObservable == null) {
            categoriesObservable = MutableLiveData()
        }
        return categoriesObservable
    }


}