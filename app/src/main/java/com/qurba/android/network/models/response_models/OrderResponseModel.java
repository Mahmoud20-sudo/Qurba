package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.OrdersModel;
import com.qurba.android.network.models.UserDataModel;

import java.util.ArrayList;
import java.util.List;

public class OrderResponseModel {

    private String type;
    private OrdersModel payload;
    private String errorType;
    private ArrayList<String> args;
    private String en;

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OrdersModel getPayload() {
        return payload;
    }

    public void setPayload(OrdersModel payload) {
        this.payload = payload;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }
}
