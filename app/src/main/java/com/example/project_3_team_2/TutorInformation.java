package com.example.project_3_team_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TutorInformation extends AppCompatActivity {

    TextView txtName, txtSubject, txtPhoneNumber, txtEmail;
    Button btnGoBack;
    MultiAutoCompleteTextView txtBio;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_information);

        txtName = findViewById(R.id.txtName);
        txtSubject = findViewById(R.id.txtSubject);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtBio = findViewById(R.id.txtBio);
        btnGoBack = findViewById(R.id.btnGoBack);
        queue = Volley.newRequestQueue(this);

        btnGoBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoogleMaps.class);
            startActivity(intent);
        });

        //get the phone number and intent to the phone app
        txtPhoneNumber.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + txtPhoneNumber.getText().toString()));
            startActivity(intent);
        });

        //get the email and intent to the email app
        txtEmail.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{txtEmail.getText().toString()});
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        Intent intent = getIntent();
        String URL = "https://findtutors.onrender.com/tutorUser?userID=" + intent.getStringExtra("userID");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response ->  {
                try {
                    //get the username of the json above
                    JSONArray jsonArray = response.getJSONArray("results");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String name = jsonObject.getString("name");
                    String subject = jsonObject.getString("subject");
                    String phoneNumber = jsonObject.getString("phoneNumber");
                    String email = jsonObject.getString("email");
                    String bio = jsonObject.getString("bio");
                    //set the text of the textviews to the username
                    txtName.setText(name);
                    txtSubject.setText(subject);
                    txtPhoneNumber.setText(phoneNumber);
                    txtEmail.setText(email);
                    txtBio.setText(bio);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
            });
        queue.add(request);
    }
}