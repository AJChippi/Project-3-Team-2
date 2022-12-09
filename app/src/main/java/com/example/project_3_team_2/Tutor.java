package com.example.project_3_team_2;

import com.google.android.gms.maps.model.LatLng;

public class Tutor {
    String id, name, subject;
    double latitude, longitude;

    public Tutor(String id, String name, String subject, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
