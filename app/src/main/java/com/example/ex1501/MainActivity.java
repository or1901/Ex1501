package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent hourlyAlarmIntent;
    private int HOURLY_ALARM_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HOURLY_ALARM_REQUEST_CODE = 0;
        setHourlyAlarm();
    }

    public void setHourlyAlarm() {
        Calendar calNow = Calendar.getInstance();
        Intent intent = new Intent(this, HourlyAlarmReceiver.class);
        hourlyAlarmIntent = PendingIntent.getBroadcast(this,
                HOURLY_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calNow.getTimeInMillis() + 60 * 60 * 1000,
                AlarmManager.INTERVAL_HOUR, hourlyAlarmIntent);
    }

    /**
     * This function performs an organized exit from the app.
     * @param view The button that was clicked in order to exit.
     */
    public void exitApp(View view) {
        finish();
    }
}