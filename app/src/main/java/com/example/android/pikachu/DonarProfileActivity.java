package com.example.android.pikachu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class DonarProfileActivity extends AppCompatActivity {

    private TextView name, date, age, gender, phnNo, address, city, location, blood, email;
    private String Name, Date, Age, Gender, PhnNo, Address, City, Location, Blood, Email, Latitude, Longitude;
    private Button button, mapView;
    private String Emaill, path;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private double latitude, longitude;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar_profile);
        setId();
        setDialog();
        Intent i = getIntent();
        Emaill = i.getStringExtra("Email");
        path = i.getStringExtra("Path");
        setData(Emaill);
    }

    private void setData(final String emaill) {
        db.collection("donars").document(emaill).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Name = documentSnapshot.getString("Name");
                            Date = documentSnapshot.getString("Date");
                            Age = documentSnapshot.getString("Age");
                            Gender = documentSnapshot.getString("Gender");
                            PhnNo = documentSnapshot.getString("Phone");
                            Address = documentSnapshot.getString("Address");
                            City = documentSnapshot.getString("City");
                            Location = documentSnapshot.getString("Location");
                            Blood = documentSnapshot.getString("Blood Group");
                            Email = documentSnapshot.getString("Email");
                            Latitude = documentSnapshot.getString("Latitude");
                            Longitude = documentSnapshot.getString("Longitude");

                            name.setText(Name);
                            date.setText(Date);
                            age.setText(Age);
                            gender.setText(Gender);
                            phnNo.setText(PhnNo);
                            address.setText(Address);
                            city.setText(City);
                            location.setText(Location);
                            blood.setText(Blood);
                            email.setText(Email);

                            latitude = Double.parseDouble(Latitude);
                            longitude = Double.parseDouble(Longitude);

                            mapView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", latitude, longitude);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                }
                            });

                            if (path.compareTo("My Profile") == 0) {
                                button.setText("Edit");
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(DonarProfileActivity.this, EditDonarProfile.class);
                                        intent.putExtra("Name", Name);
                                        intent.putExtra("Age", Age);
                                        intent.putExtra("Phone", PhnNo);
                                        intent.putExtra("Gender", Gender);
                                        intent.putExtra("Address", Address);
                                        intent.putExtra("City", City);
                                        intent.putExtra("Location", Location);
                                        intent.putExtra("Latitude", Latitude);
                                        intent.putExtra("Longitude", Longitude);
                                        intent.putExtra("Blood Group", Blood);
                                        intent.putExtra("Email", Email);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else {
                                button.setText("CONTACT");
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + PhnNo));
                                        startActivity(intent);
                                    }
                                });
                            }
                            loadingDialog.dismissDialog();
                        } else{
                            loadingDialog.dismissDialog();
                            Toast.makeText(DonarProfileActivity.this, "User does not Exists", Toast.LENGTH_SHORT).show();
                    }}
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();
                Toast.makeText(DonarProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setDialog() {
        loadingDialog = new LoadingDialog(DonarProfileActivity.this);
        loadingDialog.startLoadingDialog();
    }

    private void setId() {
        name = (TextView) findViewById(R.id.profile_donar_name);
        date = (TextView) findViewById(R.id.profile_donar_date);
        age = (TextView) findViewById(R.id.profile_donar_age);
        gender = (TextView) findViewById(R.id.profile_donar_gender);
        phnNo = (TextView) findViewById(R.id.profile_donar_phone);
        address = (TextView) findViewById(R.id.profile_donar_address);
        city = (TextView) findViewById(R.id.profile_donar_city);
        location = (TextView) findViewById(R.id.profile_donar_location);
        blood = (TextView) findViewById(R.id.profile_donar_blood);
        email = (TextView) findViewById(R.id.profile_donar_email);
        button = (Button) findViewById(R.id.profile_donar_button);
        mapView = (Button) findViewById(R.id.profile_donar_mapView);
    }
}
