package com.example.ex1501;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * Helper class for creating notifications.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 24/2/2024
 */
public class NotificationHelper {
    private static final String CHANNEL_ID = "Alarm_Channel_ID";
    private static final String CHANNEL_NAME = "Alarm_Channel";
    private static final int NOTIFICATION_ID = 1;

    public static void showNotificationTwoBtns(Context context, String title) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Intent confirmIntent = new Intent(context, TransparentDialogActivity.class);
        confirmIntent.putExtra("confirmAlarm", true);
        PendingIntent confirmPendingIntent = PendingIntent.getActivity(context,
                -1, confirmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(context, TransparentDialogActivity.class);
        snoozeIntent.putExtra("confirmAlarm", false);
        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context,
                -2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        "Confirm", confirmPendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        "Snooze", snoozePendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
