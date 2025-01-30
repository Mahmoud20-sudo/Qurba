package com.qurba.android.network.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlaceCategoryModel implements Serializable {

    private PlaceDescriptionModel name;
    private String _id;
    private String iconUrl;
    private String hoveredIconUrl;
    private String mobileIconUrl;
    private String mobileIconBackgroundUrl;
    private String createdAt;
    private String updatedAt;
    private PlaceDescriptionModel productTabName;
    private int __v;
    private List<PlaceDescriptionModel> subcategories;

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getHoveredIconUrl() {
        return hoveredIconUrl;
    }

    public void setHoveredIconUrl(String hoveredIconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public List<PlaceDescriptionModel> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<PlaceDescriptionModel> subcategories) {
        this.subcategories = subcategories;
    }

    public String getMobileIconBackgroundUrl() {
        return mobileIconBackgroundUrl;
    }

    public void setMobileIconBackgroundUrl(String mobileIconBackgroundUrl) {
        this.mobileIconBackgroundUrl = mobileIconBackgroundUrl;
    }

    public String getMobileIconUrl() {
        return mobileIconUrl;
    }

    public void setMobileIconUrl(String mobileIconUrl) {
        this.mobileIconUrl = mobileIconUrl;
    }

    public String getProductsTapName() {
        return productTabName != null ? productTabName.getEn() : null;
    }

    public void setProductsTapName(PlaceDescriptionModel productsTapName) {
        this.productTabName = productsTapName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PlaceCategoryModel) {
            if (((PlaceCategoryModel) obj).get_id().equalsIgnoreCase(get_id()))
                return true;
        }
        return false;
    }
}
