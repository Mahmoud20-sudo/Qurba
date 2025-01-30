package com.qurba.android.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    public static boolean validateEmail (String email) {
        return (!TextUtils.isEmpty (email) && Patterns.EMAIL_ADDRESS.matcher (email).matches ());
    }

    public static boolean validatePasswordLength(String password){
        return (password.equals ("") || password.isEmpty () || password.trim ().length () < 6);
    }

    public static boolean validatePhoneLength(String phone){
        return (phone.equals ("") || phone.isEmpty () || phone.trim ().length () < 10);
    }

    public static boolean isEnglishText(final String iStringToCheck) {
        return iStringToCheck.matches("^[a-zA-Z0-9.]+$");
    }
}
