package com.qurba.android.network.models.response_models;

public class OffersResponseModel {

    private String type;
    private OffersListResponse payload;
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

    public OffersListResponse getOffersListResponse() {
        return payload;
    }

    public void setOffersListResponse(OffersListResponse payload) {
        this.payload = payload;
    }
}