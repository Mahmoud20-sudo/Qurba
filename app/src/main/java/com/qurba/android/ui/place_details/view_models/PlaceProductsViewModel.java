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
import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.response_models.ProductsResponseModel;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;


import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaceProductsViewModel extends BaseViewModel {

    public Subscription subscriber;
    private MutableLiveData<List<ProductData>> productsObservable;
    private boolean isLoading;
    private boolean isOrdering;
    private boolean isDiscountMenu;

    private float discountRatio;
    private String expireDate;
    private String message;
    private BaseActivity activity;

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public PlaceProductsViewModel(@NonNull Application application) {
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

    @Bindable
    public boolean isDiscountMenu() {
        return isDiscountMenu;
    }

    @Bindable
    public String getDiscountMessage() {
        return activity.getString(R.string.enjoy) + " " + NumberFormat.getInstance().format(discountRatio * 100)
                + activity.getString(R.string.percentage) + " " + activity.getString(R.string.discount_on_menu);
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    @Bindable
    public String getEXpireDate() {
        return activity.getString(R.string.expire_on) + " " + DateUtils.getShortDate(expireDate);
    }

    public void setDiscountMenu(boolean discountMenu) {
        isDiscountMenu = discountMenu;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setMessage(String message) {
        this.message = message;
        notifyDataChanged();
    }

    public void setOrdering(boolean ordering) {
        isOrdering = ordering;
        notifyDataChanged();
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
        notifyDataChanged();
    }

    public void setDiscountRatio(float discountRatio) {
        this.discountRatio = discountRatio;
        notifyDataChanged();
    }

    public void getProducts(int page, String placeID) {
        if (SystemUtils.isNetworkAvailable(activity)) {

            QurbaLogger.Companion.logging(activity,
                    Constants.USER_GET_PLACE_PRODUCTS_ATTEMPT,
                    Line.LEVEL_INFO, "User attempt to retrieve place's products"
                    , "");

            subscriber = APICalls.Companion.getInstance().gePlaceProducts(placeID, page).
                    subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<ProductsResponseModel>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            setLoading(false);
                            e.printStackTrace();
                            QurbaLogger.Companion.logging(activity,
                                    Constants.USER_GET_PLACE_PRODUCTS_FAIL,
                                    Line.LEVEL_ERROR, "Failed to retrieve place's products"
                                    , e.getMessage());
                            Toast.makeText(getApplication(), activity.getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Response<ProductsResponseModel> response) {
                            setLoading(false);
                            if (response.code() == 200) {
                                QurbaLogger.Companion.logging(activity,
                                        Constants.USER_GET_PLACE_PRODUCTS_SUCCESS,
                                        Line.LEVEL_INFO, "Successfully retrieve place's products"
                                        , "");
                                ProductsResponseModel productsResponseModel = response.body();
                                assert productsResponseModel != null;
                                getProductsObservable().postValue(productsResponseModel.getPayload().getProducts().getDocs());
                                //section of discount
                                if (productsResponseModel.getPayload().getOffer() != null) {
                                    setDiscountRatio(productsResponseModel.getPayload().getOffer().getDiscountRatio());
                                    setExpireDate(productsResponseModel.getPayload().getOffer().getEndDate());
                                    setMessage(productsResponseModel.getPayload().getOffer().getMessage() != null ?
                                            productsResponseModel.getPayload().getOffer().getMessage().getEn() : "");
                                    if (page == 1)
                                        setDiscountMenu(productsResponseModel.getPayload().getOffer().getDiscountRatio() > 0f);
                                }
                            } else {
                                assert response.errorBody() != null;
                                try {
                                    showErrorMsg(response.errorBody().string(),
                                            Constants.USER_GET_PLACE_PRODUCTS_FAIL,
                                            "Failed to retrieve place's products ");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }


    public MutableLiveData<List<ProductData>> getProductsObservable() {
        if (productsObservable == null) {
            productsObservable = new MutableLiveData<>();
        }
        return productsObservable;
    }


}

