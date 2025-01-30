package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.List;

public class OrderProducts implements Serializable {
    private float price;
    private ProductData product;
    private PlaceDescriptionModel productName;
    private String imageUrl;

    private int quantity;
    private List<Sections> sections;
    private List<SectionsGroup> sectionGroups;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(ProductData product) {
        this.product = product;
    }

    public int getPrice() {
        return Math.round(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public PlaceDescriptionModel getProductName() {
        return productName;
    }

    public void setProductName(PlaceDescriptionModel productName) {
        this.productName = productName;
    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }

    public List<SectionsGroup> getSectionGroups() {
        return sectionGroups;
    }

    public void setSectionGroups(List<SectionsGroup> sectionGroups) {
        this.sectionGroups = sectionGroups;
    }

    public String getProfilePictureUrl() {
        return imageUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.imageUrl = profilePictureUrl;
    }
}