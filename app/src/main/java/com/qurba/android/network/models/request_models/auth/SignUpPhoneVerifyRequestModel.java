package com.qurba.android.network.models.request_models.auth;

public class SignUpPhoneVerifyRequestModel {

    private SignUpPhoneVerifyPayload payload;

    public SignUpPhoneVerifyPayload getPayload() {
        return payload;
    }

    public void setPayload(SignUpPhoneVerifyPayload payload) {
        this.payload = payload;
    }
}
