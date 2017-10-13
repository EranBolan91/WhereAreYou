package com.world.bolandian.whereareyou.models;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Bolandian on 01/07/2017.
 */

public class User {
    private String displayName;
    private String profileImage ="http://vvcexpl.com/wordpress/wp-content/uploads/2013/09/profile-default-male.png";
    private String uid;
    private String email;
    private String token;

    public User(){

    }

    public User(FirebaseUser user,String token) {
        this.displayName = user.getDisplayName();
        if(user.getPhotoUrl() != null){
            profileImage = user.getPhotoUrl().toString();
        }
        this.uid = user.getUid();
        this.email = user.getEmail();
        this.token = token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        if(profileImage != null)
            this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "displayName='" + displayName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
