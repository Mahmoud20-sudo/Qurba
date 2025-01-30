package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderNowModel implements Serializable {
    private OrderNowPayload payload;

    private String errorType;
    private ArrayList<String> args;
    private String en;

    public OrderNowPayload getPayload() {
        return payload;
    }

    public void setPayload(OrderNowPayload payload) {
        this.payload = payload;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }
}