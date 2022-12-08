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



        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(View -> {
            Intent intent = new Intent(this, GoogleMaps.class);
            startActivity(intent);
        });

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
        String url = "https://tutorapp-qi3p.onrender.com/loginUser";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,null,
                response->{
                    Log.d("API_POST", "Getting data");
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", username);
                        jsonBody.put("password", password);
//                            JSONObject jsonObject = response.getJSONObject("results");
//                            userID = jsonObject.toString();
//                            Log.d("API_POST", jsonObject.toString());

                    } catch (JSONException e) {
                        Log.e("MainActivity", "Error creating JSON object: " + e);
                    }

                    Log.d("API_POST", "Finished getting data");
                },error->{
            Log.d("API_POST", error.toString());
        });


    }


    private void verifyLogin() {
        // First, get a reference to the SharedPreferences object
        SharedPreferences sharedPref = getSharedPreferences("my_prefs", MainActivity.MODE_PRIVATE);

        // Now, we need an editor object to make changes to the preferences
        SharedPreferences.Editor editor = sharedPref.edit();

        String url = "https://tutorapp-qi3p.onrender.com/tutorUser";
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
            String url = "https://tutorapp-qi3p.onrender.com/registerUser";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,null,
                    response->{
                        Log.d("API_GET", "Getting data");
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("username", editEmail);
                            jsonBody.put("password", editPassword);
                            jsonBody.put("userType", UserType);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("API_GET", e.getMessage());
                        }
                        //Log.d("API_GET", "Finished getting data");
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
            UserType = "Student";
        }
        Log.d("Sfasf", UserType);
    }
}