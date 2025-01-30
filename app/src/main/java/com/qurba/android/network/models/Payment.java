package com.qurba.android.network.models;

import java.io.Serializable;

public class Payment implements Serializable {

    private String method;
    private boolean isPaid;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}