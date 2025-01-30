package com.qurba.android.network.models;

import java.util.ArrayList;

public class NearbyPlaceModel {

    private PlaceDescriptionModel name;
    private PlaceDescriptionModel branchName;
    private PlaceDescriptionModel shortDescription;
    private String _id;
    private float placeRating;
    private float priceRating;
    private int numberOfReviews;
    private ArrayList<String> offerIds;
    private ArrayList<PlaceCategoryModel> categories;
    private ArrayList<PlaceCategoryModel> subcategories;
    private ArrayList<PlaceCategoryModel> facilities;
    private PlaceLocation location;
    private String placeProfilePictureUrl;
    private String uniqueName;
    private int offersCount;
    private int activeOffersCount;
    private AddressModel address;
    private boolean openNow;
    private String id;
    private boolean isFollowedByUser;
    private boolean hasOpeningTimes;
    private String distance;

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public PlaceDescriptionModel getBranchName() {
        return branchName;
    }

    public void setBranchName(PlaceDescriptionModel branchName) {
        this.branchName = branchName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public ArrayList<String> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(ArrayList<String> offerIds) {
        this.offerIds = offerIds;
    }

    public ArrayList<PlaceCategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<PlaceCategoryModel> categories) {
        this.categories = categories;
    }

    public ArrayList<PlaceCategoryModel> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<PlaceCategoryModel> subcategories) {
        this.subcategories = subcategories;
    }

    public PlaceLocation getLocation() {
        return location;
    }

    public void setLocation(PlaceLocation location) {
        this.location = location;
    }

    public String getPlaceProfilePictureUrl() {
        return placeProfilePictureUrl;
    }

    public void setPlaceProfilePictureUrl(String placeProfilePictureUrl) {
        this.placeProfilePictureUrl = placeProfilePictureUrl;
    }

    public int getOffersCount() {
        return offersCount;
    }

    public void setOffersCount(int offersCount) {
        this.offersCount = offersCount;
    }

    public int getActiveOffersCount() {
        return activeOffersCount;
    }

    public void setActiveOffersCount(int activeOffersCount) {
        this.activeOffersCount = activeOffersCount;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFollowedByUser() {
        return isFollowedByUser;
    }

    public void setFollowedByUser(boolean followedByUser) {
        isFollowedByUser = followedByUser;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public boolean isHasOpeningTimes() {
        return hasOpeningTimes;
    }

    public void setHasOpeningTimes(boolean hasOpeningTimes) {
        this.hasOpeningTimes = hasOpeningTimes;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public PlaceDescriptionModel getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(PlaceDescriptionModel shortDescription) {
        this.shortDescription = shortDescription;
    }

    public float getPriceRating() {
        return priceRating;
    }

    public void setPriceRating(float priceRating) {
        this.priceRating = priceRating;
    }
}
