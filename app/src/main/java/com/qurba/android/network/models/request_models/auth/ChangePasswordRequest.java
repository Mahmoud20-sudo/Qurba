package com.qurba.android.network.models.request_models.auth;

public class ChangePasswordRequest {

    private ChangePasswordPayload payload;

    public ChangePasswordPayload getPayload() {
        return payload;
    }

    public void setPayload(ChangePasswordPayload payload) {
        this.payload = payload;
    }
}
