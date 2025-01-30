package com.qurba.android.network.models;

import java.io.Serializable;

public class DeliveryAddress implements Serializable {
    private String country;
    private String city;
    private String area;
    private String street;
    private String building;
    private String floor;
    private String flat;
    private String branchedStreet;
    private String nearestLandmark;

    public DeliveryAddress(String country,
                           String city, String area, String street, String building, String floor,
                           String flat, String nearestLandmark, String branchedStreet) {
        this.country = country;
        this.city = city;
        this.area = area;
        this.street = street;
        this.building = building;
        this.floor = floor;
        this.flat = flat;
        this.nearestLandmark = nearestLandmark;
        this.branchedStreet = branchedStreet;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String address) {
        this.street = address;
    }

    public String getFlatNumber() {
        return flat;
    }

    public void setFlatNumber(String flatNumber) {
        this.flat = flatNumber;
    }

    public String getNearestLandmark() {
        return nearestLandmark;
    }

    public void setNearestLandmark(String nearestLandmark) {
        this.nearestLandmark = nearestLandmark;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}