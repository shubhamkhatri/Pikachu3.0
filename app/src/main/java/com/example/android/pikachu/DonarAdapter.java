package com.example.android.pikachu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DonarAdapter extends ArrayAdapter<donarList> {

    public DonarAdapter(@NonNull Context context, ArrayList<donarList> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.donar_single_item, parent, false);
        }
        donarList currentList = getItem(position);
        TextView nameText = listItemView.findViewById(R.id.list_donar_name);
        TextView bloodGroupText = listItemView.findViewById(R.id.list_donar_blood);
        TextView cityText = listItemView.findViewById(R.id.list_donar_city);
        TextView dateText = listItemView.findViewById(R.id.list_donar_date);
        ImageView genderImage = listItemView.findViewById(R.id.list_donar_gender);

        nameText.setText(currentList.getName());
        bloodGroupText.setText(currentList.getBlood());
        cityText.setText(currentList.getCity());
        dateText.setText(currentList.getDate());

        if (currentList.getGender().compareTo("Female") == 0) {
            genderImage.setImageResource(R.drawable.female);
        }
        return listItemView;
    }
}
