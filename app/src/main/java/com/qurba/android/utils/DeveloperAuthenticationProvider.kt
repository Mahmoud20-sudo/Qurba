package com.qurba.android.utils

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider
import com.amazonaws.regions.Regions
import com.facebook.appevents.AppEventsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.mazenrashed.logdnaandroidclient.models.Line
import com.qurba.android.BuildConfig
import com.qurba.android.R
import com.qurba.android.network.APICalls
import com.qurba.android.network.QurbaLogger
import com.qurba.android.network.models.LoginFacebookPayload
import com.qurba.android.network.models.response_models.CognitoResponseModel
import com.qurba.android.network.models.response_models.LoginResponseModel
import com.qurba.android.utils.extenstions.showErrorMsg
import com.qurba.android.utils.extenstions.showToastMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class DeveloperAuthenticationProvider(
    accountId: String?,
    identityPoolId: String?,
    region: Regions?
) :
    AWSAbstractCognitoDeveloperIdentityProvider(accountId, identityPoolId, region) {
    // Return the developer provider name which you choose while setting up the
    // identity pool in the &COG; Console
    override fun getProviderName(): String {
        return developerProvider
    }

    // Use the refresh method to communicate with your backend to get an
    // identityId and token.
    override fun refresh(): String {
        // Override the existing token
//        setToken(null)

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)
        if (token == null)
            getCongito()

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.
        update(identityId, token)
        return token ?: ""
    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.
    override fun getIdentityId(): String {

        // Load the identityId from the cache
        identityId = SharedPreferencesManager.getInstance().identityId
        if (identityId == null) {
            // Call to your backend
        } else {
            return identityId
        }

        return ""
    }

    companion object {
        private const val developerProvider = BuildConfig.COGNITO_AUTH
    }

    fun getCongito() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.logging(QurbaApplication.getContext()
                , Constants.USER_GET_CONGITO_ATTEMPT, Line.LEVEL_INFO,
                "User attempt to get congito token")


            val call = APICalls.getInstance().getCognito()
            val subscriber = object : Subscriber<Response<CognitoResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    QurbaLogger.logging(QurbaApplication.getContext()
                        , Line.LEVEL_ERROR, Constants.USER_GET_CONGITO_FAIL,
                        "User failed attempt to get congito token", e.stackTraceToString())

                }

                override fun onNext(response: Response<CognitoResponseModel>) {
                    val responseModel = response.body()
                    if (response.code() == 200) {
                        SharedPreferencesManager.getInstance().identityId = responseModel?.payload?.cognitoIdentityId
                        update(
                            responseModel?.payload?.cognitoIdentityId,
                            responseModel?.payload?.cognitoToken
                        )
                        QurbaLogger.logging(
                            QurbaApplication.getContext(),
                            Constants.USER_GET_CONGITO_SUCCESS, Line.LEVEL_INFO,
                            "User successfully getting congito token ", ""
                        )
                    } else {
                        //Logging here
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_GET_CONGITO_FAIL,
                                "User failed to get his congito "
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
}