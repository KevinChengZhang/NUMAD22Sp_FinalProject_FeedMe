package edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class YelpApiClient {
    private static final String TAG = "YelpAPIClient";

    // I personally don't mind if this is exposed, but feel free to move it to a better place
    private String apiKey = "HBD2H-YxkqKy917O4ps7Ndp5XjirUXe3MpuCjo6bmx5RLXCgGoc-NtmrRh6IUkJU95R8i-aL4Rqfbsk9GdHBoR4DwvTGBbnR72tVrdLeZtUpWGt4MuezSq0VOzUQYXYx";
    private HttpURLConnection conn;
    private List<YelpBusiness> businesses;
    private List<String> sortByOptions;

    public YelpApiClient() {
        businesses = new ArrayList<YelpBusiness>();
        sortByOptions = new ArrayList<String>();
        sortByOptions.add("best_match");
        sortByOptions.add("rating");
        sortByOptions.add("review_count");
        sortByOptions.add("distance");
    }

    /**
     * Get the first 20 businesses in Yelp Search.
     * Yelp Business Search Docs: https://www.yelp.com/developers/documentation/v3/business_search
     * @param term the search term
     * @param location the geographic area to be used when searching for businesses
     * @param price the pricing levels to filter the search result with: 1 = $, 2 = $$, 3 = $$$, 4 = $$$$.
     *              The price filter can be a list of comma delimited pricing levels. Ex. '1, 2, 3'
     * @param sortBy the preferred filter to sort businesses by
     * @return An array list of Yelp Businesses from Yelp API search result
     */
    public List<YelpBusiness> getBusinesses(String term, String location, String price, String sortBy) {
        String base = "https://api.yelp.com/v3/businesses/search?";
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();

        // if given sortBy is not a supported option, give a best match search
        if (!sortByOptions.contains(sortBy)) {
            sortBy = "best_match";
        }

        try{
            Log.e(TAG, "Running HTTP GET Request...");
            URL url = new URL(String.format(base + "term=%s&location=%s&price=%s&sort_by=%s", term, location, price, sortBy));
            Log.e(TAG, String.valueOf(url));

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5000);
//            conn.setReadTimeout(5000);
            Log.e(TAG, "status code for yelp api: "+ conn.getResponseCode());

            if (conn.getResponseCode() != 200) {
                return businesses;
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
                System.out.println("line " + line);
            }
            reader.close();

            JSONObject jObject =  new JSONObject(responseContent.toString());
            JSONArray jsonArray = jObject.getJSONArray("businesses");

            Log.e(TAG, String.valueOf(jsonArray));

            for (int i = 0; i < jsonArray.length(); i++) {
                YelpBusiness business = new YelpBusiness(jsonArray.getJSONObject(i));
                businesses.add(business);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, businesses.toString());
        return businesses;
    }

    public List<YelpBusiness> getBusinesses(String term, String location, String price) {
        return getBusinesses(term, location, price, "best_match");
    }

    /**
     * Get the first 20 businesses in Yelp Search.
     * Yelp Business Search Docs: https://www.yelp.com/developers/documentation/v3/business_search
     * @param term the search term
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     * @param price the pricing levels to filter the search result with: 1 = $, 2 = $$, 3 = $$$, 4 = $$$$.
     *              The price filter can be a list of comma delimited pricing levels. Ex. '1, 2, 3'
     * @param sortBy the preferred filter to sort businesses by
     * @return An array list of Yelp Businesses from Yelp API search result
     */
    public List<YelpBusiness> getBusinesses(String term, double latitude, double longitude, String price, String sortBy) {
        String base = "https://api.yelp.com/v3/businesses/search?";
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();

        // if given sortBy is not a supported option, give a best match search
        if (!sortByOptions.contains(sortBy)) {
            sortBy = "best_match";
        }

        try{
            Log.e(TAG, "Running HTTP GET Request...");
            URL url = new URL(String.format(base + "term=%s&latitude=%s&longitude=%s&price=%s&sort_by=%s", term, latitude, longitude, price, sortBy));
            Log.e(TAG, String.valueOf(url));

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestMethod("GET");
            Log.e(TAG, "status code for yelp api: "+ conn.getResponseCode());

            if (conn.getResponseCode() != 200) {
                return businesses;
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
                System.out.println("line " + line);
            }
            reader.close();

            JSONObject jObject =  new JSONObject(responseContent.toString());
            JSONArray jsonArray = jObject.getJSONArray("businesses");

            Log.e(TAG, String.valueOf(jsonArray));

            for (int i = 0; i < jsonArray.length(); i++) {
                YelpBusiness business = new YelpBusiness(jsonArray.getJSONObject(i));
                businesses.add(business);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, businesses.toString());
        return businesses;
    }

    public List<YelpBusiness> getBusinesses(String term, double latitude, double longitude, String price) {
        return getBusinesses(term,latitude,longitude,price,"best_match");
    }
}
