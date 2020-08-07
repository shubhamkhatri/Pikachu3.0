package com.example.android.pikachu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity {

    private Button send;
    private EditText feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedback = (EditText) findViewById(R.id.feedback);
        send = (Button)findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback_msg = feedback.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_TEXT, feedback_msg);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"shubhamkhatri234@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Report" );
                if (intent.resolveActivity(FeedbackActivity.this.getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
