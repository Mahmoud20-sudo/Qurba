package com.qurba.android.network.models.request_models;

public class PushNotificationsPayload {

    private String operatingSystem;
    private String token;
    private String appVersion;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushNotificationsPayload() {
        return operatingSystem;
    }

    public void setPushNotificationsPayload(String pushNotificationsPayload) {
        operatingSystem = pushNotificationsPayload;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
