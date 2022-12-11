package com.example.project_3_team_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorEditInformation extends AppCompatActivity {
    EditText editName, editPhoneNumber, editEmail, editLongitude, editLatitude, editBio;
    Spinner locationSpinner, subjectSpinner;
    Button btnSave;
    String location;
    SensorManager sensorManager;
    String TAG = "TutorEditInformation";
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient flpClient;
    RequestQueue MyRequestQueue;

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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        MyRequestQueue = Volley.newRequestQueue(this);


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
                    editLatitude.setText("");
                    editLongitude.setText("");
                } else {
                    getMyLocation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // get the shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);

        btnSave.setOnClickListener(view -> {
            String name = editName.getText().toString();
            String phoneNumber = editPhoneNumber.getText().toString();
            String email = editEmail.getText().toString();
            String longitude = editLongitude.getText().toString();
            String latitude = editLatitude.getText().toString();
            String bio = editBio.getText().toString();
            String subject = subjectSpinner.getSelectedItem().toString();
            if(name.isEmpty()) {
                editName.setError("Full name is required!");
            }
            else if(phoneNumber.isEmpty()) {
                editPhoneNumber.setError("phone number is required!");
            }
            else if(email.isEmpty()) {
                editEmail.setError("email is required!");
            }
            else if(longitude.isEmpty()) {
                editLongitude.setError("longitude is required! Maybe switch to device location");
            }
            else if(latitude.isEmpty()) {
                editLatitude.setError("latitude is required! Maybe switch to device location");
            }
            else if(bio.isEmpty()) {
                editBio.setError("Please enter a bio!");
            }else {

                String url = "https://findtutors.onrender.com/updateUser?userID=" + sharedPreferences.getString("userID", "");

                //log the shared preferences
                Log.d(TAG, "onCreate: " + sharedPreferences.getString("userID", ""));
                JSONObject obj = new JSONObject();
                Log.d(TAG, "onCreate: " + obj);
                try {
                    Log.d(TAG, "SDLFJGlkSJDGLKJSEDLGD");
                    obj.put("userID", sharedPreferences.getString("userID", ""));
                    Log.d(TAG, "onCreate: " + sharedPreferences.getString("userID", ""));
                    obj.put("name", name);
                    Log.d(TAG, "onCreate: " + name);
                    obj.put("phoneNumber", phoneNumber);
                    Log.d(TAG, "onCreate: " + phoneNumber);
                    obj.put("email", email);
                    obj.put("longitude", longitude);
                    Log.d(TAG, "onCreate: " + longitude);
                    obj.put("latitude", latitude);
                    Log.d(TAG, "onCreate: " + latitude);
                    obj.put("bio", bio);
                    obj.put("subject", subject);
                    Log.d(TAG, "onCreate: " + subject);
                } catch (JSONException e) {
                    Log.d(TAG, "onCreate: " + e);
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                });
                Toast.makeText(this, "Saved Information", Toast.LENGTH_SHORT).show();
                MyRequestQueue.add(jsonObjectRequest);
                Intent intent = new Intent(TutorEditInformation.this, GoogleMaps.class);
                startActivity(intent);
            }
        });
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
        //get location
        flpClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        flpClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("MYLOC", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    editLongitude.setText(String.valueOf(location.getLongitude()));
                    editLatitude.setText(String.valueOf(location.getLatitude()));
                }
            }
        });
    }
}