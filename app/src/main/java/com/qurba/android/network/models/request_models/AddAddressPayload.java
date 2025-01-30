package com.qurba.android.network.models.request_models;

public class AddAddressPayload {

    private String label;
    private String country;
    private String city;
    private String area;
    private String street;
    private String building;
    private String floor;
    private String flat;
    private String nearestLandmark;
    private String branchedStreet;

    public String getNearestLandmark() {
        return nearestLandmark;
    }

    public void setNearestLandmark(String nearestLandmark) {
        this.nearestLandmark = nearestLandmark;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBranchedStreet() {
        return branchedStreet;
    }

    public void setBranchedStreet(String branchedStreet) {
        this.branchedStreet = branchedStreet;
    }
}