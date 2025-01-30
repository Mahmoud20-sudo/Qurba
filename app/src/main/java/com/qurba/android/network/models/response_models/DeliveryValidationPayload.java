package com.qurba.android.network.models.response_models;


public class DeliveryValidationPayload {
    private DeliveryValidationResponse payload;
    private String type;
    private String errorType;
    private String args;
    private String en;
    private String ar;

    public DeliveryValidationResponse getPayload() {
        return payload;
    }

    public void setPayload(DeliveryValidationResponse payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getAr() {
        return ar;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}

