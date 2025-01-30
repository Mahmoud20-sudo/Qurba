package com.qurba.android.ui.offers.view_models;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.response_models.SearchOffersResponseModel;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SystemUtils;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OffersSearchResultsViewModel extends BaseViewModel {

    private Subscriber<Response<SearchOffersResponseModel>> searchSubscriber;
    private boolean isLoading;
    private MutableLiveData<List<OffersModel>> searchOffersObservable;

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public OffersSearchResultsViewModel(@NonNull Application application) {
        super(application);
    }


    public void searchOffers(String query, int page) {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            Observable<Response<SearchOffersResponseModel>> call = APICalls.Companion.getInstance().searchOffers(query, false, page);
            searchSubscriber = new Subscriber<Response<SearchOffersResponseModel>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    setLoading(false);
                    e.printStackTrace();
                }

                @Override
                public void onNext(Response<SearchOffersResponseModel> response) {
                    setLoading(false);
                    SearchOffersResponseModel searchOffersResponseModel = response.body();
                    if (response.code() == 200) {
                        getOffersSearchObservable().postValue(searchOffersResponseModel.getPayload().getDocs());
                    } else {
                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(searchSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public MutableLiveData<List<OffersModel>> getOffersSearchObservable() {
        if (searchOffersObservable == null) {
            searchOffersObservable = new MutableLiveData<>();
        }
        return searchOffersObservable;
    }
}
