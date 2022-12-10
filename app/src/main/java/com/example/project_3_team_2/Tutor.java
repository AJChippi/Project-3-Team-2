package com.example.project_3_team_2;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Tutor {
    String id, name, subject;
    double latitude, longitude;
    double distance;

    public Tutor(String id, String name, String subject, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // TODO: equals() based on distance from phone location

    @Override
    public boolean equals(Object o) {
        Location.distanceBetween();


        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tutor tutor = (Tutor) o;
        return Double.compare(tutor.distance, distance) == 0;
    }
}
