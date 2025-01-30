package com.qurba.android.network.models.response_models;
import com.qurba.android.network.models.AddAddressModel;

public class NearestAreaResponseModel {

    private String type;
    private AddAddressModel payload;
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

    public AddAddressModel getPayload() {
        return payload;
    }

    public void setPayload(AddAddressModel payload) {
        this.payload = payload;
    }
}
