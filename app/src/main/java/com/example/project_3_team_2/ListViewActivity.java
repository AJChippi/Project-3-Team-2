package com.example.project_3_team_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    ListView lstTutors;
    Spinner spnListFilter;
    ArrayList<Tutor> tutors;
    TutorAdapter adapter;
    RequestQueue queue;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        queue = Volley.newRequestQueue(this);

        lstTutors = findViewById(R.id.lstTutors);
        spnListFilter = findViewById(R.id.spnListFilter);
        tutors = new ArrayList<>();
        adapter = new TutorAdapter(tutors,this);
        lstTutors.setAdapter(adapter);
//        preferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        String[] subjects = {"Filter","Math","Science","English","History","Computer Science","Foreign Language"};
        spnListFilter.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,subjects));
//        spnListFilter.setSelection(0);

        String url = "https://findtutors.onrender.com/tutorList";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
//                        Log.d("TutorHub",response+"");
                    try{
                        Log.d("TutorHub",response.getJSONArray("results").getJSONArray(0)+"");
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

                            tutors.add(new Tutor(userID,name,subject,latitude,longitude));
                        }
                        adapter.notifyDataSetChanged();
                    }catch(JSONException e){
                        Log.d("TutorHub","error (post response): " + e.getMessage());
                    }
                },error->{
            Log.d("TutorHub","error: " + error.getMessage());
        });

        queue.add(request);

        tutors.add(new Tutor("1","Example Name1","MATH",1,1));
        tutors.add(new Tutor("2","Example Name2","CS",1,1));
        tutors.add(new Tutor("3","Example Name3","CIS",1,1));

        spnListFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<Tutor> filteredTutors = new ArrayList<>();
                for (int j = 0; j < tutors.size(); j++){
//                    if ()
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}