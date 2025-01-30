package com.qurba.android.network.models;

import com.qurba.android.network.models.response_models.AppSettingsModel;
import com.qurba.android.network.models.response_models.ErrorModel;

public class AppSettingsPayload {

    private String type;
    private AppSettingsModel payload;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AppSettingsModel getPayload() {
        return payload;
    }

    public void setPayload(AppSettingsModel payload) {
        this.payload = payload;
    }
}
