package edu.neu.madcourse.numad22sp_finalproject_feedme.Login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.Recommendation;
import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.YelpBusinessPreview;

public class User {
    public String fullName, email;
    Map<String, String> Friends;
    Map<String, Recommendation> RecommendationsSent;
    Map<String, Recommendation> RecommendationsReceived;

    public User() {
    }
    public User(String name, String email) {
        this.fullName = name;
        this.email = email;
        this.Friends = new HashMap<>();
        this.RecommendationsSent = new HashMap<>();
        this.RecommendationsReceived = new HashMap<>();
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getFriends() {
        return Friends;
    }

    public void setFriends(Map<String, String> friends) {
        Friends = friends;
    }

    public Map<String, Recommendation> getRecommendationsSent() {
        return RecommendationsSent;
    }

    public void setRecommendationsSent(Map<String, Recommendation> recommendationsSent) {
        RecommendationsSent = recommendationsSent;
    }

    public Map<String, Recommendation> getRecommendationsReceived() {
        return RecommendationsReceived;
    }

    public void setRecommendationsReceived(Map<String, Recommendation> recommendationsReceived) {
        RecommendationsReceived = recommendationsReceived;
    }
}
