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
