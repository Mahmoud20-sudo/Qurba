package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.List;

public class OfferIngradients implements Serializable {
    private String itemId;
    private List<String> items;
    private String section;
    private String sectionGroup;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<String> getItemsIds() {
        return items;
    }

    public void setItemsIds(List<String> itemsIds) {
        this.items = itemsIds;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectionGroup() {
        return sectionGroup;
    }

    public void setSectionGroup(String sectionGroup) {
        this.sectionGroup = sectionGroup;
    }
}
