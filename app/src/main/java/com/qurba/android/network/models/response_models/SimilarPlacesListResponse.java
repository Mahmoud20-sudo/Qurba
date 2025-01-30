package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.PlaceModel;

import java.util.List;

public class SimilarPlacesListResponse {

    private List<PlaceModel> docs;

    public List<PlaceModel> getDocs() {
        return docs;
    }

    public void setDocs(List<PlaceModel> docs) {
        this.docs = docs;
    }
}
