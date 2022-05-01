package edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation;

public class Recommendation {
    public String restaurantID;
    public int rating;
    public String comments;
    public String friendID;

    public Recommendation() {}
    
    public Recommendation(String restaurantID, int rating, String comments, String friendID) {
        this.restaurantID = restaurantID;
        this.rating = rating;
        this.comments = comments;
        this.friendID = friendID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }
}
