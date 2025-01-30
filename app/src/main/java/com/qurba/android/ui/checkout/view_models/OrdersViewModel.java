package com.qurba.android.ui.checkout.view_models;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.utils.BaseActivity;

public class OrdersViewModel extends ViewModel implements Observable {

    private BaseActivity activity;
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private CartItems cartItems;
    private int position;

    public OrdersViewModel(BaseActivity activity, CartItems cartItems, int position) {
        this.activity = activity;
        this.cartItems = cartItems;
        this.position = position;
    }

    public String setDatangredient() {
        String string = "";
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

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }


}