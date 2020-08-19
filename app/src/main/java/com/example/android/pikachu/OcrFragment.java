package com.example.android.pikachu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class OcrFragment extends Fragment {

    public List<String> listt = new ArrayList<>();
    private String check;
    private String categorySet;
    private String getCategorySet;
    final ArrayList<word> fileList = new ArrayList<word>();
    private FloatingActionButton add;
    private FloatingActionButton graph;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ocr, container, false);
        File root = new File(Environment.getExternalStorageDirectory() + "/" + "PikachuDocument");

        ListDir(root, v);

        graph = v.findViewById(R.id.graph_view);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), graphActivity.class);
                startActivity(i);
            }
        });

        add = v.findViewById(R.id.add_ocr);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), OcrCreateActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    String getCheck(File ff) {
        check = ff.getName();
        if (check.contains("Liver Test")) {
            categorySet = "Liver Test";
        } else if (check.contains("Kidney Test")) {
            categorySet = "Kidney Test";
        } else if (check.contains("Sugar Test")) {
            categorySet = "Sugar Test";
        } else if (check.contains("Thyroid Test")) {
            categorySet = "Thyroid Test";
        } else if (check.contains("Jaundice Test")) {
            categorySet = "Jaundice Test";
        } else if (check.contains("HIV Test")) {
            categorySet = "HIV Test";
        } else {
            categorySet = "Common Test";
        }
        return categorySet;
    }

    void ListDir(File f, View v) {
        File[] files = f.listFiles();
        fileList.clear();
        if (files != null) {
            for (File file : files) {
                listt.add(file.getPath());
                getCategorySet = getCheck(file);
                fileList.add(new word(getCategorySet, file.getName()));
            }

            final DocumentAdaptor directoryList = new DocumentAdaptor(getActivity(), fileList, R.color.colorAccent);

            ListView listView = (ListView) v.findViewById(R.id.list);
            listView.setAdapter(directoryList);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                                                    Intent appInfo = new Intent(getActivity(), pdfViewer.class);
                                                    appInfo.putExtra("name_path", listt.get(position));
                                                    startActivity(appInfo);
                                                }
                                            }

            );
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

                    builder.setMessage("Do you really want to delete this Job?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    File file = new File(listt.get(position));
                                    boolean deleted = file.delete();
                                    if (deleted) {
                                        directoryList.remove(directoryList.getItem(position));
                                        directoryList.notifyDataSetChanged();
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });
        }

    }


}
