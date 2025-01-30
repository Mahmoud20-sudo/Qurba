package com.qurba.android.ui.profile.view_models;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.response_models.OffersResponseModel;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserOffersModel extends BaseViewModel {

    private boolean isDataFound = true;
    private boolean isLoading;
    private MutableLiveData<List<OffersModel>> offersObservable;

    public UserOffersModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isDataFound() {
        return isDataFound;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setDataFound(boolean dataFound) {
        isDataFound = dataFound;
        notifyDataChanged();
    }

    public void getLikedOffers(int page) {
        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(getApplication())) {

            Observable<Response<OffersResponseModel>> call = APICalls.Companion.getInstance().getLikedOffers(page);
            Subscriber<Response<OffersResponseModel>> subscriber = new Subscriber<Response<OffersResponseModel>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_RETRIEVE_HIS_OFFER_DETAILS_FAIL
                            , Line.LEVEL_ERROR, "Failed to retrieve liked offers", e.getLocalizedMessage());
                }

                @Override
                public void onNext(Response<OffersResponseModel> response) {
                    if (response.code() == 200) {
                        setLoading(false);
                        getOffersObservable().postValue(response.body().getOffersListResponse().getDocs());

                        if (page == 1 && response.body().getOffersListResponse().getDocs().isEmpty())
                            setDataFound(false);

                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_RETRIEVE_HIS_OFFER_DETAILS_SUCCESS,
                                Line.LEVEL_INFO, "Liked offers has been successfully returned", "");

                    } else {

                        String errorMsg = null;
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            errorMsg = jObjError.getJSONObject("error").get("en").toString();

                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_RETRIEVE_HIS_OFFER_DETAILS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve liked offers "
                                    , errorMsg);
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_RETRIEVE_HIS_OFFER_DETAILS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve liked offers "
                                    , e.getStackTrace().toString());
                        } finally {
                            if (page == 1)
                                Toast.makeText(QurbaApplication.getContext(),
                                        errorMsg == null ? QurbaApplication.getContext().getString(R.string.something_wrong) :
                                                errorMsg, Toast.LENGTH_SHORT).show();
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


    public MutableLiveData<List<OffersModel>> getOffersObservable() {
        if (offersObservable == null) {
            offersObservable = new MutableLiveData<>();
        }
        return offersObservable;
    }
}
