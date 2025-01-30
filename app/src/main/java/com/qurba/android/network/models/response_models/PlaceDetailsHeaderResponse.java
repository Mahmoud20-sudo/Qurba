package com.qurba.android.network.models.response_models;

public class PlaceDetailsHeaderResponse {

    private String status;
    private PlaceDetailsHeaderPayload payload;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlaceDetailsHeaderPayload getPayload() {
        return payload;
    }

    public void setPayload(PlaceDetailsHeaderPayload payload) {
        this.payload = payload;
    }
}
