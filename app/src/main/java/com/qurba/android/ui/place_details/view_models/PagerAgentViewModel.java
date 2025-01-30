package com.qurba.android.ui.place_details.view_models;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.qurba.android.R;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.ui.cart.views.CartActivity;
import com.qurba.android.ui.place_details.views.PlaceDetailsActivity;
import com.qurba.android.utils.BaseViewModel;
import com.qurba.android.utils.SharedPreferencesManager;

import java.text.NumberFormat;

public class PagerAgentViewModel extends BaseViewModel {
    private MutableLiveData<Integer> priceValue;
    private MutableLiveData<Integer> quantityValue;
    private SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();

    private boolean isAddedToCart;
    private Context context;

    public PagerAgentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(Context context) {
        priceValue = new MutableLiveData<>(0);
        quantityValue = new MutableLiveData<>(0);
        this.context = context;
        updateCartView();
    }

    @Bindable
    public boolean isAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean isAddedToCart) {
        this.isAddedToCart = isAddedToCart;
        notifyDataChanged();
    }

    public void setPriceValue(int price) {
        priceValue.setValue(price);
        notifyDataChanged();
    }

    public void setQuantityValue(int qty) {
        quantityValue.setValue(qty);
        notifyDataChanged();
    }

    public String getPrice() {
        return
                SharedPreferencesManager.getInstance().getLanguage().equalsIgnoreCase("ar") ?
                        NumberFormat.getInstance().format(priceValue.getValue()) + " " + context.getString(R.string.currency)
                        : context.getString(R.string.currency) + " " + NumberFormat.getInstance().format(priceValue.getValue());
    }

    public String getQuantity() {
        return NumberFormat.getInstance().format(quantityValue.getValue());
    }

    public View.OnClickListener viewCartActivity() {
        return v -> {
            Intent intent = new Intent(v.getContext(), CartActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
            if(v.getContext() instanceof PlaceDetailsActivity) ((PlaceDetailsActivity) v.getContext()).finish();
        };
    }

    private void updateCartView() {
        int price = 0;
        int qty = 0;

        if (sharedPref.getCart() == null) {
            setAddedToCart(false);
            return;
        }

        for (CartItems model : sharedPref.getCart().getCartItems()) {
            price += model.getPrice() * model.getQuantity();
            qty += model.getQuantity();
        }

        setQuantityValue(qty);
        setPriceValue(price);

        setAddedToCart(price > 0);
    }
}