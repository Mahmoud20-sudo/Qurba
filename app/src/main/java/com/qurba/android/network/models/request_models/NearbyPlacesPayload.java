package com.qurba.android.network.models.request_models;

import com.qurba.android.network.models.PlaceModel;

import java.util.List;

public class NearbyPlacesPayload {

    private double lng;
    private double lat;
    private FilterModel filter;
    private List<PlaceModel> docs;
    private List<PlaceModel> payload;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public FilterModel getFilter() {
        return filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }

    public List<PlaceModel> getDocs() {
        return docs;
    }

    public void setDocs(List<PlaceModel> payload) {
        this.docs = payload;
    }

    public List<PlaceModel> getPayload() {
        return payload;
    }

    public void setPayload(List<PlaceModel> payload) {
        this.payload = payload;
    }
}
