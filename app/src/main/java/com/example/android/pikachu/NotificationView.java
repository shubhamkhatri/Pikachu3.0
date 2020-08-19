package com.example.android.pikachu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class NotificationView extends AppCompatActivity {

    private TextView name, quality, quantity;
    private Button dismiss;
    private ImageView clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        name = findViewById(R.id.nt_medName);
        quality = findViewById(R.id.nt_medQuality);
        quantity = findViewById(R.id.nt_medQuantity);
        dismiss = findViewById(R.id.nt_dismissBtn);
        clock = findViewById(R.id.nt_alarmClock);

        Intent i = getIntent();
        String Name = i.getStringExtra("Name");
        String Quality = i.getStringExtra("Quality");
        String Quantity = i.getStringExtra("Quantity");

        YoYo.with(Techniques.Wobble)
                .duration(700)
                .repeat(30)
                .playOn(clock);

        name.setText(Name);
        quality.setText(Quality);
        quantity.setText(Quantity);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
