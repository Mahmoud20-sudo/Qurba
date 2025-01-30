package com.qurba.android.ui.address_component.view_models;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.BaseModel;
import com.qurba.android.network.models.response_models.DeliveryAreaPayload;
import com.qurba.android.network.models.response_models.DeliveryAreaResponse;
import com.qurba.android.network.models.response_models.DeliveryAreaResponseModel;
import com.qurba.android.network.models.response_models.DeliveryValidationPayload;
import com.qurba.android.ui.home.views.CachedDeliveryAreasFragment;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DelieveryAddressViewModel extends BaseViewModel {

    private Subscriber<Response<DeliveryAreaResponseModel>> subscriber;
    private MutableLiveData<List<DeliveryAreaResponse>> deliveryObservable;
    private Subscriber<Response<BaseModel>> baseSubscriber;

    private MutableLiveData<AddAddressModel> deleteObservable;
    private MutableLiveData<DeliveryAreaPayload> deliveryAreaPayObservable;
    public final MutableLiveData<DeliveryValidationPayload> responseMutableLiveData = new MutableLiveData<>();

    private boolean isLoading;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();
    private boolean isHaveAddress;
    private boolean isHaveDeliveryArea;
    private BaseActivity activity;

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public void setHaveDeliveryArea(boolean haveDeliveryArea) {
        isHaveDeliveryArea = haveDeliveryArea;
        notifyDataChanged();
    }

    public DelieveryAddressViewModel(@NonNull Application application) {
        super(application);
        setHaveAddress();
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isGuest() {
        return isHaveAddress;
    }

    public void setHaveAddress() {
        isHaveAddress = sharedPref.isGuest() || sharedPref.getUser().getDeliveryAddresses().isEmpty();
        notifyDataChanged();
    }

    @Bindable
    public boolean isHaveDeliveryArea() {
        return isHaveDeliveryArea;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void getCities(int page) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
//            Loggly.Companion.Logging(QurbaApplication.getContext(),
//                    Constants.USER_GOOGLE_LOGIN_API_CALL,
//                    "info", "User login using Google account has call api " + accessToken);

            setLoading(true);

            if (SharedPreferencesManager.getInstance().getDefaultCountry() == null) {
                getDefaultCountry();
                return;
            }

            Observable<Response<DeliveryAreaResponseModel>> call = APICalls.Companion.getInstance().getCities(
                    SharedPreferencesManager.getInstance().getDefaultCountry().get_id(), page);
            subscriber = new Subscriber<Response<DeliveryAreaResponseModel>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                            Constants.USER_GET_CITIES_FAIL, Line.LEVEL_INFO,
                            "User failed to retrieve cities ", Log.getStackTraceString(e));

                    setLoading(false);
                    Toast.makeText(activity, activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<DeliveryAreaResponseModel> response) {
                    setLoading(false);
                    if (response.code() == 200) {
                        getDeliveryResponse().postValue(response.body().getPayload().getDocs());
                        QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                Constants.USER_GET_CITIES_SUCCESS, Line.LEVEL_INFO,
                                "User failed to retrieve cities ", "");
                    } else {
                        try {
                            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                    Constants.USER_GET_CITIES_FAIL, Line.LEVEL_INFO,
                                    "User failed to retrieve cities ", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void getDefaultCountry() {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_DEFAULT_COUNTRY_ATTEMPT,
                    "info", "User attempt to retrieve default country ", "");

            Observable<Response<DeliveryAreaResponseModel>> call = APICalls.Companion.getInstance().getCountry();
            Subscriber subscriber = new Subscriber<Response<DeliveryAreaResponseModel>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                            Constants.USER_DEFAULT_COUNTRY_FAIL, Line.LEVEL_INFO,
                            "User failed to retrieve default country ", Log.getStackTraceString(e));

                    Toast.makeText(activity, activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<DeliveryAreaResponseModel> response) {
                    if (response.code() == 200) {

                        SharedPreferencesManager.getInstance().setDefaultCountry(response.body().getPayload().getDocs().get(0));
                        getCities(1);
                        QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                Constants.USER_DEFAULT_COUNTRY_SUCCESS, Line.LEVEL_INFO,
                                "User failed to retrieve default country ", "");
                    } else {
                        try {
                            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                    Constants.USER_DEFAULT_COUNTRY_FAIL, Line.LEVEL_INFO,
                                    "User failed to retrieve cities ", response.errorBody().string());
                        } catch (IOException e) {
                            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                    Constants.USER_DEFAULT_COUNTRY_FAIL, Line.LEVEL_INFO,
                                    "User failed to retrieve default country ", Log.getStackTraceString(e));

                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .subscribe(subscriber);
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void getAreas(int page, String cityID, String search) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                    Constants.USER_GET_AREA_BY_CITY_ATTEMPT, Line.LEVEL_INFO,
                    "User trying to retrieve areas by city id ", cityID);

            setLoading(true);
            Observable<Response<DeliveryAreaResponseModel>> call =
                    APICalls.Companion.getInstance().getAreasByCity(SharedPreferencesManager.getInstance().getDefaultCountry().get_id(), cityID, page, search);
            subscriber = new Subscriber<Response<DeliveryAreaResponseModel>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                            Constants.USER_GET_AREA_BY_CITY_FAIL, Line.LEVEL_ERROR,
                            "User failed to retrieve areas by city id ", Log.getStackTraceString(e));

                    setLoading(false);
                    Toast.makeText(activity, activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<DeliveryAreaResponseModel> response) {
                    setLoading(false);

                    if (response.code() == 200) {
                        getDeliveryResponse().postValue(response.body().getPayload().getDocs());
                        QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                Constants.USER_GET_AREA_BY_CITY_SUCCESS, Line.LEVEL_INFO,
                                "User failed to retrieve cities ", cityID);
                    } else if (response.code() == 400) {
                        try {
                            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                    Constants.USER_GET_AREA_BY_CITY_FAIL, Line.LEVEL_ERROR,
                                    "User failed to retrieve areas by city ", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAddress(AddAddressModel addAddressModel) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {

            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                    Constants.USER_ADDRESS_DELETE_SUBMIT, Line.LEVEL_INFO,
                    "User attempting to delete address", "");

            Observable<Response<BaseModel>> call = APICalls.Companion.getInstance().deleteAddress(addAddressModel.getId());
            baseSubscriber = new Subscriber<Response<BaseModel>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                            Constants.USER_ADDRESS_DELETE_FAIL, Line.LEVEL_ERROR,
                            "User failed to add address ", Log.getStackTraceString(e));

                    Toast.makeText(activity, activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<BaseModel> response) {
                    if (response.code() == 200 || response.code() == 204) {
                        deleteObservable().postValue(addAddressModel);
                        QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                Constants.USER_ADDRESS_DELETE_SUCCESS, Line.LEVEL_INFO,
                                "User deleted address successfully", "");

                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String errorMsg = jObjError.getJSONArray("error").getJSONObject(0).get("message").toString();

                            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                    Constants.USER_ADDRESS_DELETE_FAIL, Line.LEVEL_INFO,
                                    "User failed to delete address ", errorMsg);
                            Toast.makeText(activity, errorMsg
                                    , Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(getApplication().getApplicationContext(),
                                    Constants.USER_ADDRESS_DELETE_FAIL, Line.LEVEL_ERROR,
                                    "User failed to delete address ", Log.getStackTraceString(e));

                            Toast.makeText(activity,
                                    activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseSubscriber);
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void checkIfNotDelivering(String placeId, String areaId, CachedDeliveryAreasFragment cachedDeliveryAreasFragment) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {
            Observable<Response<DeliveryValidationPayload>> call = APICalls.Companion.getInstance().checkDelivering(
                    placeId, areaId);
            Subscriber<Response<DeliveryValidationPayload>> subscriber = new Subscriber<Response<DeliveryValidationPayload>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(activity
                            , activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Response<DeliveryValidationPayload> response) {
                    cachedDeliveryAreasFragment.checkAddress(response.body());
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public MutableLiveData<AddAddressModel> deleteObservable() {
        if (deleteObservable == null) {
            deleteObservable = new MutableLiveData<>();
        }
        return deleteObservable;
    }

    public MutableLiveData<List<DeliveryAreaResponse>> getDeliveryResponse() {
        if (deliveryObservable == null) {
            deliveryObservable = new MutableLiveData<>();
        }
        return deliveryObservable;
    }

    public MutableLiveData<DeliveryAreaPayload> getDeliveryAreaObservable() {
        if (deliveryAreaPayObservable == null) {
            deliveryAreaPayObservable = new MutableLiveData<>();
        }
        return deliveryAreaPayObservable;
    }
}
