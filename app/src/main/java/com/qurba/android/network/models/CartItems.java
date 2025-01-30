package com.qurba.android.network.models;

import java.util.List;

public class CartItems {
    private String _id;
    private String name;
    private String cartItemId;

    private String imageUrl;
    private List<Sections> sections;
    private List<SectionsGroup> sectionsGroup;

    private double discountRatio;
    private int price;
    private int totalPrice;

    private float beforePrice;
    private int quantity;
    private String itemType;
    private String hashKey;
    private PlaceDescriptionModel title;
    private PlaceModel placeId;
    private String offerType;
    private boolean orderable;
    private String pictureUrl;
    private float deliverFees;
    private boolean isFreeDeliveryCanceled;
    private boolean available;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }

    public double getDiscountRatio() {
        return discountRatio;
    }

    public void setDiscountRatio(double discountRatio) {
        this.discountRatio = discountRatio;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getBeforePrice() {
        return beforePrice;
    }

    public void setBeforePrice(float beforePrice) {
        this.beforePrice = beforePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlaceModel getPlaceId() {
        return placeId;
    }

    public void setPlaceId(PlaceModel placeId) {
        this.placeId = placeId;
    }


    public PlaceDescriptionModel getTitle() {
        return title;
    }

    public void setTitle(PlaceDescriptionModel title) {
        this.title = title;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof CartItems) {
            if (getCartItemId() != null)
                return this.getCartItemId().equalsIgnoreCase(((CartItems) o).getCartItemId());
            else
                return this.get_id().equalsIgnoreCase(((CartItems) o).get_id());

        } else return false;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    public float getDeliverFees() {
        return deliverFees;
    }

    public void setDeliverFees(float deliverFees) {
        this.deliverFees = deliverFees;
    }

    public boolean isFreeDeliveryCanceled() {
        return isFreeDeliveryCanceled;
    }

    public void setFreeDeliveryCanceled(boolean freeDeliveryCanceled) {
        isFreeDeliveryCanceled = freeDeliveryCanceled;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<SectionsGroup> getSectionsGroup() {
        return sectionsGroup;
    }

    public void setSectionsGroup(List<SectionsGroup> sectionsGroup) {
        this.sectionsGroup = sectionsGroup;
    }
}
