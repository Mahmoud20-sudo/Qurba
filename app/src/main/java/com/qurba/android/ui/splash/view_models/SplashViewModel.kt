package com.qurba.android.ui.splash.view_models

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.BuildConfig
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.CartPayload
import com.qurba.android.network.models.request_models.GuestAuthModel
import com.qurba.android.network.models.request_models.GuestPayload
import com.qurba.android.network.models.request_models.PushNotificationAuthModel
import com.qurba.android.network.models.request_models.PushNotificationsPayload
import com.qurba.android.network.models.response_models.CategoriesResponseModel
import com.qurba.android.network.models.response_models.DeliveryAreaResponseModel
import com.qurba.android.network.models.response_models.GuestLoginResponseModel
import com.qurba.android.network.models.response_models.LoginResponseModel
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import io.intercom.android.sdk.push.IntercomPushClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.lang.ref.WeakReference


class SplashViewModel(application: Application) : BaseViewModel(application) {

    private var guestModel: Response<GuestLoginResponseModel>? = null
    private var resgisterTokenSubscriber: Subscriber<Response<Void>>? = null

    private var guestLoginObservable: MutableLiveData<GuestLoginResponseModel.GuestModel>? = null

    private lateinit var activity: WeakReference<BaseActivity>

    fun setActivity(activity: BaseActivity) {
        this.activity = WeakReference<BaseActivity>(activity)
    }

    fun loginGuest() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            val guestAuthModel = GuestAuthModel()
            val payload = GuestPayload()
            payload.deviceId = SystemUtils.getDeviceID(activity.get())
            guestAuthModel.payload = payload

            //lifecycleScope
            viewModelScope.launch(Main) {
                try {
                    guestModel = APICalls.instance.loginGuest(guestAuthModel)
                } catch (exception: Exception) {
                    exception.message?.let { activity.get()?.showToastMsg(it) }
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_LOGIN_GUEST_FAIL, LEVEL_ERROR,
                        "User failed to login as a guest ", exception.message
                    )
                }
                if (guestModel?.code() == 200) {
                    val guestModelData = guestModel?.body()?.payload
                    SharedPreferencesManager.getInstance().token = guestModelData?.jwt
                    SharedPreferencesManager.getInstance().isGuest = true
                    SharedPreferencesManager.getInstance().guestModel = guestModelData
                    activity.get()?.let { FirebaseAnalytics.getInstance(it).setUserId(guestModelData?.id) }
                    getDefaultCountry()
                    guestModel?.let { sendFireBaseToken(it.body()!!) }
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_LOGIN_GUEST_SUCCESS, LEVEL_INFO,
                        "User successfully login as a guest ", ""
                    )
                }
                else
                    guestModel?.errorBody()?.let {
                        showErrorMsg(
                            it.string(),
                            Constants.USER_LOGIN_GUEST_FAIL,
                            "User failed to login as a guest "
                        )
                    }
            }

        } else {
            activity?.get()?.getString(R.string.no_connection)?.let {
                activity?.get()?.showToastMsg(it)
            }
        }
    }

    private fun sendFireBaseToken(guestModel: GuestLoginResponseModel) {
//        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
//            if (!it.isSuccessful) {
//                return@addOnCompleteListener
//            }
//            // Get new Instance ID token
//            val token = it.result?.token
//            Log.i("FCM Token", token)
//            registerToken(token)
//            SharedPreferencesManager.getInstance().fcmToken = token
//        }
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful) {
                        return
                    }

                    // Get new FCM registration token
                    val token: String = task.result.toString()

                    // Log and toast
                    Log.i("FCM Token", token)
                    registerToken(token, guestModel)
                    IntercomPushClient().sendTokenToIntercom(getApplication(), token)
                    SharedPreferencesManager.getInstance().fcmToken = token
                }
            })
    }

    private fun getDefaultCountry() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_DEFAULT_COUNTRY_ATTEMPT,
                LEVEL_INFO, "User attempt to retrieve default country ", ""
            )
            val call = APICalls.getInstance().getCountry()
            val subscriber = object : Subscriber<Response<DeliveryAreaResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_DEFAULT_COUNTRY_FAIL, LEVEL_ERROR,
                        "User failed to retrieve default country ", Log.getStackTraceString(e)
                    )
                }

                override fun onNext(response: Response<DeliveryAreaResponseModel>) {
                    if (response.code() == 200) {
                        SharedPreferencesManager.getInstance().defaultCountry =
                            response.body()!!.payload.docs[0]
                        logging(
                            getApplication<Application>().applicationContext,
                            Constants.USER_DEFAULT_COUNTRY_SUCCESS, LEVEL_INFO,
                            "User failed to retrieve default country ", ""
                        )
                    } else {
                        try {
                            logging(
                                getApplication<Application>().applicationContext,
                                Constants.USER_DEFAULT_COUNTRY_FAIL,
                                LEVEL_ERROR,
                                "User failed to retrieve default country ",
                                response.errorBody()!!.string()
                            )
                        } catch (e: IOException) {
                            logging(
                                getApplication<Application>().applicationContext,
                                Constants.USER_DEFAULT_COUNTRY_FAIL,
                                LEVEL_ERROR,
                                "User failed to retrieve default country ",
                                Log.getStackTraceString(e)
                            )
                            e.printStackTrace()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .subscribe(subscriber)
        }
    }

    fun getPlaceCategories() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            logging(
                QurbaApplication.getContext(),
                Constants.USER_GET_CATEGORIES_ATTEMPT,
                LEVEL_INFO, "User attempt to retrieve place categories ", ""
            )
            val call = APICalls.getInstance().placeCategories
            val subscriber = object : Subscriber<Response<CategoriesResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_GET_CATEGORIES_FAIL, LEVEL_INFO,
                        "User failed to retrieve place categories ", Log.getStackTraceString(e)
                    )
                }

                override fun onNext(response: Response<CategoriesResponseModel>) {
                    if (response.code() == 200) {
                        SharedPreferencesManager.getInstance().placeCatgories =
                            response.body()!!.payload
                        logging(
                            getApplication<Application>().applicationContext,
                            Constants.USER_GET_CATEGORIES_SUCCESS, LEVEL_INFO,
                            "User failed to retrieve place categories ", ""
                        )
                    } else {
                        try {
                            logging(
                                getApplication<Application>().applicationContext,
                                Constants.USER_GET_CATEGORIES_FAIL,
                                LEVEL_INFO,
                                "User failed to retrieve place categories ",
                                response.errorBody()!!.string()
                            )
                        } catch (e: IOException) {
                            logging(
                                getApplication<Application>().applicationContext,
                                Constants.USER_GET_CATEGORIES_FAIL,
                                LEVEL_INFO,
                                "User failed to retrieve place categories ",
                                Log.getStackTraceString(e)
                            )
                            e.printStackTrace()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .subscribe(subscriber)
        }
    }

    private fun registerToken(fcmToken: String?, guestModel: GuestLoginResponseModel) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            val pushNotificationAuthModel = PushNotificationAuthModel()
            val payload = PushNotificationsPayload()
            payload.token = fcmToken
            payload.pushNotificationsPayload = Constants.ANDROID
            payload.appVersion = BuildConfig.VERSION_NAME
            pushNotificationAuthModel.payload = payload
            val call = APICalls.getInstance().registerPushToken(pushNotificationAuthModel)
            resgisterTokenSubscriber = object : Subscriber<Response<Void>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        getApplication<Application>().applicationContext,
                        Constants.USER_SEND_FCM_TOKEN_FAIL, LEVEL_INFO,
                        "User failed to send fcm tokn ", Log.getStackTraceString(e)
                    )
                }

                override fun onNext(response: Response<Void>) {
                    Log.d("Guestttttttttt", response.code().toString() + "")
                    getGuestLoginObservable()?.postValue(guestModel.payload)
                    if (response.code() != 200) {
                        logging(
                            getApplication<Application>().applicationContext,
                            Constants.USER_SEND_FCM_TOKEN_FAIL, LEVEL_INFO,
                            "User failed to send fcm tokn ", ""
                        )
                        activity?.get()?.getString(R.string.something_wrong)?.let {
                            activity?.get()?.showToastMsg(it)
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resgisterTokenSubscriber)
        }
    }

    fun getGuestLoginObservable(): MutableLiveData<GuestLoginResponseModel.GuestModel>? {
        if (guestLoginObservable == null) {
            guestLoginObservable = MutableLiveData()
        }
        return guestLoginObservable
    }
}