package com.example.project_3_team_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.List;

public class TutorEditInformation extends AppCompatActivity {
    EditText editName, editPhoneNumber, editEmail, editLongitude, editLatitude, editBio;
    Spinner locationSpinner, subjectSpinner;
    Button btnSave;
    String location;
    String TAG = "TutorEditInformation";
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient flpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_edit_information);
        editName = findViewById(R.id.editName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editLongitude = findViewById(R.id.editLongitude);
        editLatitude = findViewById(R.id.editLatitude);
        editBio = findViewById(R.id.editBio);
        locationSpinner = findViewById(R.id.locationSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(view -> {
            Toast.makeText(this, "Saved Information", Toast.LENGTH_SHORT).show();

        });

        //get spinner values when changed
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = parent.getItemAtPosition(position).toString();
                //if location equals Custom, then show edit text for longitude and latitude
                if (location.equals("Custom")) {
                    editLongitude.setVisibility(View.VISIBLE);
                    editLatitude.setVisibility(View.VISIBLE);
                } else {
                    editLongitude.setVisibility(View.GONE);
                    editLatitude.setVisibility(View.GONE);
                    if (ActivityCompat.checkSelfPermission(TutorEditInformation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(TutorEditInformation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    else
                        Log.d("MYLOC", "Location: permissions granted");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        //get all the information and store it into a variable

        /*String name = editName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        String email = editEmail.getText().toString();
        String longitude = editLongitude.getText().toString();
        String latitude = editLatitude.getText().toString();
        String bio = editBio.getText().toString();
        String location = locationSpinner.getSelectedItem().toString();
        String subject = subjectSpinner.getSelectedItem().toString();*/


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.d(TAG, "LOcation request was granted");
            else
                Log.d(TAG, "LOcation request was denied");
        }
    }

    public void getMyLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

//        flpClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location!=null)
//                    Log.d(TAG,"My location is "+location.toString());
//                else
//                    Log.d(TAG,"My location is null");
//            }
//        });

        flpClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
            if (location != null) {
                Log.d(TAG, "My location is " + location.toString());
                Geocoder geocoder = new Geocoder(this);

                try {
                    List<Address> addrList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Log.d(TAG, addrList.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                Log.d(TAG, "My location is null");
        });
    }

    public void trackMyLocation() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(1000);
        request.setWaitForAccurateLocation(true);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, locationResult.toString());

                Location l = locationResult.getLocations().get(0);
                Log.d("ajsdlgk", l + "");

            }
        };

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
        flpClient.requestLocationUpdates(request, callback, Looper.getMainLooper());
    }
}