package com.example.ex1501;

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
    int alarmNum;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmNum = intent.getIntExtra("alarmNum", -1);

        MainActivity.getInstance().showAlertDialog(alarmNum);
    }


}