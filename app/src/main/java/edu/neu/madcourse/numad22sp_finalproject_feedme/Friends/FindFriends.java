package edu.neu.madcourse.numad22sp_finalproject_feedme.Friends;

public class FindFriends {
    public String fullName, email;

    /*
    default constructor
     */
    public FindFriends() {

    }

    public FindFriends(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
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
}
