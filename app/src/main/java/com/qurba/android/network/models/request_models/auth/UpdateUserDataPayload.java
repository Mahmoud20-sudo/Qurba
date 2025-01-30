package com.qurba.android.network.models.request_models.auth;

public class UpdateUserDataPayload {

    private String firstName;
    private String lastName;
    private String mobile;
    private String profilePictureUrl;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        if (mobile.startsWith("+2"))
            this.mobile = mobile;
        else
            this.mobile = "+2" + mobile;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
