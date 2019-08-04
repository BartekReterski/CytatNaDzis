
package com.cytatnadzis.cytaty.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.widget.Toolbar;
import com.cytatnadzis.cytaty.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DateFormat;
import java.util.Calendar;

public class Notifications extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private TextView mTextView;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        toolbar=  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.bialy));
        toolbar.setTitle("Powiadomienia");
        setSupportActionBar(toolbar);

        mTextView = findViewById(R.id.textView);
        sharedPreferences=getSharedPreferences("notificat",Context.MODE_PRIVATE);
        String time= sharedPreferences.getString("time","");
        mTextView.setText(time);

        MobileAds.initialize(this,"ca-app-pub-7746007065654957~3833214947");

        mAdView=findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        ButtonsLogic();

    }


    public void ButtonsLogic(){
        ImageView buttonTimePicker = findViewById(R.id.buttonTimePicker);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        ImageView buttonCancelAlarm = findViewById(R.id.buttonCancel);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelAlarm();
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.remove("time").apply();

            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Ustawiono codzienne przypomnienie o nowym cytacie na godzinę:\n ";

        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        mTextView.setText(timeText);
        sharedPreferences= getSharedPreferences("notificat",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("time",mTextView.getText().toString());
        editor.apply();
        editor.remove("time");

    }

    public void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

              alarmManager. setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
              alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        }



    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);

        mTextView.setText("Usunięto przypomnienie");
    }
}