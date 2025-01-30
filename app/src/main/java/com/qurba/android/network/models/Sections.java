package com.qurba.android.network.models;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private boolean freeSection;
    private boolean discountSection;
    private boolean required;
    private String _id;
    private PlaceDescriptionModel title;
    private PlaceDescriptionModel name;
    private PlaceDescriptionModel description;
    private boolean multipleChoice;
    private transient List<SectionItems> selectedItems = new ArrayList<>();
    private List<SectionItems> items = new ArrayList<>();
    private transient List<SectionItems> backupItems = new ArrayList<>();
    private int minimumChoice;
    private int maximumChoice;
    private SectionsGroup sectionsGroup;

//    public void setSelectedItem(List<SectionItems> selectedItem) {
//        this.selectedItem = selectedItem;
//    }

    public List<SectionItems> getSelectedItem() {
        return selectedItems;
    }

    public boolean isFreeSection() {
        return freeSection;
    }

    public void setFreeSection(boolean freeSection) {
        this.freeSection = freeSection;
    }

    public boolean isDiscountSection() {
        return discountSection;
    }

    public void setDiscountSection(boolean discountSection) {
        this.discountSection = discountSection;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public PlaceDescriptionModel getTitle() {
        return title;
    }

    public void setTitle(PlaceDescriptionModel title) {
        this.title = title;
    }

    public List<SectionItems> getItems() {
        return items;
    }

    public void setItems(List<SectionItems> items) {
        this.items = items;
    }

    public PlaceDescriptionModel getDescription() {
        return description;
    }

    public void setDescription(PlaceDescriptionModel description) {
        this.description = description;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public PlaceDescriptionModel getName() {
        return name;
    }

    public void setName(PlaceDescriptionModel name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sections)) return false;
        if (_id.equals(((Sections) o)._id)) return true;

        return false;
    }

    public int getMaximumChoice() {
        return maximumChoice;
    }

    public void setMaximumChoice(int maximumChoice) {
        this.maximumChoice = maximumChoice;
    }

    public int getMinimumChoice() {
        return minimumChoice;
    }

    public void setMinimumChoice(int minimumChoice) {
        this.minimumChoice = minimumChoice;
    }

    public SectionsGroup getSectionsGroup() {
        return sectionsGroup;
    }

    public void setSectionsGroup(SectionsGroup sectionsGroup) {
        this.sectionsGroup = sectionsGroup;
    }

    public List<SectionItems> getBackupItems() {
        return backupItems;
    }

    public void setBackupItems(List<SectionItems> backupItems) {
        this.backupItems = backupItems;
    }

    public void setSelectedItems(List<SectionItems> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
