package com.qurba.android.network.models.request_models;

import java.util.ArrayList;

public class GetOffersRequestModel {

    private int page;
    private String sortBy;
    private String sortOrder = "-1";
    private String lng;
    private String lat;
    private ArrayList<String> categoryIDs = new ArrayList<>();
    private ArrayList<String> types= new ArrayList<>();
    private boolean autocomplete = false;
    private String search;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public ArrayList<String> getCategoryIDs() {
        return categoryIDs;
    }

    public void setCategoryIDs(ArrayList<String> categoryIDs) {
        this.categoryIDs = categoryIDs;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public boolean isAutocomplete() {
        return autocomplete;
    }

    public void setAutocomplete(boolean autocomplete) {
        this.autocomplete = autocomplete;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
