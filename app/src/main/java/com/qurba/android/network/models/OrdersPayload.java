package com.qurba.android.network.models;

import com.qurba.android.network.models.response_models.OrdersListResponse;

import java.io.Serializable;
import java.util.List;

public class OrdersPayload implements Serializable {

    private OrdersListResponse payload;

    public OrdersListResponse getPayload() {
        return payload;
    }

    public void setPayload(OrdersListResponse payload) {
        this.payload = payload;
    }
}
