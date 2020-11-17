package ru.besttuts.stockwidget.service;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.besttuts.stockwidget.ui.EconomicWidget;
import ru.besttuts.stockwidget.util.LogUtils;
import ru.besttuts.stockwidget.util.SharedPreferencesHelper;

import static ru.besttuts.stockwidget.Config.PREF_PREFIX_KEY;
import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.LOGW;

public class AlfaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = LogUtils.makeLogTag(AlfaFirebaseMessagingService.class);

    //    public static final String FIREBASE_TOPIC = "alfa";
    public static final String FIREBASE_TOPIC = "alfatest";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        LOGD(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        LOGD(TAG, "token : " + token);

        saveFcmTokenPref(token);
        String username = getUsernamePref();
        Map<String, Object> user = new HashMap<>();

        if (null == username) {
            username = String.valueOf(UUID.randomUUID());
            saveUsernamePref(username);
            user.put("created", System.currentTimeMillis());
        }
        user.put("username", username);
        user.put("fcmToken", token);
        user.put("updated", System.currentTimeMillis());

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        mFirestore.setFirestoreSettings(settings);

        mFirestore.collection("users").document(String.valueOf(user.get("username")))
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LOGD(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LOGW(TAG, "Error writing document", e);
                    }
                });
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        LOGD(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LOGD(TAG, "Message Data payload: " + remoteMessage.getData());
            handleNow(remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LOGD(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(Map<String, String> map) {
        LOGD(TAG, "Short lived task is done.");
        for (Map.Entry<String, String> me : map.entrySet()) {
            LOGD(TAG, me.getKey() + " : " + me.getValue());
        }

        EconomicWidget.startMusic(getApplicationContext(), map.get("msg"));

        Intent intent = new Intent(getApplicationContext(), EconomicWidget.class);
        intent.setAction(EconomicWidget.UPDATE_ALL_WIDGETS);
        getApplicationContext().sendBroadcast(intent);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                        .setContentTitle("FCM Message")
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        LOGD(TAG, "onMessageSent: " + s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        LOGD(TAG, "onSendError: " + s + ", ex: " + e.getMessage());
    }

    private void saveFcmTokenPref(String token) {
        SharedPreferencesHelper.update(PREF_PREFIX_KEY + "_fcmToken", token);
    }

    private String getUsernamePref() {
        return (String) SharedPreferencesHelper.get(PREF_PREFIX_KEY + "_username", null);
    }

    private void saveUsernamePref(String username) {
        SharedPreferencesHelper.update(PREF_PREFIX_KEY + "_username", username);
    }
}
