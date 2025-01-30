package com.qurba.android.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Offers implements Serializable {
    private String offer;
    private int quantity;
    private String price;
    //    private PlaceDescriptionModel offerName;
    private List<OfferIngradients> sections;
    private List<OfferIngradients> sectionGroups;

    public Offers(String string, int quantity, List<OfferIngradients> sectionGroups, List<OfferIngradients> list) {
        this.offer = string;
        this.quantity = quantity;
        this.sectionGroups = sectionGroups;
//        this.offerName = offerName;
        this.sections = list;
    }

    public String getOffer() {
        return offer;
    }

    public int getQuantity() {
        return quantity;
    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

//    public PlaceDescriptionModel getOfferName() {
//        return offerName;
//    }
//
//    public void setOfferName(PlaceDescriptionModel offerName) {
//        this.offerName = offerName;
//    }

    public List<OfferIngradients> getSections() {
        return sections;
    }

    public void setSections(List<OfferIngradients> sections) {
        this.sections = sections;
    }

    public List<OfferIngradients> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<OfferIngradients> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }
}