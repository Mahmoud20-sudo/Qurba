package com.qurba.android.network.models.request_models.auth;

public class ForgotEmailVerifyRequestModel {

    private ForgotEmailVerifyPayload payload;

    public ForgotEmailVerifyPayload getPayload() {
        return payload;
    }

    public void setPayload(ForgotEmailVerifyPayload payload) {
        this.payload = payload;
    }
}
