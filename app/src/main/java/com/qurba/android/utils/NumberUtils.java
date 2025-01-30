package com.qurba.android.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

//    public static double getOneCharacterAfterDecimalPoint(double source){
//        double x=source;
//        DecimalFormat df = new DecimalFormat("#.#");
//        String dx=df.format(x);
//        x=Double.valueOf(dx);
//        return x;
//    }

    public static String getOneCharacterAfterDecimalPoint(double source) {
        double x = source;
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale(SharedPreferencesManager.getInstance().getLanguage()));
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.#");
//        DecimalFormat df = new DecimalFormat("#.#");
        String dx = df.format(x);
//        x = Double.valueOf(dx);
        return dx;
    }


    public static String getFinaleDiscountValue(double discountValue) {
        int discount = (int) (discountValue * 100);
        return NumberFormat.getInstance().format(discount) + "" + "%";
    }
}
