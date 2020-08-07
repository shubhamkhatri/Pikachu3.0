package com.example.android.pikachu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MedicineAdapter extends ArrayAdapter<AlarmList> {
    String Day = "";
    private ImageView delete;
    private LinearLayout item;
    DatabaseHelper mDatabaseHelper;
    private Switch state;
    private boolean dayss[] = new boolean[7];


    public MedicineAdapter(@NonNull Context context, ArrayList<AlarmList> currentList) {
        super(context, 0, currentList);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.medicine_single_item, parent, false);
        }
        createNotificationChannel();
        final AlarmList currentList = getItem(position);
        TextView hour = listItemView.findViewById(R.id.medicine_hour);
        TextView minute = listItemView.findViewById(R.id.medicine_minute);
        final TextView days = listItemView.findViewById(R.id.medicine_days);
        TextView name = listItemView.findViewById(R.id.medicine_name);
        TextView quantity = listItemView.findViewById(R.id.medicine_quantity);
        TextView quality = listItemView.findViewById(R.id.medicine_quality);
        delete = listItemView.findViewById(R.id.delete_alarm);
        item = listItemView.findViewById(R.id.medicine_item);
        state = listItemView.findViewById(R.id.alarm_state);

        if (currentList.getState() == 0)
            state.setChecked(false);
        else
            state.setChecked(true);

        hour.setText(String.valueOf(currentList.getHour()));
        minute.setText(String.valueOf(currentList.getMinute()));
        setDayArray(currentList);
        Day = "";
        if (currentList.getEveryday() == 1) {
            Day = "Everyday";
        } else {
            if (currentList.getSunday() == 1)
                Day = Day.concat("Sun ");
            if (currentList.getMonday() == 1)
                Day = Day.concat("Mon ");
            if (currentList.getTuesday() == 1)
                Day = Day.concat("Tue ");
            if (currentList.getWednesday() == 1)
                Day = Day.concat("Wed ");
            if (currentList.getThursday() == 1)
                Day = Day.concat("Thu ");
            if (currentList.getFriday() == 1)
                Day = Day.concat("Fri ");
            if (currentList.getSaturday() == 1)
                Day = Day.concat("Sat ");
        }
        days.setText(Day);
        name.setText(currentList.getMedicineName());
        quantity.setText(currentList.getQuantity());
        quality.setText(currentList.getQuality());

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = currentList.getMedicineName();
                int hour = currentList.getHour();
                int minute = currentList.getMinute();
                String quantity = currentList.getQuantity();
                String quality = currentList.getQuality();
                int everyday = currentList.getEveryday();
                int sunday = currentList.getSunday();
                int monday = currentList.getMonday();
                int tuesday = currentList.getTuesday();
                int wednesday = currentList.getWednesday();
                int thursday = currentList.getThursday();
                int friday = currentList.getFriday();
                int saturday = currentList.getSaturday();
                Log.d("TIMER FRAG", "onItemClick: You Clicked on " + name);

                Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if (itemID > -1) {
                    Log.d("TIMER FRAG", "onItemClick: The ID is: " + itemID);
                    Intent editScreenIntent = new Intent(getContext(), EditMedicine.class);
                    editScreenIntent.putExtra("id", itemID);
                    editScreenIntent.putExtra("name", name);
                    editScreenIntent.putExtra("hour", hour);
                    editScreenIntent.putExtra("minute", minute);
                    editScreenIntent.putExtra("quantity", quantity);
                    editScreenIntent.putExtra("quality", quality);
                    editScreenIntent.putExtra("everyday", everyday);
                    editScreenIntent.putExtra("sunday", sunday);
                    editScreenIntent.putExtra("monday", monday);
                    editScreenIntent.putExtra("tuesday", tuesday);
                    editScreenIntent.putExtra("wednesday", wednesday);
                    editScreenIntent.putExtra("thursday", thursday);
                    editScreenIntent.putExtra("friday", friday);
                    editScreenIntent.putExtra("saturday", saturday);
                    ((Activity) getContext()).finish();
                    getContext().startActivity(editScreenIntent);
                } else {
                    Toast.makeText(getContext(), "No ID associated with that name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDatabaseHelper = new

                DatabaseHelper(getContext());
        Cursor data = mDatabaseHelper.getItemID(currentList.getMedicineName());
        int itemID = -1;
        while (data.moveToNext()) {
            itemID = data.getInt(0);
        }

        final int finalItemID = itemID;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("Do you really want to delete this Reminder?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                for (int ii = 0; ii < 7; ii++) {
                                    if (dayss[ii])
                                        cancelAlarm(currentList, finalItemID, ii + 1);
                                }
                                mDatabaseHelper.deleteName(finalItemID, currentList.getMedicineName());
                                ((Activity) getContext()).finish();
                                Intent in = new Intent(getContext(), TabLayoutActivity.class);
                                getContext().startActivity(in);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    for (int ii = 0; ii < 7; ii++) {
                        if (dayss[ii])
                            setAlarm(currentList, finalItemID, ii + 1);
                    }
                    mDatabaseHelper.updateStateValue(finalItemID, currentList.getMedicineName(), 1);
                }
                if (!isChecked) {
                    for (int ii = 0; ii < 7; ii++) {
                        if (dayss[ii])
                            cancelAlarm(currentList, finalItemID, ii + 1);
                    }
                    mDatabaseHelper.updateStateValue(finalItemID, currentList.getMedicineName(), 0);
                }
            }
        });

        return listItemView;
    }

    private void setDayArray(AlarmList currentList) {
        if (currentList.getEveryday() == 1) {
            for (int i = 0; i < 7; i++)
                dayss[i] = true;
        } else {
            if (currentList.getSunday() == 1)
                dayss[0] = true;
            if (currentList.getMonday() == 1)
                dayss[1] = true;
            if (currentList.getTuesday() == 1)
                dayss[2] = true;
            if (currentList.getWednesday() == 1)
                dayss[3] = true;
            if (currentList.getThursday() == 1)
                dayss[4] = true;
            if (currentList.getFriday() == 1)
                dayss[5] = true;
            if (currentList.getSaturday() == 1)
                dayss[6] = true;
        }
    }

    public void setAlarm(AlarmList values, int itemID, int y) {
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("name", values.getMedicineName());
        intent.putExtra("quantity", values.getQuantity());
        intent.putExtra("quality", values.getQuality());
        intent.putExtra("id", itemID);
        // intent.putExtra("days", days);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), ((itemID * 1000) + y), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int x = y - day;
        if (x < 0)
            x = x + 7;
        int finaldate = date + x;

        calendar.set(Calendar.HOUR_OF_DAY, values.getHour());
        calendar.set(Calendar.MINUTE, values.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, finaldate);

        long alarm_time = calendar.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);

    }

    public void cancelAlarm(AlarmList values, int itemID, int y) {

        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("name", values.getMedicineName());
        intent.putExtra("quantity", values.getQuantity());
        intent.putExtra("quality", values.getQuality());
        intent.putExtra("id", itemID);
        // intent.putExtra("days", days);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), ((itemID * 1000) + y), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MedicineReminderChannel";
            String description = "Channel for medicine Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
