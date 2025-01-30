package com.qurba.android.network.models.request_models.auth;

public class SignUpEmailVerifyRequestModel {

    private SignUpEmailVerifyPayload payload;

    public SignUpEmailVerifyPayload getPayload() {
        return payload;
    }

    public void setPayload(SignUpEmailVerifyPayload payload) {
        this.payload = payload;
    }
}
