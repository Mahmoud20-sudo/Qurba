package com.qurba.android.ui.offers.view_models;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.qurba.android.R;
import com.qurba.android.network.models.SimilarOffersModel;
import com.qurba.android.ui.offers.views.OfferDetailsActivity;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DateUtils;
import com.qurba.android.utils.NumberUtils;

public class OtherOfferItemViewModel extends ViewModel implements Observable {

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private SimilarOffersModel offersModel;
    private Context context;
    private boolean discount = false;

    public OtherOfferItemViewModel(Context context, SimilarOffersModel offersModel) {
        this.offersModel = offersModel;
        this.context = context;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    private boolean timed = false;

    @Bindable
    public String getTitle() {
        return offersModel.getTitle();
    }

    @Bindable
    public String getType() {
        if (offersModel.getType().equals(Constants.OFFER_TYPE_DISCOUNT)) {
            return context.getString(R.string.discount);
        } else {
            return context.getString(R.string.free) + " " + context.getString(R.string.items);
        }
    }

    @Bindable
    public String getRemainingDays() {
        return DateUtils.getTimeUntilFromTimeStamp(offersModel.getEndDate());
    }

    @Bindable
    public String getShortEndDate() {
        return context.getString(R.string.expire) + " " + DateUtils.getShortDateFromTimeStamp(offersModel.getEndDate());
    }

    @Bindable
    public String getFollowers() {
        return offersModel.getFavoritesCount() + " " + context.getString(R.string.followers);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    @Bindable
    public boolean isDiscount() {
        if (offersModel.getType().equals(Constants.OFFER_TYPE_DISCOUNT)) {
            setDiscount(true);
        } else {
            setDiscount(false);
        }
        return discount;
    }

    @Bindable
    public String getOfferPrice() {
        return offersModel.getPrice() > 0 ? Math.round(offersModel.getPrice() * 100) / 100 + " L.E" : "";
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

    @Bindable
    public String getDiscount() {
        return NumberUtils.getFinaleDiscountValue(offersModel.getDiscountRatio());
    }

    public View.OnClickListener openOfferDetailsActivity() {
        return v -> {
            Intent intent = new Intent(context, OfferDetailsActivity.class);
            Gson gson = new Gson();
            String offer = gson.toJson(offersModel);
            intent.putExtra("offer_name", offersModel.getUniqueName());
            intent.putExtra("offer_id", offersModel.get_id());
            intent.putExtra("offer", offer);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        };
    }
}