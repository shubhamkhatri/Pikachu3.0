package com.example.android.pikachu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class DonarFragment extends Fragment {

    private String name, city, blood, gender, date, email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<donarList> donar = new ArrayList<donarList>();
    private String UserEmail;
    private FirebaseAuth firebaseAuth;
    private LoginPreferences loginPreferences;
    private SkipPreferences skipPreferences;
    private ListView listView;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_donation, container, false);
        donar.clear();
        loginPreferences = new LoginPreferences(getActivity());
        skipPreferences = new SkipPreferences(getActivity());
        listView = v.findViewById(R.id.list_donar);
        getData();
        firebaseAuth = FirebaseAuth.getInstance();
        if (loginPreferences.DonarLaunch() == 1) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            UserEmail = currentUser.getEmail();
        }
        setDialog();
        return v;
    }

    public void setDialog() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
    }

    private void getData() {
        db.collection("donars").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                name = document.getString("Name");
                                blood = document.getString("Blood Group");
                                city = document.getString("City");
                                date = document.getString("Date");
                                gender = document.getString("Gender");
                                email = document.getString("Email");
                                if (loginPreferences.DonarLaunch() == 1) {
                                    if (email.compareTo(UserEmail) == 0)
                                        continue;
                                }
                                donar.add(new donarList(name, blood, city, date, gender, email));
                            }
                            loadingDialog.dismissDialog();
                            DonarAdapter donarAdapter = new DonarAdapter(getActivity(), donar);

                            LayoutInflater inflater = getLayoutInflater();
                            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.donar_header, listView, false);
                            CardView cardView = (CardView) header.findViewById(R.id.donar_myProfile);
                            listView.addHeaderView(header, null, false);
                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (loginPreferences.DonarLaunch() == 1) {
                                        Intent i = new Intent(getActivity(), DonarProfileActivity.class);
                                        i.putExtra("Email", UserEmail);
                                        i.putExtra("Path", "My Profile");
                                        startActivity(i);
                                    } else {
                                        skipPreferences.setLaunch(0);
                                        Intent i = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                }
                            });

                            listView.setAdapter(donarAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    donarList d = donar.get(position - 1);
                                    Intent intent = new Intent(getActivity(), DonarProfileActivity.class);
                                    intent.putExtra("Email", d.getEmail());
                                    intent.putExtra("Path", "list");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            loadingDialog.dismissDialog();
                            Toast.makeText(getActivity(), "Error#121", Toast.LENGTH_SHORT).show();
                            Log.d("DATA FETCH ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
