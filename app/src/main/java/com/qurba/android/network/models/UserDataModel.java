package com.qurba.android.network.models;

import com.qurba.android.utils.SharedPreferencesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDataModel implements Serializable {

    private String _id;
    private String username;
    private String email;
    private String mobile;
    private boolean isEmailVerified;
    private boolean isMobileVerified;
    private boolean isFacebookAuthorized;
    private boolean isShowGetStarted;
    private String firstName;
    private String lastName;
    private List<String> previousProfilePicturesUrls;
    //    private ArrayList<String> birthday;
    private List<String> profileGalleryUrls;
    private List<PlaceCategoryModel> interestsCategories;
    private String role;
    private int userFollowingsCount;
    private int pmFollowingsCount;
    private int placeFollowingsCount;
    private int totalFollowingsCount;
    private UserSettings userSettings;
    private String jwt;

    private List<AddAddressModel> deliveryAddresses;
    private String profilePictureUrl;
//    private facebookUserFriendsData[] facebookUserFriendsDataiendsData;
//    private userFollowers[] userFollowers;
//    private pmFollowers[] pmFollowers;
//    private userFollowings[] userFollowings;
//    private pmFollowings[] pmFollowings;
//    private placeFollowings[] placeFollowings;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public boolean isMobileVerified() {
        return isMobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        isMobileVerified = mobileVerified;
    }

    public boolean isFacebookAuthorized() {
        return isFacebookAuthorized;
    }

    public void setFacebookAuthorized(boolean facebookAuthorized) {
        isFacebookAuthorized = facebookAuthorized;
    }

    public boolean isShowGetStarted() {
        return isShowGetStarted;
    }

    public void setShowGetStarted(boolean showGetStarted) {
        isShowGetStarted = showGetStarted;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getPreviousProfilePicturesUrls() {
        return previousProfilePicturesUrls;
    }

    public void setPreviousProfilePicturesUrls(List<String> previousProfilePicturesUrls) {
        this.previousProfilePicturesUrls = previousProfilePicturesUrls;
    }

//    public ArrayList<String> getBirthday() {
//        return birthday;
//    }
//
//    public void setBirthday(ArrayList<String> birthday) {
//        this.birthday = birthday;
//    }

    public List<String> getProfileGalleryUrls() {
        return profileGalleryUrls;
    }

    public void setProfileGalleryUrls(List<String> profileGalleryUrls) {
        this.profileGalleryUrls = profileGalleryUrls;
    }

    public List<PlaceCategoryModel> getInterestsCategories() {
        return interestsCategories;
    }

    public void setInterestsCategories(List<PlaceCategoryModel> interestsCategories) {
        this.interestsCategories = interestsCategories;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserFollowingsCount() {
        return userFollowingsCount;
    }

    public void setUserFollowingsCount(int userFollowingsCount) {
        this.userFollowingsCount = userFollowingsCount;
    }

    public int getPmFollowingsCount() {
        return pmFollowingsCount;
    }

    public void setPmFollowingsCount(int pmFollowingsCount) {
        this.pmFollowingsCount = pmFollowingsCount;
    }

    public int getPlaceFollowingsCount() {
        return placeFollowingsCount;
    }

    public void setPlaceFollowingsCount(int placeFollowingsCount) {
        this.placeFollowingsCount = placeFollowingsCount;
    }

    public int getTotalFollowingsCount() {
        return totalFollowingsCount;
    }

    public void setTotalFollowingsCount(int totalFollowingsCount) {
        this.totalFollowingsCount = totalFollowingsCount;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMobileNumber() {
        return mobile != null ?  String.format(new Locale(SharedPreferencesManager.getInstance().getLanguage()), "%d", 0 ) +
                String.format(new Locale(SharedPreferencesManager.getInstance().getLanguage()), "%d", Long.parseLong(
                        mobile.replace("+2", ""))) : "";
    }

    public String getENMobileNumber() {
        return mobile != null ?  String.format(Locale.ENGLISH, "%d", 0 ) +
                String.format(Locale.ENGLISH, "%d", Long.parseLong(
                        mobile.replace("+2", ""))) : "";
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobile = mobileNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public List<AddAddressModel> getDeliveryAddresses() {
        return deliveryAddresses == null ? new ArrayList<>() : deliveryAddresses;
    }

    public void setDeliveryAddresses(List<AddAddressModel> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }
}

