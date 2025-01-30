package com.qurba.android.ui.profile.view_models

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_ERROR
import com.mazenrashed.logdnaandroidclient.models.Line.Companion.LEVEL_INFO
import com.qurba.android.R
import com.qurba.android.databinding.DialogUpdateBinding
import com.qurba.android.network.APICalls.Companion.instance
import com.qurba.android.network.QurbaLogger.Companion.logging
import com.qurba.android.network.models.request_models.LanguageModel
import com.qurba.android.network.models.request_models.LanguagePayload
import com.qurba.android.network.models.response_models.GuestLoginResponseModel
import com.qurba.android.utils.*
import com.qurba.android.utils.SystemUtils.restartApp
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SettingsViewModel(application: Application) : BaseViewModel(application),
        Observable {
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    private var subscriber: Subscriber<Response<GuestLoginResponseModel>>? = null
    var guestLoginObservable: MutableLiveData<GuestLoginResponseModel.GuestModel>? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set

    @get:Bindable
    val userEmail: String?
        get() = if (!sharedPreferencesManager?.user?.email.isNullOrEmpty()) {
            sharedPreferencesManager?.user?.email
        } else {
            ""
        }

    @get:Bindable
    val isGuest: Boolean
        get() = sharedPreferencesManager.isGuest

    fun openChangePassword(): View.OnClickListener {
        return View.OnClickListener { v: View? -> }
    }

    fun openChangeEmail(): View.OnClickListener {
        return View.OnClickListener { v: View? -> }
    }

    fun logout(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            SharedPreferencesManager.getInstance().isGuest = true
            SharedPreferencesManager.getInstance().token = null
            SharedPreferencesManager.getInstance().user = null
            restartApp(v.context as BaseActivity)
            FirebaseAuth.getInstance().signOut()
        }
    }

    fun changeLanguage(lang: String) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            val languageModel = LanguageModel()
            val payload = LanguagePayload()
            payload.language = if (lang.equals("ar", ignoreCase = true)) "arabic" else "english"
            languageModel.payload = payload
            logging(
                    QurbaApplication.getContext(),
                    Constants.USER_CHANGE_LANGUAGE_ATTEMPT,
                    LEVEL_INFO, "User trying to change language", ""
            )
            val call = instance.changeLanguage(languageModel)
            subscriber = object : Subscriber<Response<GuestLoginResponseModel>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    logging(
                            QurbaApplication.getContext(),
                            Constants.USER_CHANGE_LANGUAGE_FAIL,
                            LEVEL_ERROR, "User failed to change language", e.localizedMessage
                    )
                }

                override fun onNext(response: Response<GuestLoginResponseModel>) {
                    response.raw().body
                    if (response.code() == 200) {
                        logging(
                                QurbaApplication.getContext(),
                                Constants.USER_CHANGE_LANGUAGE_SUCCESS,
                                LEVEL_INFO, "User successfully change language", ""
                        )
                    } else {
                        logging(
                                QurbaApplication.getContext(),
                                Constants.USER_CHANGE_LANGUAGE_FAIL,
                                LEVEL_ERROR,
                                "User failed to change language",
                                response.errorBody().toString()
                        )
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

    private fun loginGuest(context: Context) {
//        if (SystemUtils.isNetworkAvailable(getApplication())) {
//            GuestAuthModel guestAuthModel = new GuestAuthModel();
//            GuestPayload payload = new GuestPayload();
//            payload.setDeviceId(SystemUtils.getDeviceID(context));
//            guestAuthModel.setPayload(payload);
//            Observable<Response<GuestLoginResponseModel>> call = APICalls.getInstance().loginGuest(guestAuthModel);
//            subscriber = new Subscriber<Response<GuestLoginResponseModel>>() {
//                @Override
//                public void onCompleted() {
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onNext(Response<GuestLoginResponseModel> response) {
//                    com.jaychang.sa.google.SimpleAuth.disconnectGoogle();
//                    LoginManager.getInstance().logOut();
//                    GuestLoginResponseModel guestModel = response.body();
//                    if (response.code() == 200) {
//                        GuestLoginResponseModel.GuestModel guestModelData = guestModel.getPayload();
//                        SharedPreferencesManager.getInstance().setToken(guestModelData.getJwt());
//                        getGuestLoginObservable().postValue(guestModel.getPayload());
//                        SharedPreferencesManager.getInstance().setGuest(true);
//                        SharedPreferencesManager.getInstance().setUser(null);
//
//                        restartApp((BaseActivity) context);
//                    } else {
//                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            };
//            call.subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(subscriber);
//        } else {
//            Log.d("Guestttttttttt", "noc");
//            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
//        }
    }

    fun saveSettingsDialog(activity: BaseActivity, lang: String): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val binding: DialogUpdateBinding =
                DataBindingUtil.inflate(inflater, R.layout.dialog_update, null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        binding.titleTv.text = activity.getString(R.string.save_settings)
        binding.contentTv.text = activity.getString(R.string.restart_app)
        binding.notNowTv.text = activity.getString(R.string.cancel)
        binding.updateNowTv.text = activity.getString(R.string.save)
        binding.notNowTv.setOnClickListener { view -> dialog.dismiss() }
        binding.updateNowTv.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action === MotionEvent.ACTION_DOWN) {
                sharedPreferencesManager.language = lang
                changeLanguage(lang)
                SystemUtils.restartAppWithoutClear(activity)
                return@setOnTouchListener true
            }
            false
        }
        if (dialog.window != null) {
            dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.show()
        return dialog
    }
}
