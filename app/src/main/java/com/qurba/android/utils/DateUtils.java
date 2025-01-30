package com.qurba.android.utils;

import android.widget.Toast;

import com.qurba.android.R;

import java.security.PublicKey;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getTimeAgoFromTimeStamp(BaseActivity activity, String timeStamp) {
        String date = "";

        if (timeStamp != null && timeStamp.length() > 0) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                        , new Locale(SharedPreferencesManager.getInstance().getLanguage()));
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date past = format.parse(timeStamp);
                Date now = new Date();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                if (seconds < 60) {
//                    if (seconds == 1) {
//                        return NumberFormat.getInstance().format(seconds) + " " + activity.getString(R.string.second)
//                                ;
//                    } else if (seconds == -1) {
                    return activity.getString(R.string.just_now);
//                    } else {
//                        return NumberFormat.getInstance().format(seconds) + " " + activity.getString(R.string.seconds);
//                    }
                } else if (minutes < 60) {
                    if (minutes == 1) {
                        date = NumberFormat.getInstance().format(minutes) + " "
                                + activity.getString(R.string.minute);
                    } else {
                        date = NumberFormat.getInstance().format(minutes) + " " + activity.getString(R.string.minutes);
                    }
                } else if (hours < 24) {
                    if (hours == 2)
                        date = NumberFormat.getInstance().format(hours) + " " + activity.getString(R.string.two_hour);
                    else if (hours >= 3 && hours <= 10)
                        date = NumberFormat.getInstance().format(hours) + " " + activity.getString(R.string.hours);
                    else if ((hours >= 11 && hours <= 24) || hours == 1)
                        date = NumberFormat.getInstance().format(hours) + " " + activity.getString(R.string.hour);
                } else {
                    if (days == 1) {
                        date = NumberFormat.getInstance().format(days) + " " + activity.getString(R.string.day);
                    } else if (days > 6 && days < 29) {
                        double week = Math.floor(days / 7);
                        String weekHint = week > 1 ? NumberFormat.getInstance().format(week) + " " + activity.getString(R.string.weeks) : activity.getString(R.string.week);
                        date = weekHint;
                    } else if (days > 29 && days < 365) {
                        int month = (int) (days / 30);
                        String monthHint = month > 1 ? NumberFormat.getInstance().format(month) + " " + activity.getString(R.string.months)
                                : activity.getString(R.string.month);
                        date = monthHint;
                    } else if (days > 365) {
                        int years = Math.round(days / 365);
                        String yearsHint = years > 1 ? NumberFormat.getInstance().format(years) + " " + activity.getString(R.string.years) : activity.getString(R.string.year);
                        date = yearsHint;
                    } else {
                        date = NumberFormat.getInstance().format(days) + " " +
                                activity.getString(R.string.days);
                    }
                }
            } catch (Exception j) {
                j.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
        return SharedPreferencesManager.getInstance().getLanguage().equalsIgnoreCase("ar")
                ? activity.getString(R.string.ago) + "  " + date :
                date + " " + activity.getString(R.string.ago);
    }

    public static String getTimeUntilFromTimeStamp(String timeStamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getDefault());
            Date future = format.parse(timeStamp);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(future.getTime() - now.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(future.getTime() - now.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(future.getTime() - now.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(future.getTime() - now.getTime());

            if (future.before(now)) {
                return "Expired";
            } else {
                if (seconds < 60) {
                    if (seconds == 1) {
                        return seconds + " second left";
                    } else {
                        return seconds + " seconds left";
                    }
                } else if (minutes < 60) {
                    if (minutes == 1) {
                        return minutes + " minute left";
                    } else {
                        return minutes + " minutes left";
                    }
                } else if (hours < 24) {
                    if (hours == 1) {
                        return hours + " hour left";
                    } else {
                        return hours + " hours left";
                    }
                } else {
                    if (days == 1) {
                        return days + " day left";
                    } else {
                        return days + " days left";
                    }

                }
            }
        } catch (Exception j) {
            j.printStackTrace();
            return "";
        }
    }

    public static String getShortDateFromTimeStamp(String timeStamp) {
        if (timeStamp != null) {
            String shortDate = timeStamp.substring(0, timeStamp.indexOf("T"));
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = inputFormat.parse(shortDate);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "";
        } else {
            return "";
        }
    }

    public static String getLongDateFromTimeStamp(String timeStamp) {
        if (timeStamp != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy | hh:mm aa");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date date = null;
            try {
                date = format.parse(timeStamp);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getShortDate(String timeStamp) {
        if (timeStamp != null) {
            String shortDate = timeStamp.substring(0, timeStamp.indexOf("T"));
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd MMM yyy", new Locale(SharedPreferencesManager.getInstance().getLanguage()));
            Date date = null;
            try {
                date = inputFormat.parse(shortDate);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getCurrentDayName() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
    }

}
