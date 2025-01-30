package com.qurba.android.network.models.request_models;

import com.qurba.android.network.models.request_models.auth.PhoneVerifyPayload;

public class VoteRequestModel {

    private OrderNowRequest payload;

    public OrderNowRequest getPayload() {
        return payload;
    }

    public void setPayload(OrderNowRequest payload) {
        this.payload = payload;
    }
}
