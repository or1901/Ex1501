package com.example.ex1501;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent hourlyAlarmIntent;
    private int HOURLY_ALARM_REQUEST_CODE = 1;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
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

            }

        }
    };

    public void chooseAlarmTime(View view) {
        openTimePickerDialog(true);
    }

    /**
     * This function performs an organized exit from the app.
     * @param view The button that was clicked in order to exit.
     */
    public void exitApp(View view) {
        finish();
    }
}