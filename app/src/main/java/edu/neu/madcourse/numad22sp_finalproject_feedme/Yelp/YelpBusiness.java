package edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class YelpBusiness {
    private double rating;
    private String price;
    private String phone;
    private String id;
    private String alias;
    private boolean isClosed;
    private HashMap<String, String> categories;
    private int reviewCount;
    private String name;
    private String url;
    private HashMap<String, String> coordinates;
    private String imageUrl;
    private HashMap<String, String> location;
    private double distance;
    private List<String> transactions;

    public YelpBusiness(JSONObject business) throws JSONException {
        try {
            rating = business.getInt("rating");
            price = business.getString("price");
            phone = business.getString("phone");
            id = business.getString("id");
            alias = business.getString("alias");
            isClosed = business.getBoolean("is_closed");
            reviewCount = business.getInt("review_count");
            name = business.getString("name");
            url = business.getString("url");
            imageUrl = business.getString("image_url");
            distance = business.getDouble("distance");

            categories = new HashMap<String, String>();
            categories.put("alias", business.getJSONObject("categories").getString("alias"));
            categories.put("title", business.getJSONObject("categories").getString("title"));

            coordinates = new HashMap<String, String>();
            categories.put("latitude", business.getJSONObject("coordinates").getString("latitude"));
            categories.put("latitude", business.getJSONObject("coordinates").getString("latitude"));

            location = new HashMap<String, String>();
            location.put("city", business.getJSONObject("location").getString("city"));
            location.put("country", business.getJSONObject("location").getString("country"));
            location.put("address2", business.getJSONObject("location").getString("address2"));
            location.put("address3", business.getJSONObject("location").getString("address3"));
            location.put("state", business.getJSONObject("location").getString("state"));
            location.put("address1", business.getJSONObject("location").getString("address1"));
            location.put("zip_code", business.getJSONObject("location").getString("zip_code"));

            // Probably an easier and more efficient way by using Jackson
            JSONArray jsonTransactions = business.getJSONArray("transactions");
            for (int i = 0; i < jsonTransactions.length(); i++) {
                transactions.add(jsonTransactions.get(i).toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public HashMap<String, String> getCategories() {
        return categories;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, String> getCoordinates() {
        return coordinates;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public HashMap<String, String> getLocation() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

    public List<String> getTransactions() {
        return transactions;
    }
}
