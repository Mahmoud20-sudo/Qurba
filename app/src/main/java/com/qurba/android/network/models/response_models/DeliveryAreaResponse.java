package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.LocationModel;
import com.qurba.android.network.models.PlaceDescriptionModel;

import java.io.Serializable;


public class DeliveryAreaResponse implements Serializable {

    private String _id;
    private PlaceDescriptionModel name;
    private String country;
    private String city;
    private String createdAt;
    private String updatedAt;
    private LocationModel location;
    private String id;
    private float deliveryFees;

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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(float deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this._id.equalsIgnoreCase(((DeliveryAreaResponse) o).get_id());
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }
}

