package com.qurba.android.network.models;

import com.google.gson.JsonElement;

import java.util.List;

public class OrderNowPayload {

    private List<JsonElement> docs;
//    private ArrayList<OrdersModel> userCurrentOrders;

    private LocationDetails locationDetails;


    public List<JsonElement> getEntities() {
        return docs;
    }

    public void setEntities(List<JsonElement> entities) {
        this.docs = docs;
    }

//    public ArrayList<OrdersModel> getUserCurrentOrders() {
//        return userCurrentOrders;
//    }
//
//    public void setUserCurrentOrders(ArrayList<OrdersModel> userCurrentOrders) {
//        this.userCurrentOrders = userCurrentOrders;
//    }

    public LocationDetails getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(LocationDetails locationDetails) {
        this.locationDetails = locationDetails;
    }
}
