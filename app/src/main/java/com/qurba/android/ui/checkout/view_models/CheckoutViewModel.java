package com.qurba.android.ui.checkout.view_models;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.facebook.appevents.AppEventsConstants;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonArray;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.CartModel;
import com.qurba.android.network.models.DeliveryAddress;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.request_models.CreateOrdersPayload;
import com.qurba.android.network.models.request_models.CreateOrdersRequestModel;
import com.qurba.android.network.models.response_models.OrderResponseModel;
import com.qurba.android.ui.auth.views.SignUpActivity;
import com.qurba.android.ui.order_status.views.OrderStatusActivity;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;

import java.io.IOException;
import java.text.NumberFormat;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.qurba.android.utils.extenstions.ExtesionsKt.setInentResult;
import static com.qurba.android.utils.extenstions.ExtesionsKt.showErrorMsg;
import static com.qurba.android.utils.extenstions.ExtesionsKt.showToastMsg;

public class CheckoutViewModel extends BaseViewModel {

    private SharedPreferencesManager sharePref = SharedPreferencesManager.getInstance();
    private CreateOrdersPayload createOrdersPayload = new CreateOrdersPayload();
    private boolean isLoading;
    private boolean isPlaceBusy;
    private JsonArray jsonArray = new JsonArray();
    private Intent previousIntent;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    //    private Subscriber<Response<OrderResponseModel>> subscriber;
    public ObservableField<String> addressType = new ObservableField();
    public ObservableField<String> address = new ObservableField();
    public ObservableField<String> orderName = new ObservableField("");
    public ObservableField<String> orderPrice = new ObservableField("");
    public ObservableField<String> deliveryFees = new ObservableField("");
    public ObservableField<String> deliveryTime = new ObservableField("");
    public ObservableField<String> subtotal = new ObservableField("");
    public ObservableField<String> total = new ObservableField("");
    public ObservableField<String> note = new ObservableField("");
    public ObservableField<String> mobileNumber = new ObservableField();
    public ObservableField<String> totalSaves = new ObservableField();
    public ObservableField<String> vat = new ObservableField("");

    private BaseActivity activity;
    private int totalPrice;
    private int fees;
    private boolean isHaveVat = false;
    private boolean isTotalSavings;

    public void setFees(int fees) {
        this.fees = fees;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setPreviousIntent(Intent previousIntent) {
        this.previousIntent = previousIntent;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
    }

    @Bindable
    public boolean isLoading() {
        return isLoading;
    }

    @Bindable
    public boolean isTotalSavings() {
        return isTotalSavings;
    }

    @Bindable
    public boolean isPlaceBusy() {
        return isPlaceBusy;
    }

    @Bindable
    public boolean isHaveVAT() {
        if (sharedPref.getCart() == null) return false;
        return isHaveVat;
    }

    @Bindable
    public boolean isLostFreeDelivery() {
        return sharedPref.checkIfFreeDeliveryCanceled();
    }

    public void setHaveVat(boolean haveVat) {
        this.isHaveVat = haveVat;
        notifyDataChanged();
    }

    public void setTotalSavings(boolean isTotalSavings) {
        this.isTotalSavings = isTotalSavings;
    }

    public void setMobileNumber() {
        this.mobileNumber.set(sharePref.getUser().getMobileNumber());
        notifyDataChanged();
    }

    public void setAddressType() {
        switch (sharePref.getSelectedCachedArea().getLabel()) {
            case "Home":
                this.addressType.set(activity.getString(R.string.home_lbl));
                break;
            case "Work":
                this.addressType.set(activity.getString(R.string.work_lbl));
                break;
            default:
                this.addressType.set(sharePref.getSelectedCachedArea().getLabel());
                break;
        }

        notifyDataChanged();
    }

    public void setPlaceBusy(boolean isPlaceBusy) {
        this.isPlaceBusy = isPlaceBusy;
        notifyDataChanged();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setAddress() {
        AddAddressModel addAddressModel = sharePref.getSelectedCachedArea();
        address.set(addAddressModel.getCity().getName().getEn() + " ," +
                addAddressModel.getArea().getName().getEn() + " \n" +
                addAddressModel.getStreet() +
                ", " + activity.getString(R.string.building_hint) + " " + addAddressModel.getBuilding() + ", " +
                activity.getString(R.string.floor_hint) + " " + addAddressModel.getFloor() + ", " +
                activity.getString(R.string.flat_hint) + " " + addAddressModel.getFlat());
        notifyDataChanged();
    }

    private CreateOrdersRequestModel setOrderRequest() {
        CreateOrdersRequestModel createOrdersRequestModel = new CreateOrdersRequestModel();
        CreateOrdersPayload payload = new CreateOrdersPayload();
        AddAddressModel cachedAddress = sharedPref.getSelectedCachedArea();
        DeliveryAddress deliveryAddress = new DeliveryAddress(
                cachedAddress.getCountry().get_id(),
                cachedAddress.getCity().get_id(),
                cachedAddress.getArea().get_id(),
                cachedAddress.getStreet(),
                cachedAddress.getBuilding(),
                cachedAddress.getFloor(),
                cachedAddress.getFlat(),
                cachedAddress.getNearestLandmark(),
                cachedAddress.getBranchedStreet());
        payload.setDeliveryAddress(deliveryAddress);
        createOrdersRequestModel.setPayload(payload);
        return createOrdersRequestModel;
    }

    public void confirmOrder(BaseActivity activity, CircularProgressButton v) {
        if (SystemUtils.isNetworkAvailable(getApplication())) {


            Observable<Response<OrderResponseModel>> call = APICalls.Companion.getInstance().createOrder(setOrderRequest());
            Subscriber<Response<OrderResponseModel>> subscriber = new Subscriber<Response<OrderResponseModel>>() {
                @Override
                public void onCompleted() {
                    v.setEnabled(true);
//                    v.revertAnimation();
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    v.setEnabled(true);
                    v.revertAnimation();
                    v.setBackgroundResource(R.drawable.red_rect);
                    QurbaLogger.Companion.logging(activity, Constants.USER_ORDER_SUBMIT_FAIL,
                            Line.LEVEL_ERROR, "Failed to create order ", Log.getStackTraceString(e));
                    showToastMsg(activity, activity.getString(R.string.something_wrong));
                }

                @Override
                public void onNext(Response<OrderResponseModel> response) {
                    v.setEnabled(true);
                    if (response.code() == 200) {

                        setInentResult(activity, previousIntent);
                        logCheckoutOrPurchaseEvent(true, response.body().getPayload().getTransactionID());

                        QurbaLogger.Companion.logging(activity, Constants.USER_ORDER_SUBMIT_SUCCESS, Line.LEVEL_INFO,
                                "Order has been successfully created", "");
                        showToastMsg(activity,
                                activity.getString(R.string.ordered_created_successfully));
                        openConfirmation(response.body().getPayload());

                    } else {
                        v.revertAnimation();
                        v.setBackgroundResource(R.drawable.red_rect);

                        if(response.errorBody()!= null){
                            try {
                                showErrorMsg(response.errorBody().string(),
                                        Constants.USER_CLEAR_CART_FAIL,
                                        "User failed to clear his cart " );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            QurbaLogger.Companion.logging(activity, Constants.USER_NO_INTERNET_CONNECTION, Line.LEVEL_ERROR,
                    QurbaApplication.getContext().getString(R.string.no_connection),
                    "");

            showToastMsg(activity, activity.getString(R.string.no_connection));
        }
    }

    private void openConfirmation(OrdersModel ordersModel) {
        Intent intent = new Intent(getApplication(), OrderStatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ORDER_ID, ordersModel.get_id());
        intent.putExtra("is-creating", true);

        sharedPref.clearCart();
        activity.finish();
        getApplication().startActivity(intent);
    }

    public View.OnClickListener changeMobileNumber() {
        return v -> {
            Intent intent = new Intent(getApplication(), SignUpActivity.class);
            intent.putExtra(Constants.IS_ORDERING, true);
            intent.putExtra("phone", mobileNumber.get());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QurbaApplication.getContext().startActivity(intent);
        };
    }

    public View.OnClickListener createOrderClick() {
        return v -> {
            v.setEnabled(false);
            ((CircularProgressButton) v).startAnimation();
            confirmOrder((BaseActivity) v.getContext(), (CircularProgressButton) v);
        };
    }

    public void setOrderTotalPrice(CartModel cartModel) {
        if (cartModel.getDeliveryFees() > 0) //in case of non-free delivery offers
            this.deliveryFees.set(cartModel.getLocalizedDeliveryFees(activity));

        setFees(cartModel.getDeliveryFees());
        setTotalPrice(cartModel.getTotalPrice());
        setPlaceBusy(cartModel.getPlaceModel().isPlaceBusy());
        setHaveVat(cartModel.getVatPrice() > 0);
        setTotalSavings(cartModel.getTotalSavings() > 0);

        this.deliveryTime.set(activity.getString(R.string.within) + " " + cartModel.getPlaceModel().getTimeLabel(activity));
        this.orderPrice.set(sharePref.getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(cartModel.getTotalPrice()) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(cartModel.getTotalPrice()));
        this.subtotal.set(sharedPref.getCart().getLocalizedSubTotal(activity));

        vat.set(cartModel.getLocalizedVat(activity));
        this.totalSaves.set(sharePref.getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(cartModel.getTotalSavings()) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(cartModel.getTotalSavings()));
        this.total.set(sharePref.getLanguage().equalsIgnoreCase("ar") ? NumberFormat.getInstance().format(cartModel.getTotalPrice()) + " " + activity.getString(R.string.currency) : activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(cartModel.getTotalPrice()));
    }

    public int getFees() {
        return fees;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void logCheckoutOrPurchaseEvent(boolean isPurchase, String orderID) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, String.valueOf(jsonArray));
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, sharedPref.getCart().getId());
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Constants.OFFERS_PRODUCTS);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, sharedPref.getCart().getCartItems().size());
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP");

        Bundle bundle = new Bundle();
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, getTotalPrice());
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "EGP");

        if (isPurchase) {
            bundle.putDouble(FirebaseAnalytics.Param.SHIPPING, getFees());
            bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderID);
            QurbaLogger.Companion.standardFirebaseAnalyticsEventsLogging(QurbaApplication.getContext(), FirebaseAnalytics.Event.PURCHASE, bundle);
            QurbaLogger.Companion.standardFacebookEventsLoggingPurchase(activity, sharedPref.getCart().getTotalPrice(), params);
        } else {
            QurbaLogger.Companion.standardFirebaseAnalyticsEventsLogging(QurbaApplication.getContext(), FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
            QurbaLogger.Companion.standardFacebookEventsLogging(activity,
                    AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, sharedPref.getCart().getTotalPrice(), params);
        }

    }
}