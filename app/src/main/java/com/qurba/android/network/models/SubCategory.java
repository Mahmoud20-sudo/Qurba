package com.qurba.android.network.models;

public class SubCategory{

    private String subCategoryName;
    private PlaceDescriptionModel name;
    private String id;

    public SubCategory(){}

    public SubCategory(PlaceDescriptionModel name, String id) {
        this.name = name;
        this.id = id;
    }


    public SubCategory(String subCategoryName, String id) {
        this.subCategoryName = subCategoryName;
        this.id = id;
    }

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
