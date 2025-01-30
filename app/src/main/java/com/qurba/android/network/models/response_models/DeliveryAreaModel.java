package com.qurba.android.network.models.response_models;

import java.util.List;

public class DeliveryAreaModel {

    private String type;
    private List<DeliveryAreaResponse> docs;
    private ErrorModel errorModel;
    private String errorType;

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DeliveryAreaResponse> getDocs() {
        return docs;
    }

    public void setDocs(List<DeliveryAreaResponse> docs) {
        this.docs = docs;
    }
}
