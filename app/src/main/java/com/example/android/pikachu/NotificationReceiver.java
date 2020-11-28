/*
 *  Copyright (c) 2020 Pikachu(shubham khatri). All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent repeatingIntent = new Intent(context, NotificationView.class);
        repeatingIntent.putExtra("Name", name);
        repeatingIntent.putExtra("Quality", quality);
        repeatingIntent.putExtra("Quantity", quantity);
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
