package com.qurba.android.ui.auth.view_models

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.jaychang.sa.AuthCallback
import com.jaychang.sa.SocialUser
import com.jaychang.sa.google.SimpleAuth.connectGoogle
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.BuildConfig
import com.qurba.android.R
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.CognitoClient
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.QurbaLogger.Companion.standardFacebookEventsLogging
import com.qurba.android.network.models.LoginFacebookPayload
import com.qurba.android.network.models.UserDataModel
import com.qurba.android.network.models.request_models.PushNotificationAuthModel
import com.qurba.android.network.models.request_models.PushNotificationsPayload
import com.qurba.android.network.models.response_models.LoginResponseModel
import com.qurba.android.ui.auth.views.LoginPopupFragment
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.showErrorMsg
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.UserAttributes
import io.intercom.android.sdk.identity.Registration
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import com.qurba.android.utils.SharedPreferencesManager

import com.qurba.android.ui.profile.view_models.SettingsViewModel

import com.qurba.android.utils.BaseActivity

import androidx.lifecycle.ViewModelProvider




class LoginPopUpViewModel(application: Application) : BaseViewModel(application) {
    private var subscriber: Subscriber<Response<LoginResponseModel>>? = null
    var sheetFragment: LoginPopupFragment? = null
    private var socialLoginCallBack: SocialLoginCallBack? = null
    fun setSocialLoginCallBack(socialLoginCallBack: SocialLoginCallBack?) {
        this.socialLoginCallBack = socialLoginCallBack
    }

    fun loginByGoogle(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            Log.e("E", BuildConfig.TAG)
            val scopes =
                Arrays.asList("https://www.googleapis.com/auth/plus.login")
            connectGoogle(scopes, object : AuthCallback {
                override fun onSuccess(socialUser: SocialUser) {
                    sendGooogleLogInRequest(v.context, socialUser.accessToken)
                    Log.e("Email", socialUser.accessToken)
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_GOOGLE_LOGIN_SUCCESS,
                        LEVEL_INFO,
                        "User login using Google account has returned a success response from Google with access token"
                                + socialUser.accessToken,
                        ""
                    )
                    if (sheetFragment != null) sheetFragment!!.dismissAllowingStateLoss()
                }

                override fun onError(error: Throwable) {
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_GOOGLE_LOGIN_ERROR,
                        LEVEL_ERROR,
                        "User login using Google account has returned a error response from Google ",
                        error.localizedMessage
                    )
                    Log.e("ERROR", error.localizedMessage)
                }

                override fun onCancel() {
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_GOOGLE_LOGIN_CANCELED,
                        LEVEL_ERROR, "User cancelled login using Google account", ""
                    )
                }
            })
        }
    }

    fun chatWithUs(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            val uri = Uri.parse("fb://messaging/822021011234446")
            val toMessenger = Intent(Intent.ACTION_VIEW, uri)
            try {
                v.context.startActivity(toMessenger)
            } catch (ex: ActivityNotFoundException) {
                v.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/messages/t/822021011234446")
                    )
                )
            }
        }
    }

    fun loginViaFaceBook(): View.OnClickListener {
        return View.OnClickListener { v: View? -> }
    }

    fun sendLogInRequest(context: Context, accessToken: String) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            val payload = LoginFacebookPayload()
            payload.access_token = accessToken
            logging(
                QurbaApplication.getContext(),
                Constants.USER_FACEBOOK_LOGIN_API_CALL,
                LEVEL_INFO, "User login using Facebook account has call api", accessToken
            )
            val call = getInstance().loginUserFacebook(payload)
            subscriber = object : Subscriber<Response<LoginResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        context,
                        Constants.USER_FACEBOOK_LOGIN_BACKEND_FAIL,
                        LEVEL_ERROR,
                        "User login by Facebook account has returned a fail response from backend side API ",
                        Log.getStackTraceString(e)
                    )
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNext(response: Response<LoginResponseModel>) {
                    val loginResponseModel = response.body()
                    if (response.code() == 200) {
                        if (loginResponseModel!!.type == "ERROR") {
                            logging(
                                context,
                                Constants.USER_FACEBOOK_LOGIN_BACKEND_FAIL,
                                LEVEL_ERROR,
                                "User login by Facebook account has returned a fail response from backend side API ",
                                if (loginResponseModel.errorModel != null) loginResponseModel.errorModel.en else ""
                            )
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext()
                                    .getString(R.string.wrong_email_or_password),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val userDataModel = loginResponseModel.payload
                            SharedPreferencesManager.getInstance().token = userDataModel.jwt
                            SharedPreferencesManager.getInstance().user = userDataModel
                            SharedPreferencesManager.getInstance().isGuest = false
                            //                            SharedPreferencesManager.getInstance().setOrdering(true);
                            Toast.makeText(
                                context,
                                context.getString(R.string.login_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            registerToken()
                            FirebaseAnalytics.getInstance(QurbaApplication.getContext())
                                .setUserId(userDataModel._id)
                            if (sheetFragment != null) sheetFragment!!.dismissAllowingStateLoss()
                            if (socialLoginCallBack != null) socialLoginCallBack!!.onLoginFinished()
                            logging(
                                context,
                                Constants.USER_FACEBOOK_LOGIN_BACKEND_SUCCESS,
                                LEVEL_INFO,
                                "User login by Facebook account has returned a success response from backend side API",
                                ""
                            )
                            val params = Bundle()
                            params.putString(
                                AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD,
                                Constants.LOGIN_FACEBOOK
                            )
                            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP")
                            standardFacebookEventsLogging(
                                context,
                                AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION,
                                0.0,
                                params
                            )
                            val bundle = Bundle()
                            bundle.putString(
                                FirebaseAnalytics.Param.METHOD,
                                Constants.LOGIN_FACEBOOK
                            )
                            FirebaseAnalytics.getInstance(QurbaApplication.getContext())
                                .logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                            updateInterComeUser(userDataModel)
                            getCart((context as BaseActivity)) { }
                            changeLang(context);
                        }
                    } else {
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_FACEBOOK_LOGIN_BACKEND_FAIL,
                                "User login by Facebook account has returned a fail response from backend side API"
                            )
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun changeLang(context: Context?) {
        val viewModel = ViewModelProvider((context as BaseActivity?)!!).get(
            SettingsViewModel::class.java
        )
        if (viewModel != null) viewModel.changeLanguage(SharedPreferencesManager.getInstance().language)
    }

    fun sendGooogleLogInRequest(context: Context, accessToken: String) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            val payload = LoginFacebookPayload()
            payload.access_token = accessToken
            logging(
                QurbaApplication.getContext(),
                Constants.USER_GOOGLE_LOGIN_API_CALL,
                LEVEL_INFO, "User login using Google account has call api $accessToken", ""
            )
            val call = getInstance().loginUserGoogle(payload)
            subscriber = object : Subscriber<Response<LoginResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                        context,
                        Constants.USER_GOOGLE_LOGIN_BACKEND_FAIL,
                        LEVEL_ERROR,
                        "User login by Google account has returned a fail response from backend side API ",
                        Log.getStackTraceString(e)
                    )
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNext(response: Response<LoginResponseModel>) {
                    val loginResponseModel = response.body()
                    if (response.code() == 200) {
                        if (loginResponseModel!!.type == "ERROR") {
                            logging(
                                context,
                                Constants.USER_GOOGLE_LOGIN_BACKEND_FAIL,
                                LEVEL_ERROR,
                                "User login by Google account has returned a fail response from backend side API ",
                                if (loginResponseModel.errorModel != null) loginResponseModel.errorModel.en else ""
                            )
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext()
                                    .getString(R.string.wrong_email_or_password),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val userDataModel = loginResponseModel.payload
                            SharedPreferencesManager.getInstance().token = userDataModel.jwt
                            SharedPreferencesManager.getInstance().user = userDataModel
                            SharedPreferencesManager.getInstance().isGuest = false
                            //                            SharedPreferencesManager.getInstance().setOrdering(true);
                            FirebaseAnalytics.getInstance(QurbaApplication.getContext())
                                .setUserId(userDataModel._id)
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.METHOD, Constants.LOGIN_GOOGLE)
                            FirebaseAnalytics.getInstance(QurbaApplication.getContext())
                                .logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                            Toast.makeText(
                                context,
                                context.getString(R.string.login_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            registerToken()
                            if (sheetFragment != null) sheetFragment!!.dismissAllowingStateLoss()
                            if (socialLoginCallBack != null) socialLoginCallBack!!.onLoginFinished()
                            logging(
                                context,
                                Constants.USER_GOOGLE_LOGIN_BACKEND_SUCCESS,
                                LEVEL_INFO,
                                "User login by Google account has returned a success response from backend side API",
                                ""
                            )
                            val params = Bundle()
                            params.putString(
                                AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD,
                                Constants.LOGIN_GOOGLE
                            )
                            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP")
                            standardFacebookEventsLogging(
                                context,
                                AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION,
                                0.0,
                                params
                            )
                            updateInterComeUser(userDataModel)
                            changeLang(context);
                        }
                    } else {
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_GOOGLE_LOGIN_BACKEND_FAIL,
                                "User login by Google account has returned a fail response from backend side API"
                            )
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateInterComeUser(userDataModel: UserDataModel) {
        val registration = Registration.create().withUserId(userDataModel._id)
        Intercom.client().registerIdentifiedUser(registration)
        val userAttributes = UserAttributes.Builder()
            .withName(userDataModel.firstName + " " + userDataModel.lastName)
            .withEmail(userDataModel.email)
            .build()
        Intercom.client().updateUser(userAttributes)
        CognitoClient.refresh()
    }

    fun registerToken() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            val pushNotificationAuthModel = PushNotificationAuthModel()
            val payload = PushNotificationsPayload()
            payload.token = SharedPreferencesManager.getInstance().fcmToken
            payload.pushNotificationsPayload = Constants.ANDROID
            payload.appVersion = BuildConfig.VERSION_NAME
            pushNotificationAuthModel.payload = payload
            val call = getInstance().registerUserPushToken(pushNotificationAuthModel)
            val resgisterTokenSubscriber: Subscriber<Response<Void>> =
                object : Subscriber<Response<Void>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(response: Response<Void>) {
                        Log.d("Guestttttttttt", response.code().toString() + "")
                    }
                }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resgisterTokenSubscriber)
        } else {
            Log.d("Guestttttttttt", "noc")
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}