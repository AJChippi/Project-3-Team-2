package com.example.project_3_team_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    Button btnSignUp;
    Button btnInfo;
    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    private String BASE_URL = "http://192.168.1.174:3000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnInfo = findViewById(R.id.btnInfo);

        btnInfo.setOnClickListener(View -> {
            Intent intent = new Intent(this, GoogleMaps.class);
            startActivity(intent);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
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

        btnLogin.setOnClickListener(view1 -> {
            HashMap<String, String> map = new HashMap<>();

            map.put("email", editEmail.getText().toString());
            map.put("password", editPassword.getText().toString());

            Call<LoginResult> call = retrofitInterface.executeLogin(map);
            call.enqueue(new Callback<LoginResult>() {
                @Override
                public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                    if(response.code() == 200){
                        LoginResult result = response.body();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                        builder1.setTitle(result.getName());
                        builder1.setMessage(result.getEmail());

                        builder1.show();

                    } else if(response.code() == 404){
                        Toast.makeText(MainActivity.this, "Wrong Credentials",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResult> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        });

    }

    private void signupHandler() {
        View view2 = getLayoutInflater().inflate(R.layout.signup_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this, R.style.signup_layout);
        builder.setView(view2).show();

        Button btnSignup = view2.findViewById(R.id.btnNewSignUp);
        EditText editName = view2.findViewById(R.id.etEntName);
        EditText editEmail = view2.findViewById(R.id.etEntNewEmail);
        EditText editPassword = view2.findViewById(R.id.etEntNewPassword);

        btnSignup.setOnClickListener(view1 -> {
            HashMap<String, String> map = new HashMap<>();

            map.put("name", editName.getText().toString());
            map.put("email",editEmail.getText().toString());
            map.put("password",editPassword.getText().toString());

            Call<Void> call = retrofitInterface.executeSignUp(map);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 200){
                        Toast.makeText(MainActivity.this, "Signed Up Successfully",Toast.LENGTH_SHORT).show();
                    } else if(response.code() == 400){
                        Toast.makeText(MainActivity.this, "Already Registered",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}