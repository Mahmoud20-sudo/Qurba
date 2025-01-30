package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.NearbyPlaceModel;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.network.models.request_models.NearbyPlacesPayload;

import java.util.ArrayList;

public class NearbyPlacesResponseModel {

    private String type;
    private NearbyPlacesPayload payload;
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

    public NearbyPlacesPayload getPayload() {
        return payload;
    }

    public void setPayload(NearbyPlacesPayload payload) {
        this.payload = payload;
    }
}
