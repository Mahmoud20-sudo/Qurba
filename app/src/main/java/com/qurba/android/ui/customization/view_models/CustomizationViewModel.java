package com.qurba.android.ui.customization.view_models;

import android.app.Application;

import androidx.annotation.NonNull;

import com.qurba.android.network.models.SectionItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroup;
import com.qurba.android.utils.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class CustomizationViewModel extends BaseViewModel {

    private final Application application;
    boolean isReady;
    List<Sections> sectionItems = new ArrayList<>();
    float offerPrice;
    int numberOfRequiredSection = 0;
    private int numberOfSelectedRequiredSection = 0;
    private float previousPrice;
    private boolean isVariablePrice;
    private int basePrice;

    public void setVariablePrice(boolean variablePrice) {
        isVariablePrice = variablePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isVariablePrice() {
        return isVariablePrice;
    }

    public boolean checkIfGroupSectionAlreadySelected(Sections sectionModel) {
        if (sectionModel.getSectionsGroup() != null) {
            for (Sections sec : sectionItems) {//logic for group of sections
                if (sec.getSectionsGroup() != null
                        && !sec.get_id().equalsIgnoreCase(sectionModel.get_id()) &&
                        sec.getSectionsGroup().get_id().equalsIgnoreCase(sectionModel.getSectionsGroup().get_id()))
                    return true;
            }
        }
        return false;
    }

    public CustomizationViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setReady(boolean ready) {
        isReady = ready;
        notifyDataChanged();
    }

    void checkIfHaveRequiredSections(List<SectionsGroup> sectionsGroups, List<Sections> sections) {
        boolean isRequired = false;
        if (sectionsGroups != null)
            for (SectionsGroup group : sectionsGroups) {
                if (group.isRequired()) {
                    numberOfRequiredSection++;
                    isRequired = true;
                }
            }

        for (Sections sec : sections) {
            if (sec.isRequired()) {
                numberOfRequiredSection++;
                isRequired = true;
            }
        }
        setReady(!isRequired);
    }

    public void setOfferPrice() {
        this.offerPrice = 0f;
        String previousGroupdId = "";
        String previousSectionId = "";
        this.numberOfSelectedRequiredSection = 0;
        for (Sections sec : sectionItems) {
            if (sec.getSectionsGroup() != null) {
                if (sec.getSectionsGroup().isRequired() && sec.getItems().size()
                        >= sec.getMinimumChoice() && !sec.getSectionsGroup().get_id().equalsIgnoreCase(previousGroupdId)) {
                    previousGroupdId = sec.getSectionsGroup().get_id();
                    numberOfSelectedRequiredSection++;
                }
            } else if (sec.isRequired()) {
                if (sec.isRequired() && sec.getItems().size()
                        >= sec.getMinimumChoice() && !sec.get_id().equalsIgnoreCase(previousSectionId)) {
                    previousSectionId = sec.get_id();
                    numberOfSelectedRequiredSection++;
                }
            }
            else if(sec.getItems().size()
                    < sec.getMinimumChoice()){
                numberOfSelectedRequiredSection--;
            }
            for (SectionItems item : sec.getItems()) {
                offerPrice += Math.round(item.getPrice());
            }
        }
        offerPrice += basePrice;
        notifyDataChanged();
    }

    public void removeSection(Sections sections) {
        if (sectionItems.contains(sections)) {
            sectionItems.remove(sections);
            this.offerPrice -= previousPrice;
        }

        setOfferPrice();
        notifyDataChanged();
        //all required item are selected
        if (numberOfRequiredSection > numberOfSelectedRequiredSection)
            setReady(false);
    }

    public void addSection(Sections sectionModel) {
        if (sectionItems.contains(sectionModel)) {
            sectionItems.remove(sectionModel);
            this.offerPrice -= previousPrice;
        }
//        if (sectionModel.getSectionsGroup() != null) {
//            for (Sections sec : sectionItems) {//logic for group of sections
//                if (sec.getSectionsGroup() != null &&
//                        sec.getSectionsGroup().get_id().equalsIgnoreCase(sectionModel.getSectionsGroup().get_id()))
//                    return false;
//            }
//        }
        sectionItems.add(sectionModel);
        setOfferPrice();
        this.previousPrice = sectionModel.getItems().get(0).getPrice();
        //all required item are selected
        if (numberOfRequiredSection == numberOfSelectedRequiredSection)
            setReady(true);
        else
            setReady(false);
    }

    public void addSectionWithCheckBox(Sections sectionModel) {

        if (sectionItems.contains(sectionModel))
            sectionItems.get(sectionItems.indexOf(sectionModel)).setItems(sectionModel.getItems());
        else
            sectionItems.add(sectionModel);

        this.setOfferPrice();
        this.previousPrice = offerPrice;
        //all required item are selected
        if (numberOfRequiredSection == numberOfSelectedRequiredSection)
            setReady(true);
        else
            setReady(false);

    }

    public void removeSectionWithCheckBox(Sections sectionModel) {
        if (!sectionItems.contains(sectionModel) || sectionModel.getItems().isEmpty())
            sectionItems.remove(sectionModel);
        else
            sectionItems.get(sectionItems.indexOf(sectionModel)).setItems(sectionModel.getItems());

        this.setOfferPrice();
        this.previousPrice = offerPrice;
        //all required item are selected
        if (numberOfRequiredSection > numberOfSelectedRequiredSection)
            setReady(false);
    }

}