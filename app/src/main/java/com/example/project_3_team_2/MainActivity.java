package com.example.project_3_team_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    Button btnSignUp;
    Button btnListView;
    RequestQueue queue;
    RadioGroup groupUserType;
    TextView errorText;
    public String username;
    public String password;
    private String userID;
    private String UserType = "STUDENT";

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

    @SuppressLint("ClickableViewAccessibility")
    private void loginHandler() {
        View view = getLayoutInflater().inflate(R.layout.login_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this, R.style.login_layout);
        builder.setView(view).show();

        Button btnLogin = view.findViewById(R.id.btnUserLogin);
        EditText editEmail = view.findViewById(R.id.etEntEmail);
        EditText editPassword = view.findViewById(R.id.etEntPassword);
        errorText = view.findViewById(R.id.errorText);
        errorText.setVisibility(View.INVISIBLE);

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText.setVisibility(View.INVISIBLE);
            }

        });

        editEmail.setOnTouchListener((view12, motionEvent) -> {
            errorText.setVisibility(View.INVISIBLE);
            return false;
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText.setVisibility(View.INVISIBLE);
            }

        });

        editPassword.setOnTouchListener((view13, motionEvent) -> {
            errorText.setVisibility(View.INVISIBLE);
            return false;
        });


//        editPassword.setOnFocusChangeListener((view13, b) -> errorText.setVisibility(View.INVISIBLE));

        btnLogin.setOnClickListener(view1 -> {
            username = String.valueOf(editEmail.getText());
            password = String.valueOf(editPassword.getText());
            postLoginUser();
        });


    }


    private void postLoginUser() {
        String url = "https://findtutors.onrender.com/loginUser";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("API_GET", e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, response -> {
            Log.d("TAG", "onResponse: " + response);
            //share the userID
            SharedPreferences sharedPref = getSharedPreferences("my_prefs", MainActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                JSONArray jsonArray = response.getJSONArray("results");
                JSONObject jsonStatus = jsonArray.getJSONObject(1);
                JSONObject jsonUserID = jsonArray.getJSONObject(0);
                String status = jsonStatus.getString("status");
                userID = jsonUserID.getString("userID");
                String userType = jsonUserID.getString("userType");

                Intent intent;
                if(status.equals("200")) {
                    if(userType.equals("STUDENT")) {
                    intent = new Intent(this, ListViewActivity.class);
                        editor.putString("userID", userID);
                        editor.apply();
                    } else {
                    intent = new Intent(this, GoogleMaps.class);
                        editor.putString("userID", userID);
                        editor.apply();
                    }
                    startActivity(intent);
                }
            } catch (JSONException e) {
                errorText.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "onErrorResponse: " + error));
        queue.add(jsonObjectRequest);
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
//                        Intent intent;
//                        if(UserType.equals("Student")) {
//                            intent = new Intent(this, GoogleMaps.class);
//                        } else {
//                            intent = new Intent(this, TutorEditInformation.class);
//                        }
//                        startActivity(intent);
                        Toast.makeText(this.getApplicationContext(),"Register Successful",Toast.LENGTH_LONG).show();
                        loginHandler();
                        Intent intent;
                        if(UserType.equals("STUDENT")) {
                            intent = new Intent(this, ListViewActivity.class);
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
    }
}