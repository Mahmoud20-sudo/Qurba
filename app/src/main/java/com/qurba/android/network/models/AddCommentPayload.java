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

import java.util.List;

public class AddCommentPayload {
    private List<CommentModel> payload;
    public List<CommentModel> getPayload() {
        return payload;
    }
    public void setPayload(List<CommentModel> payload) {
        this.payload = payload;
    }

}

