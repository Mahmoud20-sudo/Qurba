package com.qurba.android.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.google.gson.JsonArray;
import com.qurba.android.R;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.CartModel;
import com.qurba.android.network.models.DeliveryAddress;
import com.qurba.android.network.models.OfferIngradients;
import com.qurba.android.network.models.Offers;
import com.qurba.android.network.models.OrderProductsRequests;
import com.qurba.android.network.models.Payment;
import com.qurba.android.network.models.SectionItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroupRequests;
import com.qurba.android.network.models.request_models.CreateOrdersPayload;
import com.qurba.android.ui.splash.views.SplashActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.qurba.android.utils.Constants.OFFER_TYPE_FREE_DELIVERY;

public class SystemUtils {

    public static String getDeviceID(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        String fileName = "unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.getLastPathSegment().toString();
        } else {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return fileName;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }

    public static String getTextSize(String text, int size) {
        return "<span style=\"size:" + size + "\" >" + text + "</span>";

    }

    public static boolean containsDigit(final String aString) {
        if (aString != null && !aString.isEmpty()) {
            for (char c : aString.toCharArray()) {
                if (Character.isDigit(c)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void resetData() {
        SharedPreferencesManager sharedPref = SharedPreferencesManager.getInstance();
        sharedPref.setOfferPosition(-1);
        sharedPref.setPlacePosition(-1);
        sharedPref.setProductPosition(-1);
        sharedPref.setPlacePosition(-1);
        sharedPref.setIsAppRunning(false);
        sharedPref.setOrdering(false);
    }

    public static String setLocalizedDays(Context context, String englishDayName) {
        String arabicDay = "";

        switch (englishDayName.toLowerCase()) {
            case "saturday":
                arabicDay = context.getString(R.string.saturday);
                break;
            case "sunday":
                arabicDay = context.getString(R.string.sunday);
                break;
            case "monday":
                arabicDay = context.getString(R.string.monday);
                break;
            case "tuesday":
                arabicDay = context.getString(R.string.tuesday);
                break;
            case "wednesday":
                arabicDay = context.getString(R.string.wednesday);
                break;
            case "thursday":
                arabicDay = context.getString(R.string.thursday);
                break;
            case "friday":
                arabicDay = context.getString(R.string.friday);
                break;
        }
        return arabicDay;
    }

    public static int getWeekDaysIndex(String weekday) {
        switch (weekday.toLowerCase()) {
            case "saturday":
                return 0;
            case "sunday":
                return 1;
            case "monday":
                return 2;
            case "tuesday":
                return 3;
            case "wednesday":
                return 4;
            case "thursday":
                return 5;
            default:
                return 6;
        }
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isGPSActive(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static void callPhone(String phoneNumber, Activity activity) {
        int REQUEST_PHONE_CALL = 1;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            activity.startActivity(intent);
        }

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static void restartAppWithoutClear(BaseActivity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean("branch_force_new_session", true);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

    public static void restartApp(BaseActivity activity) {
        SharedPreferencesManager.getInstance().setSelectedCachedArea(null);
        SharedPreferencesManager.getInstance().setSavedDeliveryAddress(null);
        SharedPreferencesManager.getInstance().clearCart();

        Intent intent = new Intent(activity, SplashActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean("branch_force_new_session", true);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finishAffinity();
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static boolean isAllNulls(Iterable<?> array) {
        for (Object element : array)
            if (element != null) return false;
        return true;
    }
}

