package com.qurba.android.network.models.request_models.auth;

public class ForgotPhoneVerifyPayload {
    private String mobile;
    private String recoveryMobileVerificationCode;
    private String newPassword;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileVerificationCode() {
        return recoveryMobileVerificationCode;
    }

    public void setMobileVerificationCode(String mobileVerificationCode) {
        this.recoveryMobileVerificationCode = mobileVerificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
