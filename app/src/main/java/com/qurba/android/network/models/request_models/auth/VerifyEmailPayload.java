package com.qurba.android.network.models.request_models.auth;

public class VerifyEmailPayload {

    private String email;
    private String emailVerificationCode;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecoveryEmailVerificationCode() {
        return emailVerificationCode;
    }

    public void setRecoveryEmailVerificationCode(String emailVerificationCode) {
        this.emailVerificationCode = emailVerificationCode;
    }
}
