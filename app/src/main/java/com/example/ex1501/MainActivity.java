package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * The main activity:
 * allows to configure a daily alarm(only one exists at a time). If not configured,
 * the default alarm is approximately once a hour.
 * @author Ori Roitzaid <or1901 @ bs.amalnet.k12.il>
 * @version	1
 * @since 18/2/2024
 */
public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private final int HOURLY_ALARM_REQUEST_CODE = 1;
    private TextView tvAlarmTime;
    private Calendar currentAlarmTime;
    boolean currentAlarmType;
    static SharedPreferences spAlarmFile;
    static SharedPreferences.Editor editor;
    private static MainActivity instance;
    private long savedAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAlarmTime = (TextView) findViewById(R.id.tvAlarmTime);

        spAlarmFile = (SharedPreferences) getSharedPreferences("SAVED_ALARMS", MODE_PRIVATE);

        setExactAlarmCode(0);

        savedAlarm = getSavedAlarm();

        if(savedAlarm == -1) {
            setHourlyAlarm();
        }
        else {
            Calendar currTime = Calendar.getInstance();
            currentAlarmTime = Calendar.getInstance();
            currentAlarmTime.setTimeInMillis(savedAlarm);

            if (currentAlarmTime.compareTo(currTime) <= 0) {
                currentAlarmTime.add(Calendar.DATE, 1);
            }
            setExactAlarm(currentAlarmTime);
        }

        instance = this;
    }

    /**
     * This function sets an approximate alarm for once a hour.
     */
    public void setHourlyAlarm() {
        Intent intent = new Intent(this, HourlyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this,
                HOURLY_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60 * 60 * 1000,
                AlarmManager.INTERVAL_HOUR, alarmIntent);

        currentAlarmType = false;
    }

    /**
     * This function opens the time picker dialog for the user to choose when to set the exact alarm.
     */
    private void openTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Choose time");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        /**
         * This function reacts to a time picking in the time picker dialog - it sets an exact alarm
         * to the picked time.
         * @param view The time picker object
         * @param hourOfDay The selected hour of day
         * @param minute The selected minute of the hour
         */
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            // If the time is in the past or the present, sets it to the next day
            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
            setExactAlarm(calSet);
        }
    };

    /**
     * This function links the button for setting time with opening the time picker dialog, and
     * setting the alarm for a specific time.
     * @param view The button clicked in order to set the exact alarm.
     */
    public void chooseAlarmTime(View view) {
        openTimePickerDialog();
    }

    /**
     * This function sets an exact alarm to a given calendar time.
     * @param calSet The calendar object to set the alarm to its time
     */
    public void setExactAlarm(Calendar calSet) {
        int alarmCode = getExactAlarmCode();

        cancelAlarm(currentAlarmType);  // Cancels the existing alarm
        setSnoozeCounter(0);

        Intent intent = new Intent(this, ExactAlarmReceiver.class);

        alarmIntent = PendingIntent.getBroadcast(this,
                alarmCode, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calSet.getTimeInMillis(),
                30 * 1000, alarmIntent);  // Defines with snooze of 5 minutes

        tvAlarmTime.setText("Alarm is set to: " +  calSet.getTime());
        setExactAlarmCode(alarmCode + 1);
        currentAlarmTime = calSet;

        currentAlarmType = true;
        saveAlarmInFile(currentAlarmTime);
    }

    /**
     * This function cancels an existing alarm, according to its type.
     * @param alarmType true for exact alarm, false for hourly alarm.
     */
    public void cancelAlarm(boolean alarmType) {
        Intent intent;

        if(!alarmType) {
            intent = new Intent(this, HourlyAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(this,
                    HOURLY_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        else {
            intent = new Intent(this, ExactAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(this,
                    getExactAlarmCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        }

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
    }

    public void setCurrentAlarm(Calendar newAlarm) {
        currentAlarmTime = newAlarm;
    }

    public static int getSnoozeCounter() {
        return spAlarmFile.getInt("snoozeCounter", 0);
    }

    public static void setSnoozeCounter(int counter) {
        editor = spAlarmFile.edit();

        editor.putInt("snoozeCounter", counter);
        editor.commit();
    }

    public void saveAlarmInFile(Calendar alarm) {
        editor = spAlarmFile.edit();

        editor.putLong("exactAlarm", alarm.getTimeInMillis());
        editor.commit();
    }

    public long getSavedAlarm() {
        return spAlarmFile.getLong("exactAlarm", -1);
    }

    public void updateCurrentAlarmWithSnooze() {
        currentAlarmTime = Calendar.getInstance();
        currentAlarmTime.setTimeInMillis(getSavedAlarm());

        currentAlarmTime.add(Calendar.MINUTE, 5);
        saveAlarmInFile(currentAlarmTime);

        tvAlarmTime.setText("Alarm is set to: " +  currentAlarmTime.getTime());
    }

    public void setExactAlarmCode(int code) {
        editor = spAlarmFile.edit();

        editor.putInt("currentAlarmCode", code);
        editor.commit();
    }
    public int getExactAlarmCode() {
        return spAlarmFile.getInt("currentAlarmCode", 0);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * This function performs an organized exit from the app.
     * @param view The button that was clicked in order to exit.
     */
    public void exitApp(View view) {
        finish();
    }
}