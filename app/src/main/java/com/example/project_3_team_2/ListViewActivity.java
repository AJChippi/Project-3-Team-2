package com.example.project_3_team_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.maps.android.SphericalUtil;

public class ListViewActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ListView lstTutors;
    Spinner spnListFilter;
    ArrayList<Tutor> tutors;
    TutorAdapter adapter;
    TextView txtDistance;
    RequestQueue queue;
    SharedPreferences preferences;
    String curUserID;
    FusedLocationProviderClient flpClient;
    final String TAG = "TutorHub";
    double deviceLat, deviceLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        queue = Volley.newRequestQueue(this);

        lstTutors = findViewById(R.id.lstTutors);
        txtDistance = findViewById(R.id.txtDistance);
        spnListFilter = findViewById(R.id.spnListFilter);
        tutors = new ArrayList<>();
        adapter = new TutorAdapter(tutors,this);
        lstTutors.setAdapter(adapter);

        preferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        curUserID = preferences.getString("userID","");

        String[] subjects = {"Filter","Math","Science","English","History","Computer Science","Foreign Language"};
        spnListFilter.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,subjects));
//        spnListFilter.setSelection(0);

        // Get device location for distance comparisons
        getMyLocation();

        // Append user ID
        String url = "https://findtutors.onrender.com/tutorList?userID=" + curUserID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try{
                        Log.d(TAG,response.getJSONArray("results").toString());
                        JSONArray jsonTutors = response.getJSONArray("results");
                        for (int i = 0; i < jsonTutors.length()-1; i++){
                            JSONObject t = jsonTutors.getJSONObject(i);
                            String userID = t.getString("userID");
                            String name = t.getString("name");
                            String subject = t.getString("subject");
                            double latitude, longitude;
                            try{
                                latitude = t.getDouble("latitude");
                                longitude = t.getDouble("longitude");
                            }catch (Exception e){
                                latitude =0;
                                longitude =0;
                            }

                            LatLng tutorLoc = new LatLng(latitude,longitude);
                            LatLng deviceLoc = new LatLng(deviceLat,deviceLng);

                            double distance = SphericalUtil.computeDistanceBetween(tutorLoc, deviceLoc);
                            double total = distance / 1609.344;
                            //double distance = distance(latitude,longitude,deviceLat,deviceLng);

                            Tutor nextTutor = new Tutor(userID,name,subject,latitude,longitude, (float) total);
                            tutors.add(nextTutor);
                        }
                        tutors.sort(Comparator.reverseOrder());
                        adapter.notifyDataSetChanged();
                    }catch(JSONException e){
                        Log.d(TAG,"error (post response): " + e.getMessage());
                    }
                },error->{
            Log.d(TAG,"error: " + error.getMessage());
        });

        queue.add(request);

       /* tutors.add(new Tutor("1","Example Name1","Math",1,1,10));
        tutors.add(new Tutor("2","Example Name2","Computer Science",1,1,20));
        tutors.add(new Tutor("3","Example Name3","English",1,1,40));
        tutors.add(new Tutor("3","Example Name4","Math",1,1,30));*/

        // Sort the list
//        Collections.sort(tutors);
        tutors.sort(Comparator.reverseOrder());
        adapter.notifyDataSetChanged();

        spnListFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    lstTutors.setAdapter(adapter);
                    tutors.sort(Comparator.reverseOrder());
                    adapter.notifyDataSetChanged();
                } else{
                    ArrayList<Tutor> filteredTutors = new ArrayList<>();
                    for (int j = 0; j < tutors.size(); j++){
                        Tutor t = tutors.get(j);
                        if (adapterView.getSelectedItem().toString().equals(t.subject))
                            filteredTutors.add(t);
                    }
                    TutorAdapter filteredAdapter = new TutorAdapter(filteredTutors,getApplicationContext());
                    filteredTutors.sort(Comparator.reverseOrder());
                    lstTutors.setAdapter(filteredAdapter);
                    filteredAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.d(TAG, "Location request was granted");
            else
                Log.d(TAG, "Location request was denied");
        }
    }

    public void getMyLocation() {
        //get location
        flpClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION );
        else
            Log.d(TAG,"Permission was granted");

        flpClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG,location+"");
                if (location != null) {
                    Log.d(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
                    deviceLat = location.getLatitude();
                    deviceLng = location.getLongitude();
                }
                else{
                    Log.d(TAG,"Location was null -- setting to 0");
                    deviceLat = 0;
                    deviceLng = 0;
                }
            }
        });
    }
}