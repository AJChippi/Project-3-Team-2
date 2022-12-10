package com.example.project_3_team_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    Button btnSignUp;
    Button btnInfo;
    RequestQueue queue;
    RadioGroup groupUserType;

    private String username;
    private String password;
    private String userID;
    private String UserType = "Student";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        groupUserType = findViewById(R.id.groupUserType);
        queue = Volley.newRequestQueue(this);


        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(view -> {
            loginHandler();
        });

        btnSignUp.setOnClickListener(view -> {
            signupHandler();
        });

    }

    private void loginHandler() {
        View view = getLayoutInflater().inflate(R.layout.login_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this, R.style.login_layout);
        builder.setView(view).show();

        Button btnLogin = view.findViewById(R.id.btnUserLogin);
        EditText editEmail = view.findViewById(R.id.etEntEmail);
        EditText editPassword = view.findViewById(R.id.etEntPassword);
        username = String.valueOf(editEmail.getText());
        password = String.valueOf(editPassword.getText());



        btnLogin.setOnClickListener(view1 -> {
            postLoginUser();
            verifyLogin();
        });


    }


    private void postLoginUser() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);

        } catch (JSONException e) {
            Log.e("MainActivity", "Error creating JSON object: " + e);
        }
        String url = "https://findtutors.onrender.com/loginUser";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                response->{
                    Log.d("API_POST", "onResponse: " + response);
                },error->{
            Log.d("API_POST", "onErrorResponse: " + error);
        });
        queue.add(request);
    }


    private void verifyLogin() {
        // First, get a reference to the SharedPreferences object
        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MainActivity.MODE_PRIVATE);

        // Now, we need an editor object to make changes to the preferences
        SharedPreferences.Editor editor = sharedPref.edit();

        String url = "https://findtutors.onrender.com/tutorUser";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try {
                        JSONObject jsonPerson = response.getJSONObject("results");
                        if(response.getJSONObject("results").getString("status").equals("200")) {
                            Intent intent = new Intent(this, TutorEditInformation.class);
                            startActivity(intent);
                        }
                        userID = jsonPerson.getString("userID");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },error->{
        });

        // Save the userID in shared preference
        editor.putString("userID", userID);
        // Apply the changes to the preferences
        editor.apply();
        queue.add(request);
    }



    private void signupHandler() {
        View view2 = getLayoutInflater().inflate(R.layout.signup_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this, R.style.signup_layout);

        Button btnSignup = view2.findViewById(R.id.btnNewSignUp);
        EditText editEmail = view2.findViewById(R.id.etEntNewEmail);
        EditText editPassword = view2.findViewById(R.id.etEntNewPassword);

        builder.setView(view2).show();


        btnSignup.setOnClickListener(view1 -> {
            String url = "https://findtutors.onrender.com/registerUser";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", editEmail.getText());
                jsonBody.put("password", editPassword.getText());
                jsonBody.put("userType", UserType);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("API_GET", e.getMessage());
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                    response->{
                        Log.d("signUp", "onResponse: " + response);
                        //share the userID
                        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MainActivity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            Log.d("signUp", "jsonarray: " + jsonArray);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            Log.d("signUp", "jsonobject: " + jsonObject.getString("userID"));
                            String user = jsonObject.getString("userID");
                            editor.putString("userID", user);
                            editor.apply();
                        } catch (JSONException e) {
                            Log.d("signUp", "error: " + e.getMessage());
                            e.printStackTrace();
                        }
                        Intent intent;
                        if(UserType.equals("Student")) {
                            intent = new Intent(this, GoogleMaps.class);
                        } else {
                            intent = new Intent(this, TutorEditInformation.class);
                        }
                        startActivity(intent);
                    },error->{
                Log.d("API_GET", error.toString());
            });
            queue.add(request);
        });
    }

    public void checkUserType(View v){
        CheckBox chkSelected = (CheckBox) v;
        if(chkSelected.isChecked()) {
            UserType = "TUTOR";
            chkSelected.isChecked();
        }
        else {
            UserType = "STUDENT";
        }
        Log.d("Sfasf", UserType);
    }
}