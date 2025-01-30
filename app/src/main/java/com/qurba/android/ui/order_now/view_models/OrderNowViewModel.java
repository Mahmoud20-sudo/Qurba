package com.qurba.android.ui.order_now.view_models;

import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonElement;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.AreasModel;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.CitiesModel;
import com.qurba.android.network.models.LocationDetails;
import com.qurba.android.network.models.OrderNowModel;

import com.qurba.android.ui.cart.views.CartActivity;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class OrderNowViewModel extends BaseViewModel {

    private boolean isLoading;
    private boolean isDataFound = true;
    private boolean isHaveQuantity;
    private boolean isFiltered;

    private MutableLiveData<List<JsonElement>> entitiesObservable;
//    private OrderNowRequest orderNowRequest;
    private SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
    public ObservableField<String> qty = new ObservableField("0");

    public ObservableField deliveryTitle = new ObservableField("");

    private CitiesModel citiesModel = sharedPreferencesManager.getDeliveryCity();
    private AreasModel areasModel = sharedPreferencesManager.getDliveryArea();
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    public OrderNowViewModel(@NonNull Application application) {
        super(application);
        updateQuantity();
    }

    public void setDeliveryTitle(LocationDetails locationDetails) {
        String deliveryTitle = "";
        if (locationDetails == null) {

            if (citiesModel != null)
                deliveryTitle += citiesModel.getName();
            if (areasModel != null)
                deliveryTitle += ", " + areasModel.getName();
        } else {
            deliveryTitle += locationDetails.getCity().getName().getEn() + ", " + locationDetails.getArea().getName().getEn();
        }

        this.deliveryTitle.set(deliveryTitle);
        notifyDataChanged();
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isHaveQuantity() {
        return isHaveQuantity;
    }

    @Bindable
    public boolean isDataFound() {
        return isDataFound;
    }

    @Bindable
    public boolean isFiltered() {
        return isFiltered;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
        notifyDataChanged();
    }

    public void setHaveQuantity() {
        isHaveQuantity = qty.get().equals("0");
        notifyDataChanged();
    }

    public void setDataFound(boolean dataFound) {
        isDataFound = dataFound;
        notifyDataChanged();
    }

    public void updateQuantity() {
        if (sharedPref.getCart() == null) {
            this.qty.set("0");
        } else {
            int qty = 0;
            List<CartItems> offersModels = sharedPref.getCart().getCartItems();
            for (CartItems cartItems : offersModels) {
                qty += cartItems.getQuantity();
            }
            this.qty.set(qty + "");
            notifyDataChanged();
        }
        setHaveQuantity();
    }

    public void getOrderNowData(int page, int price, String search) {

        if (page == 1)
            setLoading(true);

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {

            setDataFound(true);
//            orderNowRequest = new OrderNowRequest();


            callApi(page, price, search);
        } else {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private Map<String, Object> getIngredients(int price, String search) {
        Map<String, Object> map = new HashMap<>();

        AddAddressModel addAddressModel = sharedPreferencesManager.getSelectedCachedArea();
        String areaId = addAddressModel.getArea().getId() == null ? addAddressModel.getArea().get_id() : addAddressModel.getArea().getId();

        if (areaId != null)
            map.put("area", areaId);//5db5d11cb18a250012973276
//        if (cityId != null)
//            map.put("city", cityId);
//        if (country != null)
//            map.put("country", country);
        if (price > 0)
            map.put("price", price);
        if (!search.isEmpty())
            map.put("search", search);

        return map;
    }

    public void callApi(int page, int price, String search) {

        if (SystemUtils.isNetworkAvailable(getApplication())) {

            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                    Constants.USER_GET_ORDER_NOW_ATTEMPT,
                    Line.LEVEL_INFO, "User trying to retrieve Order-Now data "
                    , "");

            APICalls.Companion.getInstance().getOrderNow(page, getIngredients(price, search)).
                    subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<OrderNowModel>>() {
                        @Override
                        public void onCompleted() { }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_GET_ORDER_NOW_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve Order-Now data "
                                    , e.getMessage());
                            Toast.makeText(getApplication(), QurbaApplication.getContext().getString(R.string.something_wrong) + " " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Response<OrderNowModel> response) {
                            if (response.code() == 200) {
                                setDeliveryTitle(response.body().getPayload().getLocationDetails());
                                getOffersObservable().postValue(response.body().getPayload().getEntities());
                                QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                        Constants.USER_GET_ORDER_NOW_SUCCESS,
                                        Line.LEVEL_INFO, "User is able to retrieve data retrieve Order-Now data", "");

//                                if (response.body().getPayload().getEntities().isEmpty()) {
//                                    if (price > 0) {
//                                        setFiltered(true);
//                                    } else {
//                                        setDataFound(false);
//                                        setLoading(false);
//                                    }
//                                }
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_GET_ORDER_NOW_FAIL,
                                            "Failed to retrieve Order-Now data ");
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

    public MutableLiveData<List<JsonElement>> getOffersObservable() {
        if (entitiesObservable == null) {
            entitiesObservable = new MutableLiveData<>();
        }
        return entitiesObservable;
    }

    public View.OnClickListener openCartScreen() {
        return v -> {

            Intent intent = new Intent(v.getContext(), CartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QurbaApplication.getContext().startActivity(intent);
            //QurbaApplication.currentActivity().finish();
        };
    }

    public View.OnClickListener openDeliverAreaFragment() {
        return v -> {

        };
    }
}