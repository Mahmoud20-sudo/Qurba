package com.qurba.android.utils.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qurba.android.R;
import com.qurba.android.ui.splash.views.SplashActivity;
import com.qurba.android.utils.Constants;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import io.intercom.android.sdk.push.IntercomPushClient;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Bitmap bitmap;

    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));

        IntercomPushClient intercomPushClient = new IntercomPushClient();
        Map<String, String> message = remoteMessage.getData();

        if (intercomPushClient.isIntercomPush(message)) {
            intercomPushClient.handlePush(getApplication(), message);
        } else {
            // DO HOST LOGIC HERE

            Intent intent = new Intent(this, SplashActivity.class);
            Bundle bundle = new Bundle();
            try {
                bundle.putString("branch", remoteMessage.getData().get("link"));
                bundle.putString("link", remoteMessage.getData().get("link"));
                bundle.putBoolean("is-from-fcm", true);
                bundle.putBoolean("branch_force_new_session", true);
                bundle.putBoolean("is_first_session", true);
            } catch (Exception exception) {
                bundle.putBoolean("FCM_TYPE", true);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(bundle);
//        SharedPreferencesManager.getInstance().setFCMLink (remoteMessage.getData().get("link"));

            PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent, PendingIntent.FLAG_ONE_SHOT);

            //To get a Bitmap image from the URL received

//        float multiplier = getImageFactor(getResources());
            bitmap = getBitmapfromUrl(remoteMessage.getData().get("imageUrl"));

            Resources res = getResources();
            int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
            int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);

            if (bitmap != null)
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            String channelId = "Default";
            NotificationCompat.Builder builder = bitmap != null ? new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setLargeIcon(bitmap)/*Notification icon image*/
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap))/*Notification with Image*/
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(remoteMessage.getData().get("body")))
                    .setAutoCancel(true)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                    .setContentIntent(pendingIntent)
                    :
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                            .setContentTitle(remoteMessage.getData().get("title"))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(remoteMessage.getData().get("body")))
                            .setAutoCancel(true).setContentIntent(pendingIntent);

            //.setContentText(remoteMessage.getData().get("body"))
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
                channel.setShowBadge(false);
                manager.createNotificationChannel(channel);
            }

            manager.notify(getNotificationId(), builder.build());

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.REFRESH_ORDER_STATUS));

        }
    }


    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
        new IntercomPushClient().sendTokenToIntercom(getApplication(), token);

    }

    public static float getImageFactor(Resources r) {
        DisplayMetrics metrics = r.getDisplayMetrics();
        float multiplier = metrics.density / 3f;
        return multiplier;
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}