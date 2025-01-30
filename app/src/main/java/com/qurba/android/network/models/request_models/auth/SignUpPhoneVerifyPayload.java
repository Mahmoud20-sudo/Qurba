package com.qurba.android.network.models.request_models.auth;

public class SignUpPhoneVerifyPayload {
    private String mobile;
    private String mobileVerificationCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        if (mobile.startsWith("+2"))
            this.mobile = mobile;
        else
            this.mobile = "+2" + mobile;
    }

    public String getMobileVerificationCode() {
        return mobileVerificationCode;
    }

    public void setMobileVerificationCode(String mobileVerificationCode) {
        this.mobileVerificationCode = mobileVerificationCode;
    }
}
