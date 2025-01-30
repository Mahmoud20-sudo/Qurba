package com.qurba.android.network.models;

public class DeliveryModel  {

    private String _id;
    private PlaceDescriptionModel name;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }
}
