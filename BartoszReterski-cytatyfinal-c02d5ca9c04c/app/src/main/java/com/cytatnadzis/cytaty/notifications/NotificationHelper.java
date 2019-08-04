package com.cytatnadzis.cytaty.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.cytatnadzis.cytaty.MainActivity;
import com.cytatnadzis.cytaty.R;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {

        Intent resultIntent= new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent=  PendingIntent.getActivity(this,1, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext(), channelID)

//zdefiniowanie elementów powiadomienia

                .setContentTitle("Dostępny jest nowy cytat")
                .setContentText("Kliknij by zobaczyć więcej")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.iconnotificationlarge))
                .setSmallIcon(R.drawable.notificationsmall)// ZMIEN IKONE NA IKONE PROGRAMU CYTAT NA DZIS
                .setColor(getResources().getColor(R.color.transparent))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }
}