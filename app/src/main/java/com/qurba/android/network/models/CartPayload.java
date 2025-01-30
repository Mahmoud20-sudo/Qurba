/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.qurba.android.network.models.ProductCommentModel
 *  java.lang.Object
 *  java.util.ArrayList
 */
package com.qurba.android.network.models;

import com.qurba.android.network.models.response_models.CommentsListResponse;

public class CartPayload extends BaseModel{
    private CartModel payload;
    public CartModel getPayload() {
        return payload;
    }
    public void setPayload(CartModel payload) {
        this.payload = payload;
    }

}

