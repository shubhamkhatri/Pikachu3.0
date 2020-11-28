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

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddMedicine extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    private CheckBox everyday;
    private FloatingActionButton save;
    private EditText name, quantity;
    private TextView time;
    private int hour, minute;
    private Spinner spin1;
    private String[] quality = {"Choose One", "adhesive(s)", "capsule(s)", "cachet(s)", "cream(s)", "dragee(s)", "emulsion(s)", "pack(s)", "gel(s)", "drop(s)", "inhalation(s)", "shot(s)",
            "lotion(s)", "measure(s)", "tablet(s)", "powder(s)", "ointment(s)", "soap(s)", "sachet(s)", "unit(s)", "solution(s)", "spray(s)", "suspension(s)", "syrup(s)",};
    private String qualityy = "";
    DatabaseHelper mDatabaseHelper;
    private String Name, Quality, Quantity;
    private int Hour, Minute;
    private int Everyday = 0, Sunday = 0, Monday = 0, Tuesday = 0, Wednesday = 0, Thursday = 0, Friday = 0, Saturday = 0;
    private AlarmList alarmList;
    private boolean days[] = new boolean[7];


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        setId();
        setCurrentTime();
        mDatabaseHelper = new DatabaseHelper(this);

        everyday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAll();
            }
        });
        setTextColor();

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        createNotificationChannel();

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quality);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapter1);
        spin1.setOnItemSelectedListener(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDay();
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.err_msg));
                    Toast.makeText(AddMedicine.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (Sunday == 0 && Monday == 0 && Tuesday == 0 && Wednesday == 0 && Thursday == 0 && Friday == 0 && Saturday == 0) {
                    Toast.makeText(AddMedicine.this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                } else if (qualityy.isEmpty() || qualityy.compareTo("Choose One") == 0) {
                    Toast.makeText(AddMedicine.this, "Please select unit", Toast.LENGTH_SHORT).show();
                } else {
                    name.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    quantity.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    fillDays();
                    updateData();

                }
            }
        });

    }

    private void fillDays() {
        if (Everyday == 1) {
            for (int i = 0; i < 7; i++)
                days[i] = true;
        } else {
            if (Sunday == 1)
                days[0] = true;
            if (Monday == 1)
                days[1] = true;
            if (Tuesday == 1)
                days[2] = true;
            if (Wednesday == 1)
                days[3] = true;
            if (Thursday == 1)
                days[4] = true;
            if (Friday == 1)
                days[5] = true;
            if (Saturday == 1)
                days[6] = true;
        }
    }

    private void updateData() {
        Name = name.getText().toString().trim();
        Quantity = quantity.getText().toString().trim();
        Quality = qualityy;
        Hour = hour;
        Minute = minute;

        alarmList = new AlarmList(Name, Hour, Minute, Quantity, Quality, Everyday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, 1);
        AddData(alarmList);
    }

    private void setTextColor() {
        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sunday.isChecked())
                    sunday.setTextColor(Color.parseColor("#FFFFFF"));
                else {
                    everyday.setChecked(false);
                    sunday.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monday.isChecked())
                    monday.setTextColor(Color.parseColor("#FFFFFF"));
                else {
                    everyday.setChecked(false);
                    monday.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tuesday.isChecked())
                    tuesday.setTextColor(Color.parseColor("#FFFFFF"));
                else {
                    everyday.setChecked(false);
                    tuesday.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wednesday.isChecked())
                    wednesday.setTextColor(Color.parseColor("#FFFFFF"));
                else {
                    everyday.setChecked(false);
                    wednesday.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thursday.isChecked())
                    thursday.setTextColor(Color.parseColor("#FFFFFF"));
                else
                    thursday.setTextColor(Color.parseColor("#000000"));
            }
        });
        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friday.isChecked())
                    friday.setTextColor(Color.parseColor("#FFFFFF"));
                else {
                    everyday.setChecked(false);
                    friday.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saturday.isChecked())
                    saturday.setTextColor(Color.parseColor("#FFFFFF"));
                else {
                    everyday.setChecked(false);
                    saturday.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
    }

    public void checkAll() {
        if (everyday.isChecked()) {
            sunday.setChecked(true);
            sunday.setTextColor(Color.parseColor("#FFFFFF"));
            monday.setChecked(true);
            monday.setTextColor(Color.parseColor("#FFFFFF"));
            tuesday.setChecked(true);
            tuesday.setTextColor(Color.parseColor("#FFFFFF"));
            wednesday.setChecked(true);
            wednesday.setTextColor(Color.parseColor("#FFFFFF"));
            thursday.setChecked(true);
            thursday.setTextColor(Color.parseColor("#FFFFFF"));
            friday.setChecked(true);
            friday.setTextColor(Color.parseColor("#FFFFFF"));
            saturday.setChecked(true);
            saturday.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (!everyday.isChecked()) {
            sunday.setChecked(false);
            sunday.setTextColor(Color.parseColor("#000000"));
            monday.setChecked(false);
            monday.setTextColor(Color.parseColor("#000000"));
            tuesday.setChecked(false);
            tuesday.setTextColor(Color.parseColor("#000000"));
            wednesday.setChecked(false);
            wednesday.setTextColor(Color.parseColor("#000000"));
            thursday.setChecked(false);
            thursday.setTextColor(Color.parseColor("#000000"));
            friday.setChecked(false);
            friday.setTextColor(Color.parseColor("#000000"));
            saturday.setChecked(false);
            saturday.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void checkDay() {
        if (sunday.isChecked())
            Sunday = 1;
        if (monday.isChecked())
            Monday = 1;
        if (tuesday.isChecked())
            Tuesday = 1;
        if (wednesday.isChecked())
            Wednesday = 1;
        if (thursday.isChecked())
            Thursday = 1;
        if (friday.isChecked())
            Friday = 1;
        if (saturday.isChecked())
            Saturday = 1;
        if (everyday.isChecked()) {
            Everyday = 1;
            Sunday = 1;
            Monday = 1;
            Tuesday = 1;
            Wednesday = 1;
            Thursday = 1;
            Friday = 1;
            Saturday = 1;
        }

    }

    private void setId() {

        sunday = (CheckBox) findViewById(R.id.med_sunday);
        monday = (CheckBox) findViewById(R.id.med_monday);
        tuesday = (CheckBox) findViewById(R.id.med_tuesday);
        wednesday = (CheckBox) findViewById(R.id.med_wednesday);
        thursday = (CheckBox) findViewById(R.id.med_thursday);
        friday = (CheckBox) findViewById(R.id.med_friday);
        saturday = (CheckBox) findViewById(R.id.med_saturday);
        everyday = (CheckBox) findViewById(R.id.med_every_day);

        name = (EditText) findViewById(R.id.med_name);
        quantity = (EditText) findViewById(R.id.med_dose_quantity);

        time = (TextView) findViewById(R.id.med_time);
        spin1 = (Spinner) findViewById(R.id.med_spinner_dose_units);
        save = (FloatingActionButton) findViewById(R.id.save_medicine);
    }

    private void setCurrentTime() {
        Calendar mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mCurrentTime.get(Calendar.MINUTE);

        time.setText(String.format(Locale.getDefault(), "%d:%d", hour, minute));
    }

    private void showTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddMedicine.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                time.setText(String.format(Locale.getDefault(), "%d:%d", selectedHour, selectedMinute));
            }
        }, hour, minute, false);//No 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.med_spinner_dose_units:
                qualityy = quality[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(AddMedicine.this, "Please select medicine Unit", Toast.LENGTH_SHORT).show();
    }

    public void AddData(AlarmList newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if (insertData) {
            Toast.makeText(AddMedicine.this, "Data Successfully Inserted!", Toast.LENGTH_SHORT).show();
            setNotification(newEntry);
        } else {
            Toast.makeText(AddMedicine.this, "Error#999", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNotification(AlarmList values) {
        Cursor data = mDatabaseHelper.getItemID(values.getMedicineName());
        int itemID = -1;
        while (data.moveToNext()) {
            itemID = data.getInt(0);
        }
        if (itemID > -1) {

            for (int i = 0; i < 7; i++) {
                if (days[i]) {
                    int y = i + 1;

                    Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                    intent.putExtra("name", values.getMedicineName());
                    intent.putExtra("quantity", values.getQuantity());
                    intent.putExtra("quality", values.getQuality());
                    intent.putExtra("id", itemID);
                    // intent.putExtra("days", days);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ((itemID * 1000) + y), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Calendar calendar = Calendar.getInstance();
                    int date = calendar.get(Calendar.DAY_OF_MONTH);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    int x = y - day;
                    if (x < 0)
                        x = x + 7;
                    int finaldate = date + x;

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, finaldate);

                    long alarm_time = calendar.getTimeInMillis();

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                            AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                }
            }
            finish();
            Intent activity = new Intent(AddMedicine.this, TabLayoutActivity.class);
            activity.putExtra("fragment id", 2);
            startActivity(activity);
        }

    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MedicineReminderChannel";
            String description = "Channel for medicine Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
