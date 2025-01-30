package com.qurba.android.network.models;

import java.text.NumberFormat;

public class SectionItems {

    private float price;
    private String _id;
    private PlaceDescriptionModel title;
    private PlaceDescriptionModel description;

    public PlaceDescriptionModel getTitle() {
        return title;
    }

    public void setTitle(PlaceDescriptionModel title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public float getPrice() {
        return price;
    }

    public String getPriceTxt() {
        return NumberFormat.getInstance().format(Math.round(price));
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public PlaceDescriptionModel getDescription() {
        return description == null ? new PlaceDescriptionModel() : description;
    }

    public void setDescription(PlaceDescriptionModel description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionItems)) return false;
        if (_id.equals(((SectionItems) o)._id)) return true;

        return false;
    }
}
