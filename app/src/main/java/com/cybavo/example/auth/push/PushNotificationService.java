package com.cybavo.example.auth.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cybavo.auth.service.api.Callback;
import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.auth.service.auth.results.SetPushTokenResult;
import com.cybavo.auth.service.notification.PushNotification;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.MainActivity;
import com.cybavo.example.auth.service.ServiceHolder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = PushNotificationService.class.getSimpleName();

    private final static String NOTIFICATION_CHANNEL_ID = "CYBAVO-Auth-Example";
    private static final int NOTIFICATION_ID = 99;


    private final static MutableLiveData<Map<String, String>> sNewActions = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "from: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "notification: " + remoteMessage.getNotification());
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "data: " + data);
            final String payload = data.get("pinpoint.jsonBody");
            if (TextUtils.isEmpty(payload)) {
                Log.d(TAG, "Empty body: " + data);
                return;
            }

            PushNotification pushNotification = PushNotification.parse(payload);
            onReceive2FaPush(pushNotification.title, pushNotification.body, pushNotification);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM onNewToken: " + token);
        Authenticator authenticator = ServiceHolder.get(getApplicationContext());
        if (authenticator.getPairings().length > 0) {
            authenticator.setPushToken(token, new Callback<SetPushTokenResult>() {
                @Override
                public void onResult(SetPushTokenResult setPushTokenResult) {
                    // Good
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "setPushToken failed", error);
                }
            });
        }
    }

    private void initNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(channel);
        }
    }

    private void onReceive2FaPush(String title, String body, PushNotification pushNotification) {
        Log.d(TAG, "onReceive2FaPush: " + pushNotification.devices);
        sNewActions.postValue(pushNotification.devices);

        Intent intent = new Intent(this, MainActivity.class).
                setAction(Intent.ACTION_MAIN).
                addCategory(Intent.CATEGORY_LAUNCHER).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID).
                setContentIntent(pendingIntent).
                setSmallIcon(R.drawable.ic_security).
                setPriority(NotificationCompat.PRIORITY_DEFAULT).
                setContentTitle(title).
                setContentText(body)
                .setAutoCancel(true);

        NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, builder.build());
    }

    public static LiveData<Map<String, String>> getNewActions() {
        return sNewActions;
    }

    public static void clearNewActions() {
        sNewActions.setValue(null);
    }
}
