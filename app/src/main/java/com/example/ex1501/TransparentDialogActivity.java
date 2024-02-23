package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

public class TransparentDialogActivity extends AppCompatActivity {
    MainActivity mainInstance = MainActivity.getInstance();
    Intent gi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gi = getIntent();

        if (gi.getBooleanExtra("confirmAlarm", true)) {
            mainInstance.cancelAlarm(true);
            Calendar currentAlarm = Calendar.getInstance();
            currentAlarm.setTimeInMillis(mainInstance.getSavedAlarm());
            currentAlarm.add(Calendar.DATE, 1);

            mainInstance.increaseExactAlarmCode();

            mainInstance.setCurrentAlarm(currentAlarm);  // Sets for the next day
            mainInstance.setExactAlarm(currentAlarm);
            mainInstance.saveAlarmInFile(currentAlarm);
        }
        else {
            MainActivity.setSnoozeCounter(MainActivity.getSnoozeCounter() + 1);
            mainInstance.updateCurrentAlarmWithSnooze();
        }

        finish();

    }
}