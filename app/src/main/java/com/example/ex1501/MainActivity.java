package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private final int HOURLY_ALARM_REQUEST_CODE = 1;
    private int EXACT_ALARM_REQUEST_CODE;
    private Context context = this;
    private TextView tvAlarmTime;
    private AlertDialog.Builder adb;
    private AlertDialog ad;
    private final static MainActivity instance = new MainActivity();
    private Calendar currentAlarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EXACT_ALARM_REQUEST_CODE = 0;
        tvAlarmTime = (TextView) findViewById(R.id.tvAlarmTime);
        setHourlyAlarm();
    }

    public void setHourlyAlarm() {
        Intent intent = new Intent(this, HourlyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this,
                HOURLY_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60 * 60 * 1000,
                AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    private void openTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Choose time");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                Toast.makeText(context, "Can't set alarm to the past or the present!",
                        Toast.LENGTH_LONG).show();
            }
            else {
                setExactAlarm(calSet);
            }
        }
    };

    public void chooseAlarmTime(View view) {
        openTimePickerDialog();
    }

    private void setExactAlarm(Calendar calSet) {
        Intent intent = new Intent(this, ExactAlarmReceiver.class);
        intent.putExtra("alarmNum", EXACT_ALARM_REQUEST_CODE);

        alarmIntent = PendingIntent.getBroadcast(this,
                EXACT_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calSet.getTimeInMillis(),
                5 * 60 * 1000, alarmIntent);

        tvAlarmTime.setText("Alarm is set to: " +  calSet.getTime());
        EXACT_ALARM_REQUEST_CODE++;
        currentAlarmTime = calSet;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void showAlertDialog(int alarmNum) {
        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);

        if(alarmNum == 0) {
            adb.setTitle("Alarm clock");
        }
        else {
            adb.setTitle("Snooze " + alarmNum);
        }

        adb.setMessage("Do you want confirm the alarm or set 5 minutes snooze?");

        // Confirm the alarm
        adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentAlarmTime.add(Calendar.DATE, 1);
                EXACT_ALARM_REQUEST_CODE = 0;
                setExactAlarm(currentAlarmTime);
            }
        });

        // Mute and snooze
        adb.setNegativeButton("Mute & Snooze", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        ad = adb.create();
        ad.show();
    }

    /**
     * This function performs an organized exit from the app.
     * @param view The button that was clicked in order to exit.
     */
    public void exitApp(View view) {
        finish();
    }
}