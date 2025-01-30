package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.List;

public class OrderPlaceInfo implements Serializable {

    private String name;
    private String branchName;
    private String uniqueName;
    private String _id;
    private List<SubCategory> subCategories;
    private List<PlaceCategoryModel> categories;

    private String placeProfilePictureUrl;//
    private String PlaceProfilePicture;//
    private String mobileNumber;//
    private String hotLineNumber;//
    private String phoneNumber;//
    private String telephoneNumber;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<PlaceCategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<PlaceCategoryModel> categories) {
        this.categories = categories;
    }

    public String getPlaceProfilePictureUrl() {
        return placeProfilePictureUrl;
    }

    public void setPlaceProfilePictureUrl(String placeProfilePictureUrl) {
        this.placeProfilePictureUrl = placeProfilePictureUrl;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public String getPlaceProfilePicture() {
        return PlaceProfilePicture;
    }

    public void setPlaceProfilePicture(String placeProfilePicture) {
        PlaceProfilePicture = placeProfilePicture;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
