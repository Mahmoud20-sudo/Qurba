package com.qurba.android.network.models.request_models;

import java.util.ArrayList;

public class FollowUnFollowPayload {

    private ArrayList<String> placesIds = new ArrayList<>();

    public ArrayList<String> getPlacesIds() {
        return placesIds;
    }

    public void setPlacesIds(ArrayList<String> placesIds) {
        this.placesIds = placesIds;
    }
}
