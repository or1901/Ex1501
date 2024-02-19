package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

public class TransparentDialogActivity extends AppCompatActivity {
    private AlertDialog.Builder adb;
    private AlertDialog ad;
    MainActivity mainInstance = MainActivity.getInstance();
    Intent gi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gi = getIntent();
        showAlertDialog();
    }

    /**
     * This function shows the alert dialog when an exact alarm is received.
     * It has two options - snooze or confirm the alarm.
     */
    public void showAlertDialog() {
        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);

        // Sets the title according to the alarm number
        if(mainInstance.getSnoozeCounter() == 0) {
            adb.setTitle("Alarm clock");
        }
        else {
            adb.setTitle("Snooze " + mainInstance.getSnoozeCounter());
        }

        adb.setMessage("Do you want confirm the alarm or set 5 minutes snooze?");

        // Confirm the alarm
        adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar currentAlarm = mainInstance.getCurrentAlarm();
                mainInstance.cancelAlarm(true);
                mainInstance.increaseExactAlarmCode();
                currentAlarm.add(Calendar.DATE, 1);
                mainInstance.setCurrentAlarm(currentAlarm);  // Sets for the next day
                mainInstance.setExactAlarm(currentAlarm);
                finish();
            }
        });

        // Mute and snooze
        adb.setNegativeButton("Mute & Snooze", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainInstance.increaseSnoozeCounter();
                finish();
            }
        });

        ad = adb.create();
        ad.show();
    }
}