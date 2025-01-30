package com.qurba.android.network.models.request_models.auth;

public class PhoneVerifyPayload {
    private String mobile;
    private String code;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        if (mobile.startsWith("+20"))
            this.mobile = mobile;
        else
            this.mobile = "+2" + mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
