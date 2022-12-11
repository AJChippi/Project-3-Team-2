package com.example.project_3_team_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    RequestQueue queue;
    final String TAG = "GOOGLEMAPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        queue = Volley.newRequestQueue(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        //Set the camera to svsu
        LatLng svsu = new LatLng(42.7325, -84.5555);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(svsu,7));
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        Log.d("shared", sharedPreferences.getString("userID", ""));
        String url = "https://findtutors.onrender.com/tutorList?userID="+sharedPreferences.getString("userID", "");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try {

                        JSONArray jsonArray = response.getJSONArray("results");
                        for(int i = 0; i < jsonArray.length()-1; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d(TAG, "onMapReady: "+jsonObject);
                            String longitude ="";
                            String latitude = "";
                            try {
                                longitude = jsonObject.getString("longitude");
                                latitude = jsonObject.getString("latitude");
                            }catch (Exception e){
                                Log.d(TAG, "eororor: "+e);
                            }
                            Log.d(TAG,"LONGITUDE" +longitude);
                            String name = jsonObject.getString("name");
                            String subject = jsonObject.getString("subject");
                            String description = jsonObject.getString("bio");
                            String userID = jsonObject.getString("userID");

                            LatLng tutorLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            Log.d(TAG, "i wanna die"+tutorLocation.toString());
                            Marker m = map.addMarker(new MarkerOptions().position(tutorLocation).title(name).snippet("Subject: " + subject + " : Description: "+ description));
                            m.setTag(userID);
                        }

                        map.setOnInfoWindowClickListener(marker -> {
                            Intent intent = new Intent(GoogleMaps.this, TutorInformation.class);
                            intent.putExtra("userID", marker.getTag().toString());
                            Log.d(TAG, "marker: "+marker.getTag().toString());
                            startActivity(intent);
                        });

                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        e.printStackTrace();
                    }
                },error->{
            Log.d(TAG,"error: " + error.getMessage());
        });

        queue.add(request);
    }
}
