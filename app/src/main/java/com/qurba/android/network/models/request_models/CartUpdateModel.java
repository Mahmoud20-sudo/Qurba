package com.qurba.android.network.models.request_models;

public class CartUpdateModel {

    private CartUpdatePayload payload;

    public void setPayload(CartUpdatePayload payload){
        this.payload = payload;
    }
    public CartUpdatePayload getPayload(){
        return this.payload;
    }

}