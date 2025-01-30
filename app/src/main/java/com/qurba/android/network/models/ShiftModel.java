package com.qurba.android.network.models;

import java.text.NumberFormat;

public class ShiftModel {

    private Time opens;
    private Time closes;

    public Time getOpens() {
        return opens;
    }

    public void setOpens(Time opens) {
        this.opens = opens;
    }

    public Time getCloses() {
        return closes;
    }

    public void setCloses(Time closes) {
        this.closes = closes;
    }


    public class Time {
        private int hour;
        private int minutes;

        public String getMinutes() {
            return minutes > 9 ?
                    NumberFormat.getInstance().format(minutes)
                    : NumberFormat.getInstance().format(0) + NumberFormat.getInstance().format(minutes);
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public String getHour() {
            return hour > 9 ?
                    NumberFormat.getInstance().format(hour)
                    : NumberFormat.getInstance().format(0) + NumberFormat.getInstance().format(hour);
        }

        public void setHour(int hour) {
            this.hour = hour;
        }
    }
}
