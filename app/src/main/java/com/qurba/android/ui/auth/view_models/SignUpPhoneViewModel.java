package com.qurba.android.ui.auth.view_models;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.BaseModel;
import com.qurba.android.network.models.request_models.auth.SignUpPhonePayload;
import com.qurba.android.network.models.request_models.auth.SignUpPhoneRequestModel;
import com.qurba.android.network.models.response_models.LoginResponseModel;
import com.qurba.android.ui.auth.views.VerifyAccountActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;
import com.qurba.android.utils.ValidationUtils;

import org.json.JSONObject;

import java.io.IOException;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.qurba.android.utils.extenstions.ExtesionsKt.addFragment;
import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;
import static com.qurba.android.utils.extenstions.ExtesionsKt.showToastMsg;

public class SignUpPhoneViewModel extends BaseViewModel {

    @Bindable
    public String phoneNumber;
    private String phoneError;
    private boolean isLoading;
    private boolean isOrdering;
    private BaseActivity activity;
    private PhoneNumberUtil phoneNumberUtil;

    private Subscriber<Response<LoginResponseModel>> subscriber;
    private Subscriber<Response<BaseModel>> baseSubscriber;

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
        phoneNumberUtil = PhoneNumberUtil.createInstance(activity);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyDataChanged();
    }

    public SignUpPhoneViewModel(@NonNull Application application) {
        super(application);
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isArabic() {
        return SharedPreferencesManager.getInstance().getLanguage().equalsIgnoreCase("ar");
    }

    @Bindable
    public boolean isLoginViewVisible() {
        return SharedPreferencesManager.getInstance().isORdering();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    @Bindable
    public boolean isOrdering() {
        return isOrdering;
    }

    public void setOrdering(boolean ordering) {
        isOrdering = ordering;
        notifyDataChanged();
    }

    private void initialize() throws Exception {
        phoneError = null;
        setPhoneNumber(SharedPreferencesManager.getInstance().getUser() == null ? "" :
                SharedPreferencesManager.getInstance().getUser().getMobileNumber().replace("+20", ""));
    }

    @Bindable
    public String getPhoneError() {
        return phoneError;
    }

    private void setPhoneError(String phoneError) {
        this.phoneError = phoneError;
        notifyDataChanged();
    }

    private void checkInputsValidation() {
        setPhoneError(null);
        try {
            if (phoneNumber.trim().isEmpty() || phoneNumber == null) {
                setPhoneError(getApplication().getString(R.string.required_field));
            } else if (ValidationUtils.validatePhoneLength(phoneNumber.trim()) || !phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phoneNumber, "EG"))) {
                setPhoneError(getApplication().getString(R.string.invalid_phone));
            } else {
                sendMobileRequest();
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
    }

    private void sendMobileRequest() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            setLoading(true);
            SignUpPhoneRequestModel signUpPhoneRequestModel = new SignUpPhoneRequestModel();
            SignUpPhonePayload payload = new SignUpPhonePayload();

            //Toast.makeText(getApplication(), phoneNumber, Toast.LENGTH_LONG).show();

            payload.setMobile(SharedPreferencesManager.getInstance().getUser().getMobileNumber()
                    .equals(phoneNumber) ? "+2" + SharedPreferencesManager.getInstance().getUser().getENMobileNumber()
                    : parsePhoneNumber());
            signUpPhoneRequestModel.setPayload(payload);

            Observable<Response<BaseModel>> call = APICalls.Companion.getInstance().addMobileToUser(signUpPhoneRequestModel);
            baseSubscriber = new Subscriber<Response<BaseModel>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    showToastMsg(activity, activity.getString(R.string.something_wrong));
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_SEND_SMS_MOBILE_FAIL,
                            Line.LEVEL_ERROR, "Failed to send SMS", e.getLocalizedMessage());

                    setLoading(false);
                }

                @Override
                public void onNext(Response<BaseModel> response) {
                    if (response.code() == 200) {
                        openVerification();
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_SEND_SMS_MOBILE_SUCCESS
                                , Line.LEVEL_INFO, "SMS has been successfully sent", "");
                    } else {
                        assert response.errorBody() != null;
                        showErrorMsg(response.errorBody().toString(),
                                Constants.USER_SEND_SMS_MOBILE_FAIL,
                                "Failed to send SMS ");

                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseSubscriber);
        } else {
            showToastMsg(activity, activity.getString(R.string.no_connection));
        }
    }

    public View.OnClickListener signUp() {
        return v -> {
            SystemUtils.hideKeyBoard(v.getContext(), v);
            checkInputsValidation();
        };
    }

    private String parsePhoneNumber() {
        phoneNumber = phoneNumber.replaceAll("\\s+", "");
        if (phoneNumber.startsWith("+2") || phoneNumber.startsWith("002"))
            return phoneNumber;
        if (phoneNumber.startsWith("0"))
            return "+2" + phoneNumber;
        return "+20" + phoneNumber;
    }

    private void openVerification() {
        Intent intent = new Intent(getApplication(), VerifyAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//if(sharedPref.user.mobileNumber == phoneNumber)
        intent.putExtra("phone", SharedPreferencesManager.getInstance().getUser().getMobileNumber()
                .equals(phoneNumber) ? "+2" + SharedPreferencesManager.getInstance().getUser().getENMobileNumber()
                : parsePhoneNumber());
        intent.putExtra("forgot-password", false);
        intent.putExtra(Constants.IS_ORDERING, isOrdering);
        activity.finish();

        getApplication().startActivity(intent);
    }

    public View.OnClickListener openLoginActivity() {
        return v -> {
        };
    }
}
