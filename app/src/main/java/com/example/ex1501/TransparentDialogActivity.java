package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

/**
 * An activity which handles the user's choice from the notification.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 18/2/2024
 */
public class TransparentDialogActivity extends AppCompatActivity {
    Context mainInstance = MainActivity.getInstance();
    Intent gi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gi = getIntent();

        if (gi.getBooleanExtra("confirmAlarm", true)) {
            MainActivity.cancelAlarm(mainInstance);
            Calendar currentAlarm = Calendar.getInstance();
            currentAlarm.setTimeInMillis(MainActivity.getSavedAlarm());
            currentAlarm.add(Calendar.DATE, 1);

            MainActivity.setExactAlarmCode(MainActivity.getExactAlarmCode() + 1);

            MainActivity.setExactAlarm(currentAlarm, mainInstance);
            MainActivity.saveAlarmInFile(currentAlarm);
        }
        else {
            MainActivity.setSnoozeCounter(MainActivity.getSnoozeCounter() + 1);
            MainActivity.updateCurrentAlarmWithSnooze();
        }

        finish();

    }
}