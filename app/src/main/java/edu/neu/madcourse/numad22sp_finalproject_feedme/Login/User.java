package edu.neu.madcourse.numad22sp_finalproject_feedme.Login;

import java.util.ArrayList;

import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.YelpBusinessPreview;

public class User {
    public String fullName, email;
    public ArrayList<YelpBusinessPreview> favorites;

    public User() {
        this.favorites = new ArrayList<>();
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
        this.favorites = new ArrayList<>();
        favorites.add(new YelpBusinessPreview());
    }

    public User(String fullName, String email, ArrayList<YelpBusinessPreview> favorites) {
        this.fullName = fullName;
        this.email = email;
        this.favorites = favorites;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFavorites(ArrayList<YelpBusinessPreview> favorites) {
        this.favorites = favorites;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ArrayList<YelpBusinessPreview> getFavorites() {
        return favorites;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}
