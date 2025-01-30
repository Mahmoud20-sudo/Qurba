package com.qurba.android.network.models.request_models;

public class CartNoteUpdateModel {

    private CartNoteUpdatePayload payload;

    public void setPayload(CartNoteUpdatePayload payload){
        this.payload = payload;
    }
    public CartNoteUpdatePayload getPayload(){
        return this.payload;
    }

}