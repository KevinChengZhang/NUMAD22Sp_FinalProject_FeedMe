package edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile;

public class YelpBusinessPreview {
    private String name, id, imageUrl;
    public YelpBusinessPreview() {

    }
    public YelpBusinessPreview(String name, String id, String imageUrl){
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
}
