package com.example.fire_and_based;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class AnnouncementService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "Announcement_Channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("AnnouncementService", "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("AnnouncementService", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            displayNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void displayNotification(String messageTitle, String messageBody) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Announcement Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.fire_icon_free_png)  // replace with your own icon
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
