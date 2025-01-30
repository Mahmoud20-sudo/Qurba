package com.qurba.android.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qurba.android.R;

public class LocationUtils {

    public static String calculateDistance(double myLat, double myLng, double placeLat, double placeLng) {

        double theta = myLng - placeLng;
        double dist = Math.sin(deg2rad(myLat))
                * Math.sin(deg2rad(placeLat))
                + Math.cos(deg2rad(myLat))
                * Math.cos(deg2rad(placeLat))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return NumberUtils.getOneCharacterAfterDecimalPoint(dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled && network_enabled;
    }

    public static void showOpenLocationDialog(Context context) {
        if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider callingd
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            ActivityCompat.requestPermissions((Activity) context
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , 1);
            return;
        }


    }

    @SuppressLint("MissingPermission")
    public static android.location.Location getCurrentLocation(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            String[] provider = new String[]
                    {
                            LocationManager.GPS_PROVIDER,
                            LocationManager.NETWORK_PROVIDER,
                            LocationManager.PASSIVE_PROVIDER
                    };

            Location location = null;
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                for (int i = 0; i < provider.length; i++) {
                    if (locationManager.isProviderEnabled(provider[i])) {
                        location = locationManager.getLastKnownLocation(provider[i]);
                        if (location != null) {
                            break;
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
            }
            return location;
        } else {
            return null;
        }


    }
}
