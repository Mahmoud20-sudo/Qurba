package com.qurba.android.network.models;

import java.util.ArrayList;
import java.util.List;

public class PlaceLocation {

    private String type;
    private List<Float> coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Float> coordinates) {
        this.coordinates = coordinates;
    }
}
