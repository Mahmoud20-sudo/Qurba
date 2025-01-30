package com.qurba.android.network.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionsGroupRequests implements Serializable {

    private String sectionGroup;
    private List<OfferIngradients> sections = new ArrayList<>();

    public SectionsGroupRequests(String sectionGroup, OfferIngradients section){
        this.sectionGroup = sectionGroup;
        this.sections.add(section);
    }

    public SectionsGroupRequests(){}

    public String getSectionGroup() {
        return sectionGroup;
    }

    public void setSectionGroup(String sectionGroup) {
        this.sectionGroup = sectionGroup;
    }

    public List<OfferIngradients> getSections() {
        return sections;
    }

    public void setSections(List<OfferIngradients> sections) {
        this.sections = sections;
    }
}