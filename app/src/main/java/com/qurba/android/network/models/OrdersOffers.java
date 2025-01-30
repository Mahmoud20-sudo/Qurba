package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.List;

public class OrdersOffers implements Serializable {
    private OffersModel offer;
    private int quantity;
    private float price;
    private String pictureUrl;

    //    private PlaceDescriptionModel offerName;
    private List<Sections> sections;

    public OffersModel getOffer() {
        return offer;
    }

    public int getQuantity() {
        return quantity;
    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }

    public int getPrice() {
        return Math.round(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

//    public PlaceDescriptionModel getOfferName() {
//        return offerName;
//    }
//
//    public void setOfferName(PlaceDescriptionModel offerName) {
//        this.offerName = offerName;
//    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }

    public String getProfilePictureUrl() {
        return pictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.pictureUrl = profilePictureUrl;
    }
}