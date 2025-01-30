package com.qurba.android.network.models;

public class FilterFacility {

    private String id;
    private PlaceDescriptionModel name;
    private boolean checked;

    public FilterFacility(String id, String name, boolean checked) {
        this.id = id;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
