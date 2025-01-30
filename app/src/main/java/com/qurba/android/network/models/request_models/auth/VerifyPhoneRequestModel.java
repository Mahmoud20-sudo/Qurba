package com.qurba.android.network.models.request_models.auth;

public class VerifyPhoneRequestModel {

    private PhoneVerifyPayload payload;

    public PhoneVerifyPayload getPayload() {
        return payload;
    }

    public void setPayload(PhoneVerifyPayload payload) {
        this.payload = payload;
    }
}
