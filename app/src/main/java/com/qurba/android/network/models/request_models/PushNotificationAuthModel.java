package com.qurba.android.network.models.request_models;

public class PushNotificationAuthModel {

    private PushNotificationsPayload payload;

    public void setPayload(PushNotificationsPayload payload){
        this.payload = payload;
    }
    public PushNotificationsPayload getPayload(){
        return this.payload;
    }

}