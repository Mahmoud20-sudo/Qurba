package com.qurba.android.network.models.request_models;

public class GuestAuthModel {

    private GuestPayload payload;

    public void setPayload(GuestPayload payload){
        this.payload = payload;
    }
    public GuestPayload getPayload(){
        return this.payload;
    }

}