package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.ProductsPayload;

import java.util.List;

import io.branch.referral.util.Product;

public class ProductsResponseModel {

    private ProductsPayload payload;

    public ProductsPayload getPayload() {
        return payload;
    }

    public void setPayload(ProductsPayload payload) {
        this.payload = payload;
    }


}