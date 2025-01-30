package com.qurba.android.network.models.request_models.auth;

public class ForgotEmailVerifyPayload {
    private String email;
    private String recoveryEmailVerificationCode;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerificationCode() {
        return recoveryEmailVerificationCode;
    }

    public void setEmailVerificationCode(String emailVerificationCode) {
        this.recoveryEmailVerificationCode = emailVerificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
