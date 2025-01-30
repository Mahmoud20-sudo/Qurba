package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.List;

public class OrderProductsRequests implements Serializable {
    private String product;
    private int quantity;
    private transient float price;
    private String productName;
    private List<SectionsGroupRequests> sectionGroups;
    private List<OfferIngradients> sections;
//    private boolean available;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<OfferIngradients> getSections() {
        return sections;
    }

    public void setSections(List<OfferIngradients> sections) {
        this.sections = sections;
    }

    public List<SectionsGroupRequests> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<SectionsGroupRequests> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }

//    public boolean isAvailable() {
//        return available;
//    }
//
//    public void setAvailable(boolean available) {
//        this.available = available;
//    }
}