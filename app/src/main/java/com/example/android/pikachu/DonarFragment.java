package com.example.android.pikachu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private double latitude, longitude;
    Boolean nearByFlag = false;
    CheckBox nearBy;
    private double latitude2, longitude2;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(getActivity(), "Location Permission Denied!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_donation, container, false);
        donar.clear();
        loginPreferences = new LoginPreferences(getActivity());
        skipPreferences = new SkipPreferences(getActivity());
        listView = v.findViewById(R.id.list_donar);
        setHeader();
        firebaseAuth = FirebaseAuth.getInstance();
        if (loginPreferences.DonarLaunch() == 1) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            UserEmail = currentUser.getEmail();
        }

        return v;
    }

    public void setDialog() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
    }

    private void setHeader(){
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
        Button apply = (Button) header.findViewById(R.id.donar_apply);
        nearBy = (CheckBox) header.findViewById(R.id.donar_nearMe);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    getCurrentLocation();
                }
            }
        });
        listView.setAdapter(null);
    }

    private void getData() {
        donar.clear();

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
                                String latt = document.getString("Latitude");
                                String longg = document.getString("Longitude");
                                latitude2 = Double.parseDouble(latt);
                                longitude2 = Double.parseDouble(longg);
                                if (loginPreferences.DonarLaunch() == 1) {
                                    if (email.compareTo(UserEmail) == 0)
                                        continue;
                                }
                                if(!nearByFlag)
                                donar.add(new donarList(name, blood, city, date, gender, email));
                                else{
                                    if(calculateDistance(latitude2,longitude2)<=10)
                                        donar.add(new donarList(name, blood, city, date, gender, email));
                                    else
                                        continue;
                                }
                            }
                            loadingDialog.dismissDialog();
                            DonarAdapter donarAdapter = new DonarAdapter(getActivity(), donar);


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

    private void getCurrentLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            setFilter();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void setFilter() {
        if (nearBy.isChecked())
            nearByFlag = true;
        else
            nearByFlag = false;

        getData();

    }

    public double calculateDistance(double lat1, double lon1) {
        final double lat = latitude;
        final double lon = longitude;
        double lon2, lat2;

        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double r = 6371;
        return Math.round((c * r) * 100.0) / 100.0;
    }
}

