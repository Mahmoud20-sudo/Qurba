package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.PlaceDescriptionModel;
import com.qurba.android.network.models.ProductData;

import java.util.List;

public class ProductsListResponse {

    private PlaceDescriptionModel sectionName;
    private int sortOrder;

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public PlaceDescriptionModel getName() {
        return sectionName;
    }

    public void setName(PlaceDescriptionModel name) {
        this.sectionName = name;
    }

    private List<ProductData> products;

    public List<ProductData> getProducts() {
        return products;
    }

    public void setProducts(List<ProductData> products) {
        this.products = products;
    }

}
