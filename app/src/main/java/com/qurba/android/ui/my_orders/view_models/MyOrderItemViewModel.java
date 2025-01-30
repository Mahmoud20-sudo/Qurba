package com.qurba.android.ui.my_orders.view_models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.SimilarOffersModel;
import com.qurba.android.ui.offers.views.OfferDetailsActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.NumberUtils;
import com.qurba.android.utils.QurbaApplication;

import java.text.NumberFormat;

public class MyOrderItemViewModel extends ViewModel implements Observable {

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private OrdersModel ordersModel;
    private Context context;
    private boolean isActive = false;

    @Bindable
    public String getName() {
        if (ordersModel.getPlaceInfo() == null)
            return "-";
        return ordersModel.getPlaceInfo().getName().getEn() + " - " + ordersModel.getPlaceInfo().getBranchName().getEn();
    }

    @Bindable
    public String getOrderDate() {
        return DateUtils.getLongDateFromTimeStamp(ordersModel.getCreatedAt());
    }

    @Bindable
    public String getOrderTotal() {
        return NumberFormat.getInstance().format(ordersModel.getTotalPrice()) + " " + context.getString(R.string.currency);
    }

    @Bindable
    public String getID() {
        return ordersModel.getTransactionID();
    }

    @Bindable
    public String getOrderFullStatus() {
        switch (ordersModel.getStatus()) {
            case "PENDING":
                return context.getString(R.string.order_submitted);
            case "RECEIVED":
                return context.getString(R.string.order_recieved);
            case "PREPARING":
                return context.getString(R.string.order_preparing);
            case "DELIVERING":
                return context.getString(R.string.order_delivery);
            case "COMPLETED":
                return context.getString(R.string.order_complete);
            default:
                return context.getString(R.string.order_canceled);
        }
    }

    @Bindable
    public String getOrderStatus() {
        switch (ordersModel.getStatus()) {
            case "PENDING":
                return context.getString(R.string.pending);
            case "RECEIVED":
                return context.getString(R.string.recieved);
            case "PREPARING":
                return context.getString(R.string.preparing);
            case "DELIVERING":
                return  isActive ? context.getString(R.string.order_out_for_delivery): context.getString(R.string.order_delivery);
            case "COMPLETED":
                return context.getString(R.string.completed);
            default:
                return context.getString(R.string.canceled);
        }
    }

    @Bindable
    public int getColor() {//#f7a157
        switch (ordersModel.getStatus().toUpperCase()) {
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

    @Bindable
    public String getCurrentStatus() {
        return context.getString(R.string.current_hint);
    }

    @Bindable
    public boolean isCurrentOder() {
        return ordersModel.isCurrent();
    }

    public MyOrderItemViewModel(Context context, OrdersModel ordersModel , boolean... isActive) {
        this.ordersModel = ordersModel;
        this.context = context;
        this.isActive = isActive.length > 0;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public View.OnClickListener openOfferDetailsActivity() {
        return v -> {

        };
    }
}
