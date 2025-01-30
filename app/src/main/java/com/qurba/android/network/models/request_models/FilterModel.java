package com.qurba.android.network.models.request_models;

import java.util.ArrayList;

public class FilterModel {

//    "subCategoryIds": [],
//        "facilityIds": [],
//        "hashtagIds": []

    private ArrayList<String> categoryIds = new ArrayList<>();

    public ArrayList<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(ArrayList<String> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
