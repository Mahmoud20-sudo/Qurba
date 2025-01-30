package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.LocationModel;
import com.qurba.android.network.models.NearbyPlaceModel;
import com.qurba.android.network.models.PlaceDescriptionModel;

import java.util.ArrayList;

public class DeliveryAreaPayload {
    private DeliveryAreaResponse payload;
    private String type;
    private String errorType;
    private String args;
    private String en;
    private String ar;

    public DeliveryAreaResponse getPayload() {
        return payload;
    }

    public void setPayload(DeliveryAreaResponse payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getAr() {
        return ar;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}

