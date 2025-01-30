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
import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.response_models.ProductsResponseModel;
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

public class UserMealsModel extends BaseViewModel {

    private boolean isDataFound = true;
    private boolean isLoading;
    private MutableLiveData<List<ProductData>> proudctsObservable;

    public UserMealsModel(@NonNull Application application) {
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

    public void getLikedProducts(int page) {
        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(getApplication())) {

            Observable<Response<ProductsResponseModel>> call = APICalls.Companion.getInstance().getLikedProducts(page);
            Subscriber<Response<ProductsResponseModel>> subscriber = new Subscriber<Response<ProductsResponseModel>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_RETRIEVE_LIKED_MEALS_FAIL
                            , Line.LEVEL_ERROR, "Failed to retrieve liked meals "
                            , e.getStackTrace().toString());
                }

                @Override
                public void onNext(Response<ProductsResponseModel> response) {
                    if (response.code()   == 200) {
                        getProductsObservable().postValue(response.body().getPayload().getDocs());
                        if (page == 1 && response.body().getPayload().getDocs().isEmpty())
                            setDataFound(false);

                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_RETRIEVE_LIKED_MEALS_SUCCESS,
                                Line.LEVEL_INFO, "Liked meals has been successfully returned", "");

                        setLoading(false);
                    } else {
                        String errorMsg = null;
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            errorMsg = jObjError.getJSONObject("error").get("en").toString();

                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_RETRIEVE_LIKED_MEALS_FAIL
                                    , Line.LEVEL_ERROR, "Failed to retrieve liked meals "
                                    , errorMsg);
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_RETRIEVE_LIKED_MEALS_FAIL
                                    , Line.LEVEL_ERROR, "Failed to retrieve liked meals "
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

    public MutableLiveData<List<ProductData>> getProductsObservable() {
        if (proudctsObservable == null) {
            proudctsObservable = new MutableLiveData<>();
        }
        return proudctsObservable;
    }

}
