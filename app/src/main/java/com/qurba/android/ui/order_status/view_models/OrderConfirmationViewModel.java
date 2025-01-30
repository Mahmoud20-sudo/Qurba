package com.qurba.android.ui.order_status.view_models;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.R;
import com.qurba.android.network.APICalls;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.OrderHistory;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.response_models.OrderResponseModel;
import com.qurba.android.ui.home.views.HomeActivityKotlin;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;
import com.qurba.android.utils.SystemUtils;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class OrderConfirmationViewModel extends BaseViewModel {

    private Subscriber<Response<OrderResponseModel>> subscriber;
    private MutableLiveData<OrdersModel> ordersModel;

    public ObservableField<String> address = new ObservableField("");
    public ObservableField<String> orderNumber = new ObservableField("");
    public ObservableField<String> orderBranch = new ObservableField("");
    public ObservableField<String> note = new ObservableField("");
    public ObservableField<String> total = new ObservableField("");
    public ObservableField<String> vat = new ObservableField("");
    public ObservableField<String> orderPrice = new ObservableField("");
    public ObservableField<String> orderDate = new ObservableField("");
    public ObservableField<String> placePhone = new ObservableField("");
    public ObservableField<String> subtotal = new ObservableField("");
    public ObservableField<String> fees = new ObservableField("");
    public ObservableField<String> cancelReason = new ObservableField("");
    public ObservableField<String> orderOn = new ObservableField("");
    public ObservableField<String> totalSaving = new ObservableField("");
    public ObservableField<String> placeName = new ObservableField("");
    public ObservableField<String> orderStatusFull = new ObservableField("");
    public ObservableField<String> deliveryTime = new ObservableField("");

    public ObservableField<Boolean> showOrderDetails = new ObservableField(false);

    private boolean isLoading;
    public ObservableField<Boolean> isCancled = new ObservableField(false);
    public ObservableField<Boolean> isRecieved = new ObservableField(false);
    public ObservableField<Boolean> isPrepared = new ObservableField(false);
    public ObservableField<Boolean> isDelivering = new ObservableField(false);
    public ObservableField<Boolean> isCompleted = new ObservableField(false);

    private boolean isOrderCreated = false;
    private boolean isDataFound = true;
    private boolean isCancelable = true;
    private boolean isPlaceBusy = false;

    private int typeFace = Typeface.NORMAL;

    private BaseActivity activity;
    private boolean isTotalSavings;
    private OrdersModel orderObject;
    private boolean isHaveVAT;

//    public abstract static class BindingAdapter {
//        @androidx.databinding.BindingAdapter("app:textStyle")
//        public static void setTextStyle(TextView v, int style) {
//            v.setTypeface(null, style);
//        }
//    }

    public OrderConfirmationViewModel(@NonNull Application application) {
        super(application);
    }


    @Bindable
    public boolean isDataFound() {
        return isDataFound;
    }

    @Bindable
    public int getValueFormat() {
        return typeFace;
    }

    @Bindable
    public boolean isCancelable() {
        return isCancelable;
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
    public boolean isOrderCreated() {
        return isOrderCreated;
    }

    @Bindable
    public boolean isPlaceBusy() {
        return isPlaceBusy;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataChanged();
    }

    public void setPlaceBusy(boolean placeBusy) {
        isPlaceBusy = placeBusy;
        notifyDataChanged();
    }

    @Bindable
    public boolean isHaveVAT() {
        return isHaveVAT;
    }

    public void setHaveVAT(boolean haveVAT) {
        isHaveVAT = haveVAT;
        notifyDataChanged();
    }

    public void setTotalSavings(boolean totalSavings) {
        isTotalSavings = totalSavings;
        notifyDataChanged();
    }

    public void setTypeFace(int typeFace) {
        this.typeFace = typeFace;
    }

    public void setOrderCreated(boolean orderCreated) {
        isOrderCreated = orderCreated;
        notifyDataChanged();
    }

    public void setCancled(boolean cancled) {
        isCancled.set(cancled);
        notifyDataChanged();
    }

    public void setRecieved(boolean recieved) {
        isRecieved.set(recieved);
        notifyDataChanged();
    }

    public void setPrepared(boolean prepared) {
        isPrepared.set(prepared);
        notifyDataChanged();
    }

    public void setDelivering(boolean delivering) {
        isDelivering.set(delivering);
        notifyDataChanged();
    }

    public void setCompleted(boolean completed) {
        isCompleted.set(completed);
        notifyDataChanged();
    }

    public void setDataFound(boolean dataFound) {
        isDataFound = dataFound;
        notifyDataChanged();
    }

    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
        notifyDataChanged();
    }

    public void setOrderObject(OrdersModel orderObject) {
        this.orderObject = orderObject;
        orderNumber.set(activity.getString(R.string.order_id_hint) + " " + orderObject.getTransactionID());
        fees.set(orderObject.getDeliveryFees(activity));
        orderDate.set(DateUtils.getTimeAgoFromTimeStamp(activity, orderObject.getCreatedAt()));
        orderOn.set(DateUtils.getTimeAgoFromTimeStamp(activity, orderObject.getCreatedAt()));
        cancelReason.set(orderObject.getHistory() != null ? orderObject.getHistory().get(orderObject.getHistory().size() - 1).getCancellationReason() : "");
        totalSaving.set(orderObject.getTotalSavings(activity));
        total.set(orderObject.getTotalPrice(activity));
        subtotal.set(orderObject.getSubTotal(activity));
        vat.set(orderObject.getLocalizedVat(activity));
        note.set(orderObject.getNote());

        address.set(orderObject.getDeliveryAddress().getCity().getName().getEn() + ", " +
                orderObject.getDeliveryAddress().getArea().getName().getEn() + " \n" +
                orderObject.getDeliveryAddress().getStreet() +
                ", " + activity.getString(R.string.building_hint) + " " + orderObject.getDeliveryAddress().getBuilding() + ", " +
                activity.getString(R.string.floor_hint) + " " + orderObject.getDeliveryAddress().getFloor() + ", " +
                activity.getString(R.string.flat_hint) + " " + orderObject.getDeliveryAddress().getFlat() + "\n" + activity.getString(R.string.mobile_number)
                + ": " + orderObject.getUser().getMobile());

        setStatusHistory(orderObject.getHistory());
        setPlaceBusy(orderObject.getPlaceInfo().isPlaceBusy());

        if (orderObject.getPlaceInfo() != null) {
            orderBranch.set(orderObject.getPlaceInfo().getName().getEn() + " " + orderObject.getPlaceInfo().getBranchName().getEn());
            placeName.set(orderObject.getPlaceInfo().getName().getEn() + " - " + orderObject.getPlaceInfo().getBranchName().getEn());
            if (orderObject.getPlaceInfo().getMobileNumber() != null)
                placePhone.set(orderObject.getPlaceInfo().getMobileNumber());
            else if (orderObject.getPlaceInfo().getHotLineNumber() != null)
                placePhone.set(orderObject.getPlaceInfo().getHotLineNumber());
            else if (orderObject.getPlaceInfo().getTelephoneNumber() != null)
                placePhone.set(orderObject.getPlaceInfo().getTelephoneNumber());
            else
                placePhone.set(orderObject.getPlaceInfo().getPhoneNumber());
        }
    }

    @Bindable
    public int getColor() {//#f7a157
        if (orderObject == null)
            return Color.BLACK;

        switch (orderObject.getStatus().toUpperCase()) {
            case "PENDING":
                return Color.parseColor("#f7a157");
            case "DELIVERING":
                return Color.parseColor("#659e1e");
            case "CANCELLED":
                return Color.parseColor("#d0021b");
            case "RECEIVED":
                return Color.parseColor("#659E1E");
            default:
                return Color.BLACK;
        }
    }

    private void setStatus(String status) {
        switch (status) {
            case "PENDING":
                orderStatusFull.set(activity.getString(R.string.pending));
                break;
            case "RECEIVED":
                setRecieved(true);
                orderStatusFull.set(activity.getString(R.string.order_recieved));
                break;
            case "PREPARING":
                setPrepared(true);
                setRecieved(true);
                orderStatusFull.set(activity.getString(R.string.order_preparing));
                break;
            case "DELIVERING":
                setDelivering(true);
                setPrepared(true);
                setRecieved(true);
                orderStatusFull.set(activity.getString(R.string.order_out_for_delivery));
                break;
            case "COMPLETED":
                setDelivering(true);
                setPrepared(true);
                setRecieved(true);
                setCompleted(true);
                orderStatusFull.set(activity.getString(R.string.order_complete));
                break;
            case "CANCELLED":
                setTypeFace(Typeface.BOLD);
                setCancelable(false);
                setCancled(true);
                orderStatusFull.set(activity.getString(R.string.order_canceled));
                break;
        }
    }

    private void setStatusHistory(List<OrderHistory> historyList) {
//        for (OrderHistory orderHistory : historyList) {
//            setStatus(orderHistory.getStatus());
//        }
    }

    public void callPlace() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+201212888860", null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        QurbaApplication.getContext().startActivity(intent);
    }

    public View.OnClickListener goHome() {
        return v -> {
//            activity.onBackPressed();
            Intent intent = new Intent(activity, HomeActivityKotlin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.IS_ORDER_FINISHED, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        };
    }

    public View.OnClickListener cancelOrder() {
        return v -> {
            activity.showCancelDialog(this);
        };
    }

    public View.OnClickListener showOrderDetailsClick() {
        return v -> {
            showOrderDetails.set(true);
            notifyDataChanged();
        };
    }

    public void getOrderDetails(String id) {
        //setOrderCreated(true);

        if (SystemUtils.isNetworkAvailable(QurbaApplication.getContext())) {
            setLoading(true);

            Observable<Response<OrderResponseModel>> call = APICalls.Companion.getInstance().getOrderDetails(id);
            subscriber = new rx.Subscriber<Response<OrderResponseModel>>() {
                @Override
                public void onCompleted() {
                    setLoading(false);
                }

                @Override
                public void onError(Throwable e) {
                    setLoading(false);
                    e.printStackTrace();
                    Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                            Constants.USER_ORDER_DETAILS_FAIL
                            , Line.LEVEL_ERROR, "Failed to retrieve orders details", e.getLocalizedMessage());
                }

                @Override
                public void onNext(Response<OrderResponseModel> response) {
                    setLoading(false);
                    if (response.code() == 200) {
                        OrdersModel ordersModel = response.body().getPayload();
                        getOrdersModelObservable().postValue(ordersModel);
                        SharedPreferencesManager.getInstance().setOrdering(isOrderCreated);
                        QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                Constants.USER_ORDER_DETAILS_SUCCESS,
                                Line.LEVEL_INFO, "Order details has been successfully returned", "");
                    } else {
                        try {
                            String error = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(error);
                            String errorMsg = jObjError.getJSONObject("error").get("en").toString();

                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ORDER_DETAILS_FAIL
                                    , Line.LEVEL_ERROR, "Failed to retrieve orders details",
                                    response.code() + " " + errorMsg);
                            Toast.makeText(QurbaApplication.getContext(), errorMsg
                                    , Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            QurbaLogger.Companion.logging(QurbaApplication.getContext(),
                                    Constants.USER_ORDER_DETAILS_FAIL
                                    , Line.LEVEL_ERROR, "Failed to retrieve orders details",
                                    response.code() + " " + e.getStackTrace());
                            Toast.makeText(QurbaApplication.getContext(),
                                    QurbaApplication.getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
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

    public MutableLiveData<OrdersModel> getOrdersModelObservable() {
        if (ordersModel == null) {
            ordersModel = new MutableLiveData<>();
        }
        return ordersModel;
    }

//    public int updateTotalPrice(List<Offers> offers) {
//
//        int totalPrice = 0;
//        for (Offers offersModel : offers) {
//            totalPrice += offersModel.getQuantity() * offersModel.getPrice();
//        }
//        return totalPrice;
//    }

}
