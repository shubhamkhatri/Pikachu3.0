package com.example.android.pikachu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;


public class BmiFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText feet, inch, cm, weight, age;
    private TextView ans, severelyUnderweight, underweight, normal, overweight, obese, bmi, difference;
    private String Height = "", bmiText;
    double Ht, Weight, bmiValue;
    private Spinner spin1;
    private String[] height = {"cm", "feet/inch"};
    private LinearLayout feetLayout;
    private TextInputLayout cmLayout;
    private Button calculate;
    private CardView range;
    private int wt1, wt2, wt3, wt4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bmi, container, false);
        setDefault(v);
        feetLayout.setVisibility(View.GONE);
        cmLayout.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, height);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapter1);
        spin1.setOnItemSelectedListener(this);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Height.equals("cm") && cm.getText().toString().trim().isEmpty()) {
                    cm.setError(getString(R.string.err_msg));
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (Height.equals("feet/inch") && feet.getText().toString().trim().isEmpty()) {
                    feet.setError(getString(R.string.err_msg));
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (Height.equals("feet/inch") && inch.getText().toString().trim().isEmpty()) {
                    inch.setError(getString(R.string.err_msg));
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (weight.getText().toString().trim().isEmpty()) {
                    weight.setError(getString(R.string.err_msg));
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (age.getText().toString().trim().isEmpty()) {
                    age.setError(getString(R.string.err_msg));
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(age.getText().toString().trim()) < 18) {
                    Toast.makeText(getActivity(), "Age should be more than 18 years", Toast.LENGTH_SHORT).show();
                } else
                    calculateBmi();

            }
        });
        return v;
    }

    private void setDefault(View view) {
        feet = (EditText) view.findViewById(R.id.bmi_feet);
        inch = (EditText) view.findViewById(R.id.bmi_inch);
        cm = (EditText) view.findViewById(R.id.bmi_cm);
        weight = (EditText) view.findViewById(R.id.bmi_weight);
        age = (EditText) view.findViewById(R.id.bmi_age);
        spin1 = (Spinner) view.findViewById(R.id.bmi_height_spinner);
        feetLayout = (LinearLayout) view.findViewById(R.id.bmi_feet_scale);
        cmLayout = (TextInputLayout) view.findViewById(R.id.bmi_cm_scale);
        calculate = (Button) view.findViewById(R.id.bmi_calculate);
        ans = (TextView) view.findViewById(R.id.bmi_ans_text);
        range = (CardView) view.findViewById(R.id.bmi_range_cardView);
        severelyUnderweight = (TextView) view.findViewById(R.id.bmi_range_SU);
        underweight = (TextView) view.findViewById(R.id.bmi_range_U);
        normal = (TextView) view.findViewById(R.id.bmi_range_N);
        overweight = (TextView) view.findViewById(R.id.bmi_range_OW);
        obese = (TextView) view.findViewById(R.id.bmi_range_O);
        bmi = (TextView) view.findViewById(R.id.bmi_value);
        difference=(TextView)view.findViewById(R.id.bmi_diff_text);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.bmi_height_spinner:
                Height = height[position];
                if (Height.equals("cm")) {
                    feetLayout.setVisibility(View.GONE);
                    cmLayout.setVisibility(View.VISIBLE);
                } else {
                    feetLayout.setVisibility(View.VISIBLE);
                    cmLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), "Select Height Unit", Toast.LENGTH_SHORT).show();
    }

    public void calculateBmi() {
        if (Height.equals("feet/inch")) {
            double Feet = Double.parseDouble(feet.getText().toString().trim());
            double Inch = Double.parseDouble(inch.getText().toString().trim());
            Ht = (Feet * 30.48) + (Inch * 2.54);
        } else {
            Ht = Double.parseDouble(cm.getText().toString().trim());
        }
        Ht = Ht / 100;
        Weight = Double.parseDouble(weight.getText().toString().trim());
        bmiValue = Weight / (Ht * Ht);
        double roundOff = Math.round(bmiValue * 100);
        setRange(Ht, Weight);
        bmi.setText(String.valueOf(roundOff / 100));
        if (bmiValue < 16) {
            bmiText = "Severely underweight";
            ans.setText(bmiText);
            ans.setTextColor(ContextCompat.getColor(getContext(), R.color.severelyUnderweight));
            bmi.setTextColor(ContextCompat.getColor(getContext(), R.color.severelyUnderweight));
            difference.setTextColor(ContextCompat.getColor(getContext(), R.color.severelyUnderweight));
        } else if (bmiValue < 18.5) {
            bmiText = "Underweight";
            ans.setText(bmiText);
            ans.setTextColor(ContextCompat.getColor(getContext(), R.color.underweight));
            bmi.setTextColor(ContextCompat.getColor(getContext(), R.color.underweight));
            difference.setTextColor(ContextCompat.getColor(getContext(), R.color.underweight));
        } else if (bmiValue < 25) {
            bmiText = "Normal";
            ans.setText(bmiText);
            ans.setTextColor(ContextCompat.getColor(getContext(), R.color.normal));
            bmi.setTextColor(ContextCompat.getColor(getContext(), R.color.normal));
            difference.setTextColor(ContextCompat.getColor(getContext(), R.color.normal));
        } else if (bmiValue < 30) {
            bmiText = "Overweight";
            ans.setText(bmiText);
            ans.setTextColor(ContextCompat.getColor(getContext(), R.color.overweight));
            bmi.setTextColor(ContextCompat.getColor(getContext(), R.color.overweight));
            difference.setTextColor(ContextCompat.getColor(getContext(), R.color.overweight));
        } else {
            bmiText = "Obese";
            ans.setText(bmiText);
            ans.setTextColor(ContextCompat.getColor(getContext(), R.color.obese));
            bmi.setTextColor(ContextCompat.getColor(getContext(), R.color.obese));
            difference.setTextColor(ContextCompat.getColor(getContext(), R.color.obese));
        }
    }

    private void setRange(double heightt, double value) {
        double diff=0.0;
        range.setVisibility(View.VISIBLE);
        wt1 = (int) Math.ceil(15.9 * heightt * heightt);
        wt2 = (int) Math.ceil(18.4 * heightt * heightt);
        wt3 = (int) Math.ceil(24.9 * heightt * heightt);
        wt4 = (int) Math.ceil(29.9 * heightt * heightt);

        severelyUnderweight.setText("SeverlyUnderweight: < " + (wt1 - 1));
        underweight.setText("Underweight: " + wt1 + " - " + (wt2 - 1));
        normal.setText("Normal: " + wt2 + " - " + (wt3 - 1));
        overweight.setText("Overweight: " + wt3 + " - " + (wt4 - 1));
        obese.setText("Obese: > " + wt4);

        if (value < wt2)
            diff = value-wt2;
        else if (value >= wt3)
            diff = value-(wt3 - 1);
        else
            diff = 0.0;
        difference.setText(String.valueOf(diff));
    }
}
