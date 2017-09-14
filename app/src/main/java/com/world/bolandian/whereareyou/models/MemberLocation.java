package com.world.bolandian.whereareyou.models;

/**
 * Created by Bolandian on 25/08/2017.
 */

public class MemberLocation {
    private User user;
    private double latitude;
    private double longitude;

    public MemberLocation(User user, double latitude, double longitude) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "MemberLocation{" +
                "user=" + user +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
