package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.LastAddressModel;

public class AppSettingsModel {

    private String iosVersion;
    private String androidVersion;
    private boolean mandatoryUpdate;

    public boolean isMandatoryUpdate() {
        return mandatoryUpdate;
    }

    public void setMandatoryUpdate(boolean mandatoryUpdate) {
        this.mandatoryUpdate = mandatoryUpdate;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getIosVersion() {
        return iosVersion;
    }

    public void setIosVersion(String iosVersion) {
        this.iosVersion = iosVersion;
    }
}
