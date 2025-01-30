package com.qurba.android.network.models;

import java.util.ArrayList;

public class DayModel {

    private String day;
    private boolean isDayOpen;
    private ArrayList<ShiftModel> shifts;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isDayOpen() {
        return isDayOpen;
    }

    public void setDayOpen(boolean dayOpen) {
        isDayOpen = dayOpen;
    }

    public ArrayList<ShiftModel> getShifts() {
        return shifts;
    }

    public void setShifts(ArrayList<ShiftModel> shifts) {
        this.shifts = shifts;
    }
}
