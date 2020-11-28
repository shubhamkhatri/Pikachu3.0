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

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddDonar extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView location_address, login;
    private Button select_location;
    private double latitude, longitude;
    private RadioButton a_button, b_button, o_button, ab_button, negative_button, positive_button;
    private String blood, symbol, bloodGroup, locationn, Gender;
    private Button save;
    private ResultReceiver resultReceiver;
    private EditText name, age, phn_no, address, city, email, pass, confirmPass;
    private TextInputLayout Password, ConfirmPass;
    private FirebaseAuth firebaseAuth;
    private boolean male, female;
    private RadioButton genderMale, genderFemale;
    private ProgressDialog progressDialog;
    private LoadingDialog loadingDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(AddDonar.this, "Location Permission Denied!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setDialog() {
        loadingDialog = new LoadingDialog(AddDonar.this);
        loadingDialog.startLoadingDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donar);

        resultReceiver = new AdressResultReceiver(new Handler());
        setId();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddDonar.this, LoginActivity.class));
            }
        });

        select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddDonar.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    getCurrentLocation();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodGroup = bloodCheck().concat(symbolCheck());
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (age.getText().toString().trim().isEmpty()) {
                    age.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (phn_no.getText().toString().trim().isEmpty()) {
                    phn_no.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (genderCheck().isEmpty()) {
                    Toast.makeText(AddDonar.this, "Please select Gender", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().isEmpty()) {
                    address.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (city.getText().toString().trim().isEmpty()) {
                    city.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (locationn == null || locationn.isEmpty()) {
                    Toast.makeText(AddDonar.this, "Please select Location", Toast.LENGTH_SHORT).show();
                } else if (bloodCheck().isEmpty()) {
                    Toast.makeText(AddDonar.this, "Please select Blood Group", Toast.LENGTH_SHORT).show();
                } else if (symbolCheck().isEmpty()) {
                    Toast.makeText(AddDonar.this, "Please select Blood Group", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().trim().isEmpty()) {
                    email.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (pass.getText().toString().trim().isEmpty()) {
                    Password.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (confirmPass.getText().toString().trim().isEmpty()) {
                    ConfirmPass.setError(getString(R.string.err_msg));
                    Toast.makeText(AddDonar.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (pass.getText().toString().trim().compareTo(confirmPass.getText().toString().trim()) != 0) {
                    ConfirmPass.setError("Password do not match");
                } else {
                    name.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    age.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    phn_no.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    address.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    city.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    email.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    pass.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    confirmPass.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    progressDialog.setMessage("SignUp in progress");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    String user_email = email.getText().toString().trim();
                    String user_password = confirmPass.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddDonar.this, "Username and Password saved", Toast.LENGTH_SHORT).show();
                                dataUpdate();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AddDonar.this, "Error!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dataUpdate() {
        String Name = name.getText().toString().trim();
        String Age = age.getText().toString().trim();
        String PhoneNo = phn_no.getText().toString().trim();
        Gender = genderCheck();
        String Address = address.getText().toString().trim();
        String City = city.getText().toString().trim();
        String Location = locationn;
        bloodGroup = bloodCheck().concat(symbolCheck());
        String Email = email.getText().toString().trim();
        String Passwordd = pass.getText().toString().trim();

        Map<String, Object> donar = new LinkedHashMap<>();

        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);
        donar.put("Name", Name);
        donar.put("Age", Age);
        donar.put("Phone", PhoneNo);
        donar.put("Gender", Gender);
        donar.put("Address", Address);
        donar.put("City", City);
        donar.put("Location", Location);
        donar.put("Latitude", lat);
        donar.put("Longitude", lon);
        donar.put("Blood Group", bloodGroup);
        donar.put("Email", Email);
        donar.put("Password", Passwordd);
        LocalDate date = LocalDate.now();
        String d = String.valueOf(date);
        donar.put("Date", d);

        db.collection("donars").document(Email).set(donar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendEmailVerification();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddDonar.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG Database Error", e.toString());
                    }
                });

    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddDonar.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(AddDonar.this, LoginActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AddDonar.this, "Error Sending Mail!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setId() {
        a_button = (RadioButton) findViewById(R.id.donar_blood_a);
        b_button = (RadioButton) findViewById(R.id.donar_blood_b);
        ab_button = (RadioButton) findViewById(R.id.donar_blood_ab);
        o_button = (RadioButton) findViewById(R.id.donar_blood_o);
        negative_button = (RadioButton) findViewById(R.id.donar_blood_negative);
        positive_button = (RadioButton) findViewById(R.id.donar_blood_positive);
        save = (Button) findViewById(R.id.donar_save);
        location_address = findViewById(R.id.donar_location_address);
        select_location = (Button) findViewById(R.id.donar_location_button);

        name = (EditText) findViewById(R.id.donar_name);
        age = (EditText) findViewById(R.id.donar_age);
        phn_no = (EditText) findViewById(R.id.donar_phnNo);
        genderMale = (RadioButton) findViewById(R.id.donar_male);
        genderFemale = (RadioButton) findViewById(R.id.donar_female);
        address = (EditText) findViewById(R.id.donar_address);
        city = (EditText) findViewById(R.id.donar_city);
        email = (EditText) findViewById(R.id.donar_email);
        pass = (EditText) findViewById(R.id.donar_password);
        confirmPass = (EditText) findViewById(R.id.donar_confirmPass);

        Password = (TextInputLayout) findViewById(R.id.donar_password_layout);
        ConfirmPass = (TextInputLayout) findViewById(R.id.donar_confirmPass_layout);

        login = (TextView) findViewById(R.id.donar_login);
    }

    private void getCurrentLocation() {
        setDialog();
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(AddDonar.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(AddDonar.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddresFromLatLong(location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    public String genderCheck() {
        String g = "";
        male = genderMale.isChecked();
        female = genderFemale.isChecked();
        if (male)
            g = "Male";
        if (female)
            g = "Female";

        return g;
    }

    public String bloodCheck() {
        blood = "";
        if (a_button.isChecked())
            blood = "A";
        if (b_button.isChecked())
            blood = "B";
        if (ab_button.isChecked())
            blood = "AB";
        if (o_button.isChecked())
            blood = "O";
        return blood;
    }

    public String symbolCheck() {
        symbol = "";
        if (negative_button.isChecked())
            symbol = "-";
        if (positive_button.isChecked())
            symbol = "+";
        return symbol;
    }

    private void fetchAddresFromLatLong(Location location) {
        Intent intent = new Intent(AddDonar.this, FetchAddress.class);
        intent.putExtra(constants.RECEIVER, resultReceiver);
        intent.putExtra(constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AdressResultReceiver extends ResultReceiver {

        AdressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == constants.SUCCESS_RESULT) {
                locationn = resultData.getString(constants.RESULT_DATA_KEY);
                location_address.setText(locationn);
                loadingDialog.dismissDialog();
            } else {
                loadingDialog.dismissDialog();
                Toast.makeText(AddDonar.this, resultData.getString(constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
