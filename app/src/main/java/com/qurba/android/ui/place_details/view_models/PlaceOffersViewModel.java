package com.qurba.android.ui.place_details.view_models;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.response_models.DeliveryAreaResponse;
import com.qurba.android.network.models.response_models.OffersResponseModel;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaceOffersViewModel extends BaseViewModel {

    public Subscription subscriber;
    private MutableLiveData<List<OffersModel>> offersObservable;
    private boolean isLoading;
    private boolean isOrdering;
    private int currentPage;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    public PlaceOffersViewModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isOrdering() {
        return isOrdering;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setOrdering(boolean ordering) {
        isOrdering = ordering;
        notifyDataChanged();
    }

    public void getOffers(int page, String placeID, String... exclude) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            if (currentPage == page)//for nested scroll view multiple call with the same page issue
                return;

            Map<String, Object> map = new HashMap<>();
            map.put("page", page);
            if (exclude.length > 0)
                map.put("exclude[]", exclude[0]);

            currentPage = page;
            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_PLACE_INFO_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve place's offers"
                    , "");

            subscriber = APICalls.Companion.getInstance().gePlaceOffers(placeID, map).
                    subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<OffersResponseModel>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            setLoading(false);
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_PLACE_OFFERS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve place's offers"
                                    , e.getMessage());
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Response<OffersResponseModel> response) {
                            setLoading(false);
                            if (response.code() == 200) {
                                OffersResponseModel offersResponseModel = response.body();
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_GET_PLACE_OFFERS_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully retrieve place's offers"
                                        , "");
                                assert offersResponseModel != null;
                                getOffersObservable().postValue(offersResponseModel.getOffersListResponse().getDocs());
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_GET_PLACE_OFFERS_FAIL,
                                            "Failed to retrieve place's offers ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
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
