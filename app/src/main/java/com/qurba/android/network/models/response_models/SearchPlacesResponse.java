package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.NearbyPlaceModel;
import java.util.ArrayList;

public class SearchPlacesResponse {

    private String type;
    private String typeError;
    private ArrayList<NearbyPlaceModel> payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeError() {
        return typeError;
    }

    public void setTypeError(String typeError) {
        this.typeError = typeError;
    }

    public ArrayList<NearbyPlaceModel> getPayload() {
        return payload;
    }

    public void setPayload(ArrayList<NearbyPlaceModel> payload) {
        this.payload = payload;
    }
}
