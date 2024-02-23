package com.example.ex1501;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver which gets notified when an exact alarm is received. It displays an alert
 * dialog when active.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 19/2/2024
 */
public class ExactAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newActivityIntent = new Intent(context, TransparentDialogActivity.class);
        newActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newActivityIntent);
    }


}