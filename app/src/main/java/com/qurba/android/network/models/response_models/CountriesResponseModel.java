package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.CountriesModel;

import java.util.List;

public class CountriesResponseModel {

    private String type;
    private List<CountriesModel> payload;
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

    public List<CountriesModel> getPayload() {
        return payload;
    }

    public void setPayload(List<CountriesModel> payload) {
        this.payload = payload;
    }
}
