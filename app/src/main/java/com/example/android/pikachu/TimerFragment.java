package com.example.android.pikachu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class TimerFragment extends Fragment {

    private FloatingActionButton add;
    private DatabaseHelper mDatabaseHelper;
    private String Name, Quality, Quantity;
    private int Hour, Minute;
    private int Everyday = 0, Sunday = 0, Monday = 0, Tuesday = 0, Wednesday = 0, Thursday = 0, Friday = 0, Saturday = 0, State = 0;
    private ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        mDatabaseHelper = new DatabaseHelper(getActivity());
        listView = v.findViewById(R.id.list_medicine);
        populateListView();

        add = v.findViewById(R.id.add_medicine);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddMedicine.class);
                startActivity(i);
            }
        });
        return v;
    }

    private void populateListView() {

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();
        final ArrayList<AlarmList> listData = new ArrayList<AlarmList>();
        while (data.moveToNext()) {
            //get the value from the database in column 1
            //then add it to the ArrayList
            Name = data.getString(1);
            Hour = data.getInt(2);
            Minute = data.getInt(3);
            Quantity = data.getString(4);
            Quality = data.getString(5);
            Everyday = data.getInt(6);
            Sunday = data.getInt(7);
            Monday = data.getInt(8);
            Tuesday = data.getInt(9);
            Wednesday = data.getInt(10);
            Thursday = data.getInt(11);
            Friday = data.getInt(12);
            Saturday = data.getInt(13);
            State = data.getInt(14);

            listData.add(new AlarmList(Name, Hour, Minute, Quantity, Quality, Everyday, Sunday,
                    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, State));
        }
        //create the list adapter and set the adapter
        final MedicineAdapter medicineAdapter = new MedicineAdapter(getActivity(), listData);
        listView.setAdapter(medicineAdapter);

    }

}
