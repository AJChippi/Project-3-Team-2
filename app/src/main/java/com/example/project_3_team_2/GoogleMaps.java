package com.example.project_3_team_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
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

    private GoogleMap mMap;
    RequestQueue queue;
    final String TAG = "TutorHub";

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
        GoogleMap map = googleMap;

        //Set the camera to svsu
        LatLng svsu = new LatLng(43.51301, -83.96065);
        /*
        map.addMarker(new MarkerOptions()
                .position(svsu)
                .title("Marker in Svsu"));
                */
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(svsu,13));

        String url = "https://findtutors.onrender.com/tutorList?userID=";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try{
                        Log.d(TAG,response.getJSONArray("results").getJSONArray(0)+"");
                        JSONArray jsonTutors = response.getJSONArray("results").getJSONArray(0);
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

                            // Get distance to tutor
                            float[] result = new float[1];
                            //Location.distanceBetween(deviceLat,deviceLng,latitude,longitude,result);
                            float distance = result[0];
                            LatLng newTutor = new LatLng(latitude, longitude);
                            map.addMarker(new MarkerOptions()
                                    .position(newTutor)
                                    .title(name + " Subject: " + subject));
                            //Tutor nextTutor = new Tutor(userID,name,subject,latitude,longitude,distance);
                            //tutors.add(nextTutor);
                        }
                        //tutors.sort(Comparator.reverseOrder());
                        //adapter.notifyDataSetChanged();
                    }catch(JSONException e){
                        Log.d(TAG,"error (post response): " + e.getMessage());
                    }
                },error->{
            Log.d(TAG,"error: " + error.getMessage());
        });


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                //show the information for the tutor
                Intent intent = new Intent(GoogleMaps.this, TutorInformation.class);
                startActivity(intent);
            }
        });
    }
}
