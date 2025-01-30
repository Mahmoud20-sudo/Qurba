package com.qurba.android.network.models;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class OffersFilteringModel {

    private int price;
    private List<String> offerType;
    private String canDeliver;
    private String activeNow;
    private List<PlaceCategoryModel> categoryModels;

    public List<String> getOfferType() {
        return offerType != null ? offerType : new ArrayList<>();
    }

    public int getPrice() {
        return price;
    }

    public String getLocalePrice() {
        return NumberFormat.getInstance().format(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getActiveNow() {
        return activeNow;
    }

    public void setActiveNow(String activeNow) {
        this.activeNow = activeNow;
    }

    public String getCanDeliver() {
        return canDeliver;
    }

    public void setCanDeliver(String canDeliver) {
        this.canDeliver = canDeliver;
    }

    public void setOfferType(List<String> offerType) {
        this.offerType = offerType;
    }

    public List<PlaceCategoryModel> getCategoryModels() {
        return categoryModels != null ? categoryModels : new ArrayList<>();
    }

    public void setCategoryModels(List<PlaceCategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }
}
