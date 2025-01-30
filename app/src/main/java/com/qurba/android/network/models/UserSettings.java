package com.qurba.android.network.models;

import java.io.Serializable;

public class UserSettings implements Serializable {

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
