package com.qurba.android.network.models;

import java.util.ArrayList;

public class CitiesModel {

    private String name;
    private ArrayList<AreasModel> areas;
    private String id;
    private String _id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<AreasModel> getAreasModels() {
        return areas;
    }

    public void setAreasModels(ArrayList<AreasModel> areas) {
        this.areas = areas;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CitiesModel)) return false;

        if(id.equals(((CitiesModel) o).id)) return true;


        return false;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
