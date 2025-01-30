package com.qurba.android.network.models.request_models;

import com.qurba.android.network.models.CartItems;

import java.util.ArrayList;
import java.util.List;

public class ValidateOrderPayload {

    private List<CartItems> offers;
    private List<CartItems> products;

    public List<CartItems> getOffers() {
        return offers;
    }

    public List<CartItems> getAll() {
        List<CartItems> allModels = new ArrayList<>();

        if (offers != null)
            allModels.addAll(offers);
        if (products != null)
            allModels.addAll(products);

        return allModels;
    }

    public boolean isValid() {
        for (CartItems model : getAll()) {
            if (!model.isAvailable()) {
                return false;
            }
        }
        return true;
    }

    public void setOffers(List<CartItems> offers) {
        this.offers = offers;
    }

    public List<CartItems> getProducts() {
        return products;
    }

    public void setProducts(List<CartItems> products) {
        this.products = products;
    }
}
