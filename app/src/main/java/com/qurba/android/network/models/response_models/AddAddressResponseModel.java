package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.AddressModel;
import com.qurba.android.network.models.LastAddressModel;
import com.qurba.android.network.models.request_models.AddAddressPayload;

public class AddAddressResponseModel {

    private com.qurba.android.network.models.AddAddressModel payload;
    private ErrorModel errorModel;

    public com.qurba.android.network.models.AddAddressModel  getPayload() {
        return payload;
    }

    public void setPayload(com.qurba.android.network.models.AddAddressModel  payload) {
        this.payload = payload;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }
}
