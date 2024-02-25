package com.example.ex1501;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences spAlarmFile = (SharedPreferences) context.getSharedPreferences("SAVED_ALARMS", MODE_PRIVATE);

        MainActivity.initSpObj(context);

        long alarm = MainActivity.getSavedAlarm();
        Calendar savedAlarm = Calendar.getInstance();
        savedAlarm.setTimeInMillis(alarm);

        MainActivity.setExactAlarm(savedAlarm, context);
        Toast.makeText(context, "" + savedAlarm.getTime(), Toast.LENGTH_LONG).show();
    }
}