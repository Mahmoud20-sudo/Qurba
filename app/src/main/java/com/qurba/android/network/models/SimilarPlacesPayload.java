package com.qurba.android.network.models;

import com.qurba.android.network.models.response_models.OrdersListResponse;
import com.qurba.android.network.models.response_models.SimilarPlacesListResponse;

import java.io.Serializable;

public class SimilarPlacesPayload implements Serializable {

    private SimilarPlacesListResponse payload;

    public SimilarPlacesListResponse getPayload() {
        return payload;
    }

    public void setPayload(SimilarPlacesListResponse payload) {
        this.payload = payload;
    }
}
