package com.qurba.android.network.models.request_models.auth;

public class VerifyEmailRequest {

    private VerifyEmailPayload payload;

    public VerifyEmailPayload getPayload() {
        return payload;
    }

    public void setPayload(VerifyEmailPayload payload) {
        this.payload = payload;
    }
}
