package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.DeliveryModel;
import com.qurba.android.network.models.LocationModel;
import com.qurba.android.network.models.PlaceDescriptionModel;

import java.text.NumberFormat;

public class DeliveryValidationResponse {

    private String _id;
    private PlaceDescriptionModel name;
    private DeliveryModel country;
    private DeliveryModel city;
    private String createdAt;
    private String updatedAt;
    private LocationModel location;
    private String id;
    private float deliveryFees;
    private int deliveryTime;
    private boolean isPlaceBusy;

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

    public DeliveryModel getCountry() {
        return country;
    }

    public void setCountry(DeliveryModel country) {
        this.country = country;
    }

    public DeliveryModel getCity() {
        return city;
    }

    public void setCity(DeliveryModel city) {
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

    public String getDeliveryFees() {
        return NumberFormat.getInstance().format(Math.round(deliveryFees));
    }

    public int getFees() {
        return Math.round(deliveryFees);
    }

    public void setDeliveryFees(int deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public int getTotalPrice(int subTotal, double vat) {
        return (int) (deliveryFees + subTotal + (subTotal * vat));
    }

    public String getDeliveryTime() {
        return NumberFormat.getInstance().format(deliveryTime);
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isPlaceBusy() {
        return isPlaceBusy;
    }

    public void setPlaceBusy(boolean placeBusy) {
        isPlaceBusy = placeBusy;
    }
}
