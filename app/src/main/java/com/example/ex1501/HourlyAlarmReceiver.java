package com.example.ex1501;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Broadcast receiver which gets notified when an hourly alarm is received. It displays a toast
 * message when active.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 18/2/2024
 */
public class HourlyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Hourly alarm!", Toast.LENGTH_LONG).show();
    }
}