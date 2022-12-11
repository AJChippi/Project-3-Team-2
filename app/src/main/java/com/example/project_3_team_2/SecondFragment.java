package com.example.project_3_team_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_3_team_2.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondFragment extends Fragment {
    Button btnLogin;
    Button btnSignUp;
    Button btnListView;
    RequestQueue queue;
    RadioGroup groupUserType;
    TextView errorText;
    public String username;
    public String password;
    private String userID;
    private String UserType = "TUTOR";
    private FragmentSecondBinding binding;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupUserType = view.findViewById(R.id.groupUserType);
        queue = Volley.newRequestQueue(getActivity());

        btnLogin = view.findViewById(R.id.btnSignIn);
        btnSignUp = view.findViewById(R.id.btnCreateAccount);


//        btnListView = view.findViewById(R.id.btnListView);
//        btnListView.setOnClickListener(v->{
//            Intent intent = new Intent(getActivity(), ListViewActivity.class);
//            startActivity(intent);
//        });

        btnLogin.setOnClickListener(view2 -> {
            loginHandler();
        });

        btnSignUp.setOnClickListener(view2 -> {
            signupHandler();
        });



    }

    @SuppressLint("ClickableViewAccessibility")
    private void loginHandler() {
        View view = getLayoutInflater().inflate(R.layout.login_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity(), R.style.login_layout);
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
            SharedPreferences sharedPref = getActivity().getSharedPreferences("my_prefs", MainActivity.MODE_PRIVATE);
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
                        intent = new Intent(getActivity(), ListViewActivity.class);
                        editor.putString("userID", userID);
                        editor.apply();
                    } else {
                        intent = new Intent(getActivity(), GoogleMaps.class);
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
                getActivity(), R.style.signup_layout);

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
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("my_prefs", MainActivity.MODE_PRIVATE);
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
                        Toast.makeText(getActivity().getApplicationContext(),"Register Successful",Toast.LENGTH_LONG).show();
                        loginHandler();
                        Intent intent;
                        if(UserType.equals("STUDENT")) {
                            intent = new Intent(getActivity(), ListViewActivity.class);
                        } else {
                            intent = new Intent(getActivity(), TutorEditInformation.class);
                        }
                        startActivity(intent);
                    },error->{
                Log.d("API_GET", error.toString());
            });
            queue.add(request);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}