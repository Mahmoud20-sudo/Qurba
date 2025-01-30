package com.qurba.android.network.models;

import java.util.ArrayList;
import java.util.List;


public class SimilarOffersModel{

    private String uniqueName;
    private List<String> galleryUrls;
    private PlaceLocation placeLocation;
    private int favoritesCount;
    private int claimsCount;
    private String _id;
    private String description;
    private String title;
    private double discountRatio;
    private String startDate;
    private String type;
    private String endDate;
    private String pictureUrl;
    private String placeId;
    private String PMId;
    private String createdAt;
    private String updatedAt;
    private String status;
    private String id;
    private boolean isFavoritedByUser;
    private boolean isClaimedByUser;

    private float price;

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public List<String> getGalleryUrls() {
        return galleryUrls;
    }

    public void setGalleryUrls(List<String> galleryUrls) {
        this.galleryUrls = galleryUrls;
    }

    public PlaceLocation getPlaceLocation() {
        return placeLocation;
    }

    public void setPlaceLocation(PlaceLocation placeLocation) {
        this.placeLocation = placeLocation;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public int getClaimsCount() {
        return claimsCount;
    }

    public void setClaimsCount(int claimsCount) {
        this.claimsCount = claimsCount;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDiscountRatio() {
        return discountRatio;
    }

    public void setDiscountRatio(double discountRatio) {
        this.discountRatio = discountRatio;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPMId() {
        return PMId;
    }

    public void setPMId(String PMId) {
        this.PMId = PMId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavoritedByUser() {
        return isFavoritedByUser;
    }

    public void setFavoritedByUser(boolean favoritedByUser) {
        isFavoritedByUser = favoritedByUser;
    }

    public boolean isClaimedByUser() {
        return isClaimedByUser;
    }

    public void setClaimedByUser(boolean claimedByUser) {
        isClaimedByUser = claimedByUser;
    }


    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
