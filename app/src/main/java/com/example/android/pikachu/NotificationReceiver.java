package com.example.android.pikachu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getStringExtra("name");
        String quantity = intent.getStringExtra("quantity");
        String quality = intent.getStringExtra("quality");
        int id = intent.getIntExtra("id", 0);
      //  boolean days[] = intent.getBooleanArrayExtra("days");

        String Heading = "Medicine Reminder";
        String text = "Reminder for " + name + " " + quantity + " " + quality;
        //for (int i = 0; i < 7; i++) {
        //  if (days[i]) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent repeatingIntent = new Intent(context, NotificationView.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.clock)
                .setContentTitle(Heading)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(id, builder.build());
        //     }
        // }
    }
}
