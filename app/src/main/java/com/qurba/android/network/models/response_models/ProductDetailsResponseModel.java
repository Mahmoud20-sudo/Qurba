package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.ProductData;

public class ProductDetailsResponseModel {

    private String type;
    private ProductData payload;
    private ErrorModel errorModel;
    private String errorType;

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProductData getPayload() {
        return payload;
    }

    public void setPayload(ProductData payload) {
        this.payload = payload;
    }
}
