package com.qurba.android.ui.offers.view_models;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;

import android.app.Application;
import android.app.Dialog;
import android.view.View;
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
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.network.models.request_models.GetOffersRequestModel;
import com.qurba.android.network.models.request_models.NearbyPlacesPayload;
import com.qurba.android.network.models.request_models.OrderNowRequest;
import com.qurba.android.network.models.response_models.OffersResponseModel;
import com.qurba.android.network.models.response_models.SearchOffersResponseModel;
import com.qurba.android.ui.offers.views.OfferTypesFragment;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OffersViewModel extends BaseViewModel {

    private Subscription subscriber;
    private Subscriber<Response<NearbyPlacesPayload>> featuredPlacesSubscriber;
    private Subscriber<Response<SearchOffersResponseModel>> searchSubscriber;
    private MutableLiveData<List<OffersModel>> offersObservable;
    private MutableLiveData<List<PlaceModel>> placesObservable;
    private MutableLiveData<List<OffersModel>> searchOffersObservable;
    private boolean isLoading;
    public GetOffersRequestModel getOffersRequestModel;
    private boolean discountSelected = false;
    private boolean freeItemsSelected = false;
    private boolean isAscending = false;
    private Dialog dialog;
    private OrderNowRequest offersRequest;
    private SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
    private boolean isDataFound;
    private OfferTypesFragment offerTypesFragment;

    @Bindable
    public boolean isDataFound() {
        return isDataFound;
    }

    public void setDataFound(boolean dataFound) {
        isDataFound = dataFound;
        notifyDataChanged();
    }

    public boolean isDiscountSelected() {
        return discountSelected;
    }

    private void setDiscountSelected(boolean discountSelected) {
        this.discountSelected = discountSelected;
    }

    public boolean isFreeItemsSelected() {
        return freeItemsSelected;
    }

    private void setFreeItemsSelected(boolean freeItemsSelected) {
        this.freeItemsSelected = freeItemsSelected;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public void setAscending(boolean ascending) {
        isAscending = ascending;
    }

    public OffersViewModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void getOffers(int page, String place, String search) {

        if (page == 1) {
            setDataFound(true);
            setLoading(true);
        }

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_OFFERS_ATTEMPT,
                    Line.LEVEL_INFO, "User trying to retrieve Offers list data "
                    , "");


            AddAddressModel addAddressModel = sharedPreferencesManager.getSelectedCachedArea();
            String cityId = addAddressModel.getCity().getId() == null ? addAddressModel.getCity().get_id() : addAddressModel.getCity().getId();
            String areaId = addAddressModel.getArea().getId() == null ? addAddressModel.getArea().get_id() : addAddressModel.getArea().getId();
            String country = addAddressModel.getCountry().getId() == null ? addAddressModel.getCountry().get_id() : addAddressModel.getCountry().getId();

            Map<String, Object> map = new HashMap<>();

            if (place != null)
                map.put("place", place);
            if (country != null)
                map.put("country", country);
            if (cityId != null)
                map.put("city", cityId);
            if (areaId != null)
                map.put("area", areaId);
            if (!search.isEmpty())
                map.put("search", search);

            if (sharedPreferencesManager.getFilterModel() != null) {
                if (!sharedPreferencesManager.getFilterModel().getOfferType().isEmpty())
                    map.put("type[]", sharedPreferencesManager.getFilterModel().getOfferType().get(0));

                if (sharedPreferencesManager.getFilterModel().getCanDeliver() != null)
                    map.put("can_deliver", true);

                if (sharedPreferencesManager.getFilterModel().getActiveNow() != null)
                    map.put("active_now", true);

                if (sharedPreferencesManager.getFilterModel().getPrice() > 0)
                    map.put("price", sharedPreferencesManager.getFilterModel().getPrice());
            }

//            Observable.just(new Story()).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(getStoryObserver());

            subscriber = APICalls.Companion.getInstance()
                    .getOffers(page, map).
                            subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<OffersResponseModel>>() {
                        @Override
                        public void onCompleted() {
                            setLoading(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            setLoading(false);
                            //e.printStackTrace();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_OFFERS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve Offers list data "
                                    , e.getMessage());

                            Toast.makeText(getApplication(), getApplication().getString(R.string.something_wrong) + " " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Response<OffersResponseModel> response) {
                            if (response.code() == 200) {
                                OffersResponseModel offersResponseModel = response.body();
                                getOffersObservable().postValue(offersResponseModel.getOffersListResponse().getDocs());

                                if (offersResponseModel.getOffersListResponse().getDocs().isEmpty() && page == 1)
                                    setDataFound(false);

                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_GET_OFFERS_SUCCESS,
                                        Line.LEVEL_INFO, "User able to retrieve Offers list data "
                                        , "");

                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_GET_OFFERS_FAIL,
                                            "Failed to retrieve Offers list data ");
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

    public void getFeaturedPlaces() {
        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_FEATURED_PLACES_ATTEMPT,
                    Line.LEVEL_INFO, "User trying to retrieve featured places data "
                    , "");

            Observable<Response<NearbyPlacesPayload>> call = APICalls.Companion.getInstance().getFeaturedPlaces();
            featuredPlacesSubscriber = new Subscriber<Response<NearbyPlacesPayload>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    setLoading(false);
                    e.printStackTrace();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_GET_FEATURED_PLACES_FAIL,
                            Line.LEVEL_ERROR, "Failed to retrieve featured places data "
                            , e.getMessage());
                }

                @Override
                public void onNext(Response<NearbyPlacesPayload> response) {
                    setLoading(false);
                    NearbyPlacesPayload offersResponseModel = response.body();
                    if (response.code() == 200) {
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_GET_FEATURED_PLACES_SUCCESS,
                                Line.LEVEL_INFO, "User able to retrieve featured places data "
                                , "");

                        getPlacesObservable().postValue(offersResponseModel.getPayload());
                    } else {
                        assert response.errorBody() != null;
                        try {
                            showErrorMsg(response.errorBody().string(),
                                    Constants.USER_GET_FEATURED_PLACES_FAIL,
                                    "Failed to retrieve featured places data ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(featuredPlacesSubscriber);
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void searchOffers(String query) {
//        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
//
//            Observable<Response<SearchOffersResponseModel>> call = APICalls.getInstance().searchOffers(query, true, 1);
//            searchSubscriber = new Subscriber<Response<SearchOffersResponseModel>>() {
//                @Override
//                public void onCompleted() {
//                    setLoading(false);
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    setLoading(false);
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onNext(Response<SearchOffersResponseModel> response) {
//                    setLoading(false);
//                    SearchOffersResponseModel searchOffersResponseModel = response.body();
//                    if (response.code() == 200) {
//                        getOffersSearchObservable().postValue(searchOffersResponseModel.getPayload().getDocs());
//                    } else {
//                        Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            };
//            call.subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(searchSubscriber);
//        } else {
//            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
//        }
    }

    public void offersSortByMostClaimed() {
        getOffersRequestModel.setSortBy(Constants.OFFER_FILTER_CLAIMS_COUNT);
        getOffersRequestModel.setPage(1);
//        getOffers(getOffersRequestModel);
    }

    public void offersSortByMostRecent() {
        getOffersRequestModel.setSortBy(Constants.OFFER_FILTER_CREATED_AT);
        getOffersRequestModel.setPage(1);
//        getOffers(getOffersRequestModel);
    }

    public void offersSortByMostFollowed() {
        getOffersRequestModel.setSortBy(Constants.OFFER_FILTER_FAVORITES_COUNT);
        getOffersRequestModel.setPage(1);
//        getOffers(getOffersRequestModel);
    }

    public void offersSortByNearest() {
        getOffersRequestModel.setSortBy(Constants.OFFER_FILTER_NEAREST);
        getOffersRequestModel.setPage(1);
//        getOffers(getOffersRequestModel);
    }

    public MutableLiveData<List<OffersModel>> getOffersObservable() {
        if (offersObservable == null) {
            offersObservable = new MutableLiveData<>();
        }
        return offersObservable;
    }

    public MutableLiveData<List<PlaceModel>> getPlacesObservable() {
        if (placesObservable == null) {
            placesObservable = new MutableLiveData<>();
        }
        return placesObservable;
    }


    public MutableLiveData<List<OffersModel>> getOffersSearchObservable() {
        if (searchOffersObservable == null) {
            searchOffersObservable = new MutableLiveData<>();
        }
        return searchOffersObservable;
    }


    public View.OnClickListener toggleFree() {
        return v -> {
            if (isFreeItemsSelected()) {
                setFreeItemsSelected(false);
                getOffersRequestModel.getTypes().clear();
            } else {
                setFreeItemsSelected(true);
                getOffersRequestModel.getTypes().clear();
                getOffersRequestModel.getTypes().add(Constants.OFFER_TYPE_FREE_ITEMS);
            }

            setDiscountSelected(false);
            getOffersRequestModel.setPage(1);
//            getOffers(getOffersRequestModel);
        };
    }

    public View.OnClickListener toggleDiscount() {
        return v -> {
            if (isDiscountSelected()) {
                setDiscountSelected(false);
                getOffersRequestModel.getTypes().clear();
            } else {
                setDiscountSelected(true);
                getOffersRequestModel.getTypes().clear();
                getOffersRequestModel.getTypes().add(Constants.OFFER_TYPE_DISCOUNT);

            }

            setFreeItemsSelected(false);
            getOffersRequestModel.setPage(1);
//            getOffers(getOffersRequestModel);
        };
    }


}
