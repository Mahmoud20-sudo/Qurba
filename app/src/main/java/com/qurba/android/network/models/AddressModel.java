package com.qurba.android.network.models;

import java.util.List;

public class AddressModel {

    private PlaceDescriptionModel firstLine;
    private AddressDetailsModel area;
    private AddressDetailsModel city;
    private AddressDetailsModel country;

    private List<PlaceDescriptionModel> nearestLandmark;
    private List<PlaceDescriptionModel> nearestParking;

    public PlaceDescriptionModel getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(PlaceDescriptionModel firstLine) {
        this.firstLine = firstLine;
    }

    public AddressDetailsModel getArea() {
        return area;
    }

    public void setArea(AddressDetailsModel area) {
        this.area = area;
    }

    public AddressDetailsModel getCity() {
        return city;
    }

    public void setCity(AddressDetailsModel city) {
        this.city = city;
    }

    public AddressDetailsModel getCountry() {
        return country;
    }

    public void setCountry(AddressDetailsModel country) {
        this.country = country;
    }

    public List<PlaceDescriptionModel> getNearestLandmark() {
        return nearestLandmark;
    }

    public void setNearestLandmark(List<PlaceDescriptionModel> nearestLandmark) {
        this.nearestLandmark = nearestLandmark;
    }

    public List<PlaceDescriptionModel> getNearestParking() {
        return nearestParking;
    }

    public void setNearestParking(List<PlaceDescriptionModel> nearestParking) {
        this.nearestParking = nearestParking;
    }
}
