package com.qurba.android.ui.auth.view_models

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.network.APICalls.Companion.getInstance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.BaseModel
import com.qurba.android.network.models.request_models.auth.*
import com.qurba.android.network.models.response_models.LoginResponseModel
import com.qurba.android.ui.add_edit_address.views.AddAddressActivity
import com.qurba.android.ui.checkout.views.CheckoutActivity
import com.qurba.android.utils.*
import com.qurba.android.utils.extenstions.setInentResult
import com.qurba.android.utils.extenstions.showErrorMsg
import org.json.JSONObject
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class VerifyAccountViewModel(application: Application) :
    BaseViewModel(application) {
    private var isLoading = false
    private var baseSubscriber: Subscriber<Response<BaseModel>>? = null
    private var subscriber: Subscriber<Response<LoginResponseModel>>? = null
    var resendObservable: MutableLiveData<String>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set
    private var isOrdering = false
    private var activity: BaseActivity? = null
    private var isEditing = false

    @Bindable
    override fun isLoading(): Boolean {
        return isLoading
    }

    fun setActivity(activity: BaseActivity?) {
        this.activity = activity
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        notifyDataChanged()
    }

    fun setIsOrdering(isOrdering: Boolean) {
        this.isOrdering = isOrdering
    }

    fun setIsEditing(isEditing: Boolean) {
        this.isEditing = isEditing
        notifyDataChanged()
    }

    fun verifyOrderingPhoneCodeRequest(phoneNumber: String?, verificationCode: String?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            setLoading(true)
            val verifyPhoneRequestModel = VerifyPhoneRequestModel()
            val payload = PhoneVerifyPayload()
            payload.mobile = phoneNumber
            payload.code = verificationCode
            verifyPhoneRequestModel.payload = payload
            val call = getInstance().verifyUserMobile(verifyPhoneRequestModel)
            subscriber = object : Subscriber<Response<LoginResponseModel>>() {
                override fun onCompleted() {
                    setLoading(false)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    logging(
                        QurbaApplication.getContext(),
                        Constants.USER_VERIFY_MOBILE_FAIL, LEVEL_ERROR,
                        "User Verifying his Mobile Number in case it was not verified before action fail ",
                        e.localizedMessage
                    )
                }

                override fun onNext(response: Response<LoginResponseModel>) {
                    val loginResponseModel = response.body()
                    if (response.code() == 200) {
//                        if (loginResponseModel.getType().equals("ERROR")) {
//                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
//                                    Constants.USER_VERIFY_MOBILE_FAIL
//                                    , Line.LEVEL_ERROR, "User Verifying his Mobile Number in case it was not verified before action fail",
//                                    loginResponseModel.getErrorModel() != null ? loginResponseModel.getErrorModel().getEn() : "");
//
//                            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.invalid_verification_code), Toast.LENGTH_SHORT).show();
//                        } else {
                        val userDataModel = loginResponseModel!!.payload
                        SharedPreferencesManager.getInstance().token = userDataModel.jwt
                        SharedPreferencesManager.getInstance().user = userDataModel
                        SharedPreferencesManager.getInstance().isGuest = false

                        openCreateOrder()

                        logging(
                            QurbaApplication.getContext(),
                            Constants.USER_VERIFY_MOBILE_SUCCESS,
                            LEVEL_INFO,
                            "Mobile has been successfully verified",
                            ""
                        )
                        //                        }
                    } else {
                        response?.errorBody()?.string()?.let {
                            showErrorMsg(
                                it,
                                Constants.USER_VERIFY_MOBILE_FAIL,
                                "User Verifying his Mobile Number in case it was not verified before action fail"
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

    fun resendOrderingPhoneCodeRequest(phoneNumber: String?, verificationCode: String?) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            setLoading(true)
            val signUpPhoneRequestModel = SignUpPhoneRequestModel()
            val payload = SignUpPhonePayload()
            payload.mobile = phoneNumber
            signUpPhoneRequestModel.payload = payload
            val call = getInstance().addMobileToUser(signUpPhoneRequestModel)
            baseSubscriber = object : Subscriber<Response<BaseModel>>() {
                override fun onCompleted() {
                    setLoading(false)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    setLoading(false)
                }

                override fun onNext(response: Response<BaseModel>) {
                    if (response.code() == 200) {
                        resendObservable!!.postValue("")
                    } else {
                        try {
                            val obj = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                obj.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (t: Throwable) {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext().getString(R.string.something_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseSubscriber)
        } else {
            Toast.makeText(
                QurbaApplication.getContext(),
                QurbaApplication.getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun verifyPhoneCodeRequest(phoneNumber: String?, verificationCode: String) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            setLoading(true)
            val signUpPhoneVerifyRequestModel = SignUpPhoneVerifyRequestModel()
            val payload = SignUpPhoneVerifyPayload()
            payload.mobile = phoneNumber
            payload.mobileVerificationCode = verificationCode
            signUpPhoneVerifyRequestModel.payload = payload
            val call = getInstance().signUpUserPhoneVerify(signUpPhoneVerifyRequestModel)
            subscriber = object : Subscriber<Response<LoginResponseModel>>() {
                override fun onCompleted() {
                    setLoading(false)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(
                        QurbaApplication.getContext(),
                        QurbaApplication.getContext().getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    setLoading(false)
                }

                override fun onNext(response: Response<LoginResponseModel>) {
                    val loginResponseModel = response.body()
                    if (response.code() == 200) {
                        if (loginResponseModel!!.type == "ERROR") {
                            Toast.makeText(
                                QurbaApplication.getContext(),
                                QurbaApplication.getContext()
                                    .getString(R.string.invalid_verification_code),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            openCreateAccount(null, phoneNumber)
                        }
                    } else {
                        Toast.makeText(
                            QurbaApplication.getContext(),
                            QurbaApplication.getContext().getString(R.string.something_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun openCreateAccount(email: String?, phone: String?) {}

    private fun openCreateOrder() {
        if (!isOrdering) {
            activity?.intent?.let { activity?.setInentResult(it) }

            activity!!.finish()
            return
        }

        val intent = Intent(
            getApplication(), if (SharedPreferencesManager.getInstance().selectedCachedArea.label
                == null
            ) AddAddressActivity::class.java else CheckoutActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        activity!!.startActivity(intent)
        activity!!.finish()
    }

    private fun openForgotPassword(verificationCode: String, email: String?, phone: String?) {}
}