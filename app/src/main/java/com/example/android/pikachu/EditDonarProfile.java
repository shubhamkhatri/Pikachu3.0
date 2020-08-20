package com.example.android.pikachu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditDonarProfile extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView location_address;
    private Button select_location;
    private double latitude, longitude;
    private RadioButton a_button, b_button, o_button, ab_button, negative_button, positive_button;
    private String blood, symbol, bloodGroup, locationn, Gender, UserEmail;
    private Button save;
    private ResultReceiver resultReceiver;
    private EditText name, age, phn_no, address, city;
    private FirebaseAuth firebaseAuth;
    private boolean male, female;
    private LoadingDialog loadingDialog;
    private RadioButton genderMale, genderFemale;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String Namee, Agee, Phn_noo, Genderr, Addresss, Cityy, Locationn, Latitudee, Longitudee, BloodGroupp;
    private String lat, lon;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(EditDonarProfile.this, "Location Permission Denied!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donar_profile);
        resultReceiver = new AdressResultReceiverEdit(new Handler());
        Intent i = getIntent();
        UserEmail = i.getStringExtra("Email");
        Namee = i.getStringExtra("Name");
        Agee = i.getStringExtra("Age");
        Phn_noo = i.getStringExtra("Phone");
        Genderr = i.getStringExtra("Gender");
        Addresss = i.getStringExtra("Address");
        Cityy = i.getStringExtra("City");
        Locationn = i.getStringExtra("Location");
        Latitudee = i.getStringExtra("Latitude");
        Longitudee = i.getStringExtra("Longitude");
        BloodGroupp = i.getStringExtra("Blood Group");

        setId();
        setDefault();

        select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditDonarProfile.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    getCurrentLocation();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                bloodGroup = bloodCheck().concat(symbolCheck());
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.err_msg));
                    Toast.makeText(EditDonarProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (age.getText().toString().trim().isEmpty()) {
                    age.setError(getString(R.string.err_msg));
                    Toast.makeText(EditDonarProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (phn_no.getText().toString().trim().isEmpty()) {
                    phn_no.setError(getString(R.string.err_msg));
                    Toast.makeText(EditDonarProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (genderCheck().isEmpty()) {
                    Toast.makeText(EditDonarProfile.this, "Please select Gender", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().isEmpty()) {
                    address.setError(getString(R.string.err_msg));
                    Toast.makeText(EditDonarProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (city.getText().toString().trim().isEmpty()) {
                    city.setError(getString(R.string.err_msg));
                    Toast.makeText(EditDonarProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (locationn == null || locationn.isEmpty()) {
                    Toast.makeText(EditDonarProfile.this, "Please select Location", Toast.LENGTH_SHORT).show();
                } else if (bloodCheck().isEmpty()) {
                    Toast.makeText(EditDonarProfile.this, "Please select Blood Group", Toast.LENGTH_SHORT).show();
                } else if (symbolCheck().isEmpty()) {
                    Toast.makeText(EditDonarProfile.this, "Please select Blood Group", Toast.LENGTH_SHORT).show();
                } else {
                    dataUpdate();
                    setDialog();
                }
            }
        });

    }
    public void setDialog() {
        loadingDialog = new LoadingDialog(EditDonarProfile.this);
        loadingDialog.startLoadingDialog();
    }

    private void setDefault() {
        name.setText(Namee);
        age.setText(Agee);
        phn_no.setText(Phn_noo);
        address.setText(Addresss);
        city.setText(Cityy);
        lat = Latitudee;
        lon = Longitudee;
        locationn = Locationn;
        location_address.setText(locationn);
        if (Genderr.compareTo("Male") == 0) {
            genderMale.setChecked(true);
        } else {
            genderFemale.setChecked(true);
        }
        int length = BloodGroupp.length();
        String bloodd = BloodGroupp.substring(0, length - 1);
        String symboll = BloodGroupp.substring(length - 1);
        if (bloodd.equals("A"))
            a_button.setChecked(true);
        else if (bloodd.equals("B"))
            b_button.setChecked(true);
        else if (bloodd.equals("O"))
            o_button.setChecked(true);
        else
            ab_button.setChecked(true);

        if (symboll.equals("+"))
            positive_button.setChecked(true);
        else
            negative_button.setChecked(true);

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

        Map<String, Object> donar = new LinkedHashMap<>();

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
        LocalDate date = LocalDate.now();
        String d = String.valueOf(date);
        donar.put("Date", d);

        db.collection("donars").document(UserEmail).update(donar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditDonarProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                        finish();
                        Intent i = new Intent(EditDonarProfile.this, DonarProfileActivity.class);
                        i.putExtra("Email", UserEmail);
                        i.putExtra("Path", "My Profile");
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(EditDonarProfile.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG Database Error", e.toString());
                    }
                });

    }


    private void setId() {
        a_button = (RadioButton) findViewById(R.id.donar_blood_a_edit);
        b_button = (RadioButton) findViewById(R.id.donar_blood_b_edit);
        ab_button = (RadioButton) findViewById(R.id.donar_blood_ab_edit);
        o_button = (RadioButton) findViewById(R.id.donar_blood_o_edit);
        negative_button = (RadioButton) findViewById(R.id.donar_blood_negative_edit);
        positive_button = (RadioButton) findViewById(R.id.donar_blood_positive_edit);
        save = (Button) findViewById(R.id.donar_edit_save);
        location_address = findViewById(R.id.donar_location_address_edit);
        select_location = (Button) findViewById(R.id.donar_location_button_edit);

        name = (EditText) findViewById(R.id.donar_name_edit);
        age = (EditText) findViewById(R.id.donar_age_edit);
        phn_no = (EditText) findViewById(R.id.donar_phnNo_edit);
        genderMale = (RadioButton) findViewById(R.id.donar_male_edit);
        genderFemale = (RadioButton) findViewById(R.id.donar_female_edit);
        address = (EditText) findViewById(R.id.donar_address_edit);
        city = (EditText) findViewById(R.id.donar_city_edit);

    }

    private void getCurrentLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(EditDonarProfile.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(EditDonarProfile.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            lat = String.valueOf(latitude);
                            lon = String.valueOf(longitude);
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
        Intent intent = new Intent(EditDonarProfile.this, FetchAddress.class);
        intent.putExtra(constants.RECEIVER, resultReceiver);
        intent.putExtra(constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AdressResultReceiverEdit extends ResultReceiver {

        AdressResultReceiverEdit(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == constants.SUCCESS_RESULT) {
                locationn = resultData.getString(constants.RESULT_DATA_KEY);
                location_address.setText(locationn);
            } else {
                Toast.makeText(EditDonarProfile.this, resultData.getString(constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
