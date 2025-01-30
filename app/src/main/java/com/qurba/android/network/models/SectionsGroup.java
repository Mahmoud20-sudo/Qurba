package com.qurba.android.network.models;

import java.util.List;
public class SectionsGroup {

    private boolean required;
    private String _id;
    private PlaceDescriptionModel title;
    private List<Sections> sections;
    private boolean freeSectionGroup;
    private boolean discountSectionGroup;

    public String get_id() {
        return _id;
    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }

    public PlaceDescriptionModel getTitle() {
        return title;
    }

    public void setTitle(PlaceDescriptionModel title) {
        this.title = title;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionsGroup)) return false;
        if(_id.equals(((SectionsGroup) o)._id)) return true;

        return false;
    }

    public boolean isDiscountSectionGroup() {
        return discountSectionGroup;
    }

    public void setDiscountSectionGroup(boolean discountSectionGroup) {
        this.discountSectionGroup = discountSectionGroup;
    }

    public boolean isFreeSectionGroup() {
        return freeSectionGroup;
    }

    public void setFreeSectionGroup(boolean freeSectionGroup) {
        this.freeSectionGroup = freeSectionGroup;
    }
}
