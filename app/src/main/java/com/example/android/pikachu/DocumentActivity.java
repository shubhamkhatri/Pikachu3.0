package com.example.android.pikachu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Adapter;


import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import java.util.ArrayList;
import java.io.File;
import java.util.List;


public class DocumentActivity extends AppCompatActivity {
     public List<String> listt = new ArrayList<>();
     String check;
String categorySet;
String getCategorySet;
    final ArrayList<word> fileList=new ArrayList<word>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        //File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        File root = new File(Environment.getExternalStorageDirectory() + "/" + "PikachuDocument");

        ListDir(root);

    }

String getCheck(File ff){
        check=ff.getName();
        if(check.contains("Liver Test")){categorySet="Liver Test"; }
        else if(check.contains("Kidney Test")){categorySet="Kidney Test"; }
        else if(check.contains("Sugar Test")){categorySet="Sugar Test"; }
        else if(check.contains("Thyroid Test")){categorySet="Thyroid Test"; }
        else if(check.contains("Jaundice Test")){categorySet="Jaundice Test"; }
        else if(check.contains("HIV Test")){categorySet="HIV Test"; }
        else {categorySet="Common Test"; }
return categorySet;
    }

    void ListDir(File f) {
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files) {
           listt.add(file.getPath());
           getCategorySet=getCheck(file);
            fileList.add(new word(getCategorySet,file.getName()));
        }

 DocumentAdaptor directoryList = new DocumentAdaptor (this, fileList,R.color.colorAccent);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(directoryList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {



                Intent appInfo = new Intent(DocumentActivity.this, pdfViewer.class);
                appInfo.putExtra("name_path",listt.get(position));
                startActivity(appInfo);
            }
        });



    }



}