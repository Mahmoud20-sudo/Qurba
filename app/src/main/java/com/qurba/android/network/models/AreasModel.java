package com.qurba.android.network.models;

import java.util.ArrayList;

public class AreasModel {

    private String name;
    private float deliveryFees;
    private String id;
    private String _id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(int deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
