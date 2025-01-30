package com.qurba.android.network.models;

import com.qurba.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class PlaceDetailsModel {

    private PlaceDescriptionModel name;
    private PlaceDescriptionModel branchName;
    private PlaceDescriptionModel about;
    private PlaceDescriptionModel shortDescription;
    private AddressModel address;
    private String uniqueName;
    private String mobileNumber;
    private String hotLineNumber;
    private String telephoneNumber;
    private String status;
    private String placeProfileCoverPictureUrl;
    private String placeProfilePictureUrl;
    private boolean isHighlighted;
    private float placeRating;
    private int numberOfReviews;
    private ArrayList<PlaceCategoryModel> categories;
    private ArrayList<PlaceCategoryModel> subcategories;
    private ArrayList<PlaceCategoryModel> facilities;
    private List<NearbyPlaceModel> branches;
    private PlaceLocation location;
    private boolean isPlaceOpen;
    private boolean hasOpeningTimes;
    private boolean orderable;
    private TreeMap<String, DayModel> openingTimes;
    private ArrayList<String> placeGalleryPicturesUrls;
    private ArrayList<String> placeBranchesInfo;
    private PlaceDescriptionModel availability;

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

    public PlaceDescriptionModel getAbout() {
        return about;
    }

    public void setAbout(PlaceDescriptionModel about) {
        this.about = about;
    }

    public PlaceDescriptionModel getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(PlaceDescriptionModel shortDescription) {
        this.shortDescription = shortDescription;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getHotLineNumber() {
        return hotLineNumber;
    }

    public void setHotLineNumber(String hotLineNumber) {
        this.hotLineNumber = hotLineNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlaceProfileCoverPictureUrl() {
        return placeProfileCoverPictureUrl;
    }

    public void setPlaceProfileCoverPictureUrl(String placeProfileCoverPictureUrl) {
        this.placeProfileCoverPictureUrl = placeProfileCoverPictureUrl;
    }

    public String getPlaceProfilePictureUrl() {
        return placeProfilePictureUrl;
    }

    public void setPlaceProfilePictureUrl(String placeProfilePictureUrl) {
        this.placeProfilePictureUrl = placeProfilePictureUrl;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
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

    public ArrayList<PlaceCategoryModel> getFacilities() {
        return facilities;
    }

    public PlaceLocation getLocation() {
        return location;
    }

    public void setLocation(PlaceLocation location) {
        this.location = location;
    }

    public boolean isOpenNow() {
        return isPlaceOpen;
    }

    public void setOpenNow(boolean openNow) {
        this.isPlaceOpen = openNow;
    }

    public boolean isHasOpeningTimes() {
        return hasOpeningTimes;
    }

    public void setHasOpeningTimes(boolean hasOpeningTimes) {
        this.hasOpeningTimes = hasOpeningTimes;
    }

    public TreeMap<String, DayModel> getOpeningTimes() {
        return openingTimes;
    }

    public ArrayList<String> getPlaceGalleryPicturesUrls() {
        return placeGalleryPicturesUrls;
    }

    public void setPlaceGalleryPicturesUrls(ArrayList<String> placeGalleryPicturesUrls) {
        this.placeGalleryPicturesUrls = placeGalleryPicturesUrls;
    }

    public ArrayList<String> getPlaceBranchesInfo() {
        return placeBranchesInfo;
    }

    public void setPlaceBranchesInfo(ArrayList<String> placeBranchesInfo) {
        this.placeBranchesInfo = placeBranchesInfo;
    }

    public List<NearbyPlaceModel> getBranches() {
        return branches;
    }

    public void setBranches(List<NearbyPlaceModel> branches) {
        this.branches = branches;
    }

    public PlaceDescriptionModel getAvailability() {
        return availability;
    }

    public void setAvailability(PlaceDescriptionModel availability) {
        this.availability = availability;
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }
}
