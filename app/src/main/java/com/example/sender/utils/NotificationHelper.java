package com.example.sender.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID = "sender_channel";

    public static void ensureChannel(Context ctx) {
        NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, "Sender audio", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }
    }

    public static Notification build(Context ctx) {
        ensureChannel(ctx);
        return new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle("Sender running")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .build();
    }
}
