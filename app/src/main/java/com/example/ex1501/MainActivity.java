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
    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;
    private final static int HOURLY_ALARM_REQUEST_CODE = -3;
    private static Calendar currentAlarmTime;
    private static SharedPreferences spAlarmFile;
    static SharedPreferences.Editor editor;
    private static MainActivity instance;
    private long savedAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

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
            setExactAlarm(currentAlarmTime, this);
        }
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
            setExactAlarm(calSet, instance);
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
    public static void setExactAlarm(Calendar calSet, Context context) {
        int alarmCode = getExactAlarmCode();
        setExactAlarmCode(alarmCode + 1);

        cancelAlarm(instance);  // Cancels the existing alarm
        setSnoozeCounter(0);

        Intent intent = new Intent(context, ExactAlarmReceiver.class);

        alarmIntent = PendingIntent.getBroadcast(context,
                alarmCode, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager)instance.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calSet.getTimeInMillis(),
                5 * 60 * 1000, alarmIntent);  // Defines with snooze of 5 minutes

        saveAlarmInFile(calSet);
    }

    /**
     * This function cancels the current alarm in a given context.
     * @param context The given context
     */
    public static void cancelAlarm(Context context) {
        Intent intent;
        int alarmCode = getActiveAlarmCode();

        if(alarmCode > 0) {
            intent = new Intent(context, HourlyAlarmReceiver.class);
        }
        else {
            intent = new Intent(context, ExactAlarmReceiver.class);
        }

        alarmIntent = PendingIntent.getBroadcast(context,
                alarmCode, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
    }

    /**
     * This function gets the snooze counter saved in the shared preferences file.
     * @return The snooze counter saved in the sp file
     */
    public static int getSnoozeCounter() {
        return spAlarmFile.getInt("snoozeCounter", 0);
    }

    /**
     * This function sets the snooze counter saved in the shared preferences file.
     * @param counter The value to set the counter
     */
    public static void setSnoozeCounter(int counter) {
        editor = spAlarmFile.edit();

        editor.putInt("snoozeCounter", counter);
        editor.commit();
    }

    /**
     * This function saves a given alarm in the shared preferences file.
     * @param alarm The alarm to save in the file(wil be saved as long type - millis)
     */
    public static void saveAlarmInFile(Calendar alarm) {
        editor = spAlarmFile.edit();

        editor.putLong("exactAlarm", alarm.getTimeInMillis());
        editor.commit();
    }

    /**
     * This function gets the alarm saved in the shared preferences file.
     * @return If exists, the alarm saved in the shared preferences file, otherwise -1
     */
    public static long getSavedAlarm() {
        return spAlarmFile.getLong("exactAlarm", -1);
    }

    /**
     * This function updates the current saved alarm with the snooze time.
     */
    public static void updateCurrentAlarmWithSnooze() {
        currentAlarmTime = Calendar.getInstance();
        currentAlarmTime.setTimeInMillis(getSavedAlarm());

        currentAlarmTime.add(Calendar.MINUTE, 5);
        saveAlarmInFile(currentAlarmTime);
    }

    /**
     * This function sets the exact alarm code saved in the shared preferences file.
     * @param code The value to set the current exact alarm code
     */
    public static void setExactAlarmCode(int code) {
        editor = spAlarmFile.edit();

        editor.putInt("currentAlarmCode", code);
        editor.commit();
    }

    /**
     * This function gets the exact alarm code saved in the shared preferences file.
     * @return The exact alarm cod saved in the sp file
     */
    public static int getExactAlarmCode() {
        return spAlarmFile.getInt("currentAlarmCode", 0);
    }

    /**
     * This function gets the instance of the main activity.
     * @return The instance of the main activity
     */
    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * This function gets the active alarm code.
     * @return The active alarm cod
     */
    private static int getActiveAlarmCode() {
        int exactCode = getExactAlarmCode();

        if(exactCode >= 0) {
            return exactCode;
        }

        return HOURLY_ALARM_REQUEST_CODE;
    }

    /**
     * This function inits the shared preferences file object.
     * @param context The context to initialize the shared preferences with.
     */
    public static void initSpObj(Context context) {
        spAlarmFile = (SharedPreferences) context.getSharedPreferences("SAVED_ALARMS", MODE_PRIVATE);
    }

    /**
     * This function performs an organized exit from the app.
     * @param view The button that was clicked in order to exit.
     */
    public void exitApp(View view) {
        finish();
    }
}