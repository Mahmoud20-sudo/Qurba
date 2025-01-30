package com.qurba.android.network.models;

import java.io.Serializable;

public class LocationDetails implements Serializable {

    private Data country;
    private double distance;
    private Data city;
    private Data area;

    public Data getCountry() {
        return country;
    }

    public void setCountry(Data country) {
        this.country = country;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Data getCity() {
        return city;
    }

    public void setCity(Data city) {
        this.city = city;
    }

    public Data getArea() {
        return area;
    }

    public void setArea(Data area) {
        this.area = area;
    }

    public static class Data implements Serializable{
        private String _id;
        private PlaceDescriptionModel name;

        public PlaceDescriptionModel getName() {
            return name;
        }

        public void setName(PlaceDescriptionModel name) {
            this.name = name;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }

}
