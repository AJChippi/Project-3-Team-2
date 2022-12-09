package com.example.project_3_team_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.util.Log;
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

        String url = "https://tutorapp-qi3p.onrender.com/tutorList";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try{
                        JSONArray jsonTutors = response.getJSONArray("results").getJSONObject(0).getJSONArray("tutors");
                        for (int i = 0; i < jsonTutors.length(); i++){
                            JSONObject t = jsonTutors.getJSONObject(i);
                            if (t.getString("userType").equals("TUTOR")){
                                String userID = t.getString("userID");
                                String name = t.getString("name");
                                String subject = t.getString("subject");
                                double latitude = t.getDouble("latitude");
                                double longitude = t.getDouble("longitude");
                                tutors.add(new Tutor(userID,name,subject,latitude,longitude));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                },error->{
            Log.d("TutorHub","There was an error");
        });

        queue.add(request);

        tutors.add(new Tutor("1","Example Name1","MATH",1,1));
        tutors.add(new Tutor("2","Example Name2","CS",1,1));
        tutors.add(new Tutor("3","Example Name3","What",1,1));
    }
}