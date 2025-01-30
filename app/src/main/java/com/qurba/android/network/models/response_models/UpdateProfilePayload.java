package com.qurba.android.network.models.response_models;

import com.qurba.android.network.models.CommentModel;
import com.qurba.android.network.models.PlaceCategoryModel;
import com.qurba.android.network.models.PlaceDescriptionModel;
import com.qurba.android.network.models.PlaceLocation;
import com.qurba.android.network.models.PlaceModel;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.List;

public class UpdateProfilePayload {

    private int code;
    private String en;
    private String ar;

    public String getMessage() {
        return SharedPreferencesManager.getInstance().getLanguage().equalsIgnoreCase("ar") ? ar : en;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getEn() {
        return en;
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
}
