package com.example.android.pikachu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import java.io.File;

public class firstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        String folder_main = "PikachuDocument";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

    }
    public void openMainActivity(View view)
    {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void openDocumentActivity(View view)
    {
        Intent i=new Intent(this,DocumentActivity.class);
        startActivity(i);
    }

    public void openBarGraph(View view){


        Intent i=new Intent(this,graphActivity.class);
        startActivity(i);

    }
}
