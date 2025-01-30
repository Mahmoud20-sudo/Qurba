package com.qurba.android.network.models;

import java.util.ArrayList;

public class CountriesModel {

    private String countryName;
    private String country;
    private ArrayList<CitiesModel> cities;

    private String name;
    private String _id;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ArrayList<CitiesModel> getCitiesModels() {
        return cities;
    }

    public void setCitiesModels(ArrayList<CitiesModel> citiesModels) {
        this.cities = citiesModels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
