package com.qurba.android.ui.cart.view_models;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.qurba.android.R;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.SharedPreferencesManager;

import java.text.NumberFormat;

public class CartItemViewModel extends ViewModel implements Observable {

    private final int position;
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private CartItems cartItems;
    private BaseActivity activity;
    private boolean isEditable = false;

    public CartItemViewModel(BaseActivity activity, CartItems cartItems, int position) {
        this.activity = activity;
        this.cartItems = cartItems;
        this.position = position;
    }

    public String setDatangredient() {
        String string = "";
        String groupName = "";

        if (cartItems.getSections() != null) {
            for (int j = 0; j < cartItems.getSections().size(); j++) {
                string += setSectionData(cartItems.getSections().get(j)) + (j < cartItems.getSections().size() - 1 ? ", " : "");
            }
        }
        if (cartItems.getSectionsGroup() != null && !cartItems.getSectionsGroup().isEmpty()) {
            string += (string.length() > 0 ? ", " : "");
            for (int i = 0; i < cartItems.getSectionsGroup().size(); i++) {
                SectionsGroup group = cartItems.getSectionsGroup().get(i);
                for (int j = 0; j < group.getSections().size(); j++) {
                    string += setSectionData(group.getSections().get(j)) + (j < cartItems.getSections().size() - 1 ? ", " : "");
                }
                string += i < cartItems.getSectionsGroup().size() - 1 ? ", " : "";
            }
        }

        return string;
    }

    private String setSectionData(Sections sections) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(stringBuilder.length() > 0 ? ", " : "");
        for (int i = 0; i < sections.getItems().size(); i++) {
            stringBuilder.append(sections.getItems().get(i).getTitle().getEn());
            stringBuilder.append(i == sections.getItems().size() - 1 ? "" : ", ");
        }
        return stringBuilder.toString().trim();
    }

    @Bindable
    public String getTitle() {
        return cartItems.getTitle().getEn();
    }

    @Bindable
    public boolean isAvailable() {
        return cartItems.isAvailable();
    }

    @Bindable
    public String getQuantity() {
        return cartItems.getQuantity() + "";
    }

    @Bindable
    public String getItemPrice() {
        if (SharedPreferencesManager.getInstance().getLanguage().equalsIgnoreCase("ar"))
            return NumberFormat.getInstance().format(cartItems.getPrice()) + " " + activity.getString(R.string.currency);
        else
            return activity.getString(R.string.currency) + " " + NumberFormat.getInstance().format(cartItems.getPrice());
    }

    @Bindable
    public String getTotalPrice() {
        return cartItems.getPrice() * cartItems.getQuantity() + "";
    }

    @Bindable
    public boolean isReadyToOrder() {
        return cartItems.isOrderable();
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public View.OnClickListener opendDeliveryAreaActivity() {
        return v -> {

        };
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }
}
