package com.example.project_3_team_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TutorEditInformation extends AppCompatActivity {
    EditText editName, editPhoneNumber, editEmail, editLongitude, editLatitude, editBio;
    Spinner locationSpinner, subjectSpinner;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_edit_information);
        editName = findViewById(R.id.editName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editLongitude = findViewById(R.id.editLongitude);
        editLatitude = findViewById(R.id.editLatitude);
        editBio = findViewById(R.id.editBio);
        locationSpinner = findViewById(R.id.locationSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(view -> {
            Toast.makeText(this, "Saved Information", Toast.LENGTH_SHORT).show();

        });

        //get all the information and store it into a variable

        String name = editName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        String email = editEmail.getText().toString();
        String longitude = editLongitude.getText().toString();
        String latitude = editLatitude.getText().toString();
        String bio = editBio.getText().toString();
        String location = locationSpinner.getSelectedItem().toString();
        String subject = subjectSpinner.getSelectedItem().toString();


    }
}