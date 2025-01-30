package com.qurba.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
//import com.crashlytics.android.Crashlytics;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transfermanager.internal.S3ProgressListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kwai.koom.javaoom.KOOM;
import com.mazenrashed.logdnaandroidclient.LogDna;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.qurba.android.BuildConfig;
import com.qurba.android.R;
import com.qurba.android.network.CognitoClient;
import com.qurba.android.network.QurbaLogger;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.io.File;
import java.util.Locale;

import io.branch.referral.Branch;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

import static android.app.admin.SecurityLog.LEVEL_ERROR;
import static android.app.admin.SecurityLog.LEVEL_INFO;

public class QurbaApplication extends Application {

    private static Context context;
    private static Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        initFacebook();
        initEmjoiManagerSDK();
        initSharedPrefManager();
        initFirebase();
        initBranch();
        initPlacesSDK();
        initLogDna();
        initKOOM();
        initIntercom();
        initCongito();
    }

    private void initEmjoiManagerSDK(){
        EmojiManager.install(new GoogleEmojiProvider());
    }

    private void initSharedPrefManager(){
        SharedPreferencesManager.getInstance().initialize(getApplicationContext());
    }


    private void initFirebase(){
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    private void initBranch(){
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }

    private void initPlacesSDK(){
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_places_api_key), new Locale("ar"));
        }
    }

    private void initLogDna(){
        LogDna.INSTANCE.init(BuildConfig.LogDNA_APIKEY, BuildConfig.TAG, "Qurba Android");
    }

    private void initKOOM(){
        KOOM.init(this);
    }

    private void initFacebook(){
        FacebookSdk.setIsDebugEnabled(BuildConfig.DEBUG);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        FacebookSdk.fullyInitialize();
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
    }

    private void initIntercom(){
        Intercom.initialize(this, BuildConfig.Intercome_APIKEY, BuildConfig.Intercome_SECRET);
    }

    private void initCongito(){
        CognitoClient.getService();
        if (SharedPreferencesManager.getInstance().getUser() != null) CognitoClient.refresh();
    }

    public static void setCurrentActivity(Activity currentActivity) {
        activity = currentActivity;
    }

    public static Activity currentActivity() {
        return activity;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
