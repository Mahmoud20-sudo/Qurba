package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.PlaceDetailsModel;

public class PlaceDetailsResponseModel {

    private String type;
    private PlaceDetailsModel payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PlaceDetailsModel getPayload() {
        return payload;
    }

    public void setPayload(PlaceDetailsModel payload) {
        this.payload = payload;
    }
}
