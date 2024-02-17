package com.example.ex1501;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class ExactAlarmReceiver extends BroadcastReceiver {

    int alarmNum;
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmNum = intent.getIntExtra("alarmNum", -1);

        MainActivity.getInstance().showAlertDialog(alarmNum);
    }


}