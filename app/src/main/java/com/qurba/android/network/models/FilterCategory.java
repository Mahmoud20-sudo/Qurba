package com.qurba.android.network.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class FilterCategory  {


    public FilterCategory(String id, String name, List<SubCategory> subCategories, boolean checked) {
        this.id = id;
        this.name = name;
        this.subCategories = subCategories;
        this.checked = checked;
    }

    private String id;
    private String name;
    private List<SubCategory> subCategories;
    private boolean checked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}


