package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.OrdersModel;

import java.util.List;


public class OrdersListResponse {

    private List<OrdersModel> docs;

    public List<OrdersModel> getDocs() {
        return docs;
    }

    public void setDocs(List<OrdersModel> docs) {
        this.docs = docs;
    }
}
