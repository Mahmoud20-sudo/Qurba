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

import java.util.ArrayList;

public class CommentsPayload {
    private CommentsListResponse payload;
    public CommentsListResponse getPayload() {
        return payload;
    }
    public void setPayload(CommentsListResponse payload) {
        this.payload = payload;
    }

}

