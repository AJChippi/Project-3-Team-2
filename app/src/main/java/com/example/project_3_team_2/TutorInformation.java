package com.example.project_3_team_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
            Intent intent = new Intent(this, TutorEditInformation.class);
            startActivity(intent);
        });

        String URL = "https://tutorapp-qi3p.onrender.com/tutor?username=";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response ->  {
                try {
                    JSONObject jsonArray = response.getJSONObject("books");
                    txtName.setText(jsonArray.getString("name"));
                    } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
        }, error -> {
            error.printStackTrace();
            txtName.setText("Error");
            txtBio.setText("Error");
            txtEmail.setText("Error");
            txtPhoneNumber.setText("Error");
            txtSubject.setText("Error");
        });

        queue.add(request);
    }
}