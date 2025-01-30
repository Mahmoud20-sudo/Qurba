package com.qurba.android.network;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.qurba.android.BuildConfig;
import com.qurba.android.R;
import com.qurba.android.utils.Constants;
import com.qurba.android.utils.DeveloperAuthenticationProvider;
import com.qurba.android.utils.QurbaApplication;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.qurba.android.utils.QurbaApplication.getContext;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class CognitoClient {
    private static TransferUtility transferUtility = null;
    private static DeveloperAuthenticationProvider developerProvider;
    private static Intent tsIntent;

    private synchronized static TransferUtility setClient() {

        developerProvider =
                new DeveloperAuthenticationProvider(null,
                        BuildConfig.COGNITO_IDENTITY_POOL_ID, Regions.EU_WEST_1);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),
                developerProvider,
                Regions.EU_WEST_1);

        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider, Region.getRegion(Regions.EU_WEST_1));

        transferUtility = TransferUtility.builder()
                .context(getContext())
                .s3Client(s3Client)
                .awsConfiguration(new AWSConfiguration(getContext()))
                .build();


        return transferUtility;
    }

    public static void stopService(){
        if(tsIntent != null)
            getContext().getApplicationContext().stopService(tsIntent);
    }

    public static void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Amazon channel",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_MIN);

            NotificationManager notificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(getContext(), "Amazon channel")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .build();

            tsIntent = new Intent(getContext().getApplicationContext(), TransferService.class);
            tsIntent.putExtra(TransferService.INTENT_KEY_NOTIFICATION, notification);
            tsIntent.putExtra(TransferService.INTENT_KEY_NOTIFICATION_ID, Constants.EDIT_PROFILE_SERVICE);
            tsIntent.putExtra(TransferService.INTENT_KEY_REMOVE_NOTIFICATION, true);
            getContext().getApplicationContext().startForegroundService(tsIntent);
        } else {
            getContext().startService(new Intent(getContext(), TransferService.class));
        }
    }

    public static void getService() {
        setClient();
    }

    public static void refresh() {
        if (SharedPreferencesManager.getInstance().getUser() != null)
            developerProvider.refresh();
    }

    public static TransferUtility getTransferUtility() {
        return transferUtility;
    }
}
