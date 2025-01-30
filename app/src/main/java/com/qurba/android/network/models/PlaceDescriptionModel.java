package com.qurba.android.network.models;

import com.qurba.android.utils.SharedPreferencesManager;

import java.io.Serializable;

public class PlaceDescriptionModel implements Serializable {

    private String en;
    private String ar;
    private String abbr;

    public String getEn() {
        return SharedPreferencesManager.getInstance().getLanguage().equalsIgnoreCase("ar")
                ? ar : en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getAr() {
        return ar;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
}
