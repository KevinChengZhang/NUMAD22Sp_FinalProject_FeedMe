package edu.neu.madcourse.numad22sp_finalproject_feedme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.neu.madcourse.numad22sp_finalproject_feedme.FriendFeed.FriendFeed;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Friends.FindFriendsActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Login.LoginActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed.MainFeed;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.MakeRecommendationActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.UserProfile;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpApiClient;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "YelpActivity";
    private YelpApiClient yelpApiClient;

    private String userEmail = "";

    private HashMap<Integer, String> priceRange = new HashMap<Integer, String>();
    List<Boolean> states = Arrays.asList(false, false, false, false);
    private String wanted_price;
    private Handler resultHandler = new Handler();
    private AlertDialog alertDialog;

    private EditText cuisine;
    private EditText location;
    private TextView result;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        setContentView(R.layout.activity_main);

        // Add all possible prices in Yelp business search
        // 1 = $, 2 = $$, 3 = $$$, 4 = $$$$
        for (int i = 1; i > 4; i ++) {
            Log.e(TAG, "Adding Prices: " + i);
            priceRange.put(i, "");
        }

        yelpApiClient = new YelpApiClient();
        initControl();
    }

    private void initControl() {
        Button price1 = findViewById(R.id.yelp_price_1);
        Button price2 = findViewById(R.id.yelp_price_2);
        Button price3 = findViewById(R.id.yelp_price_3);
        Button price4 = findViewById(R.id.yelp_price_4);
        List<Button> buttons = Arrays.asList(price1, price2, price3, price4);
        for(int i = 0; i < buttons.size(); i++) {
            if(!states.get(i)) {
                buttons.get(i).setBackgroundColor(Color.GRAY);
            }
        }

        Button search = findViewById(R.id.yelp_search);
        Button profileButton = findViewById(R.id.profileButton);
        Button friendsButton = findViewById(R.id.friendsButton);
        Button feedButton = findViewById(R.id.mainFeedButton);
        Button recButton = findViewById(R.id.makeRecButton);
        Button friendFeedButton = findViewById(R.id.friendFeedButton);
        Button loginButton = findViewById(R.id.mainLoginButton);
        Button logoutButton = findViewById(R.id.mainLogoutButton);

        price1.setOnClickListener(this);
        price2.setOnClickListener(this);
        price3.setOnClickListener(this);
        price4.setOnClickListener(this);
        search.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        friendsButton.setOnClickListener(this);
        feedButton.setOnClickListener(this);
        recButton.setOnClickListener(this);
        friendFeedButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        cuisine = findViewById(R.id.yelp_cuisine_text);
        location = findViewById(R.id.yelp_location_text);
        result = findViewById(R.id.yelp_result_text);
        progressBar = findViewById(R.id.yelp_progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yelp_price_1:
                onPriceClicked(findViewById(R.id.yelp_price_1), 1);
                break;

            case R.id.yelp_price_2:
                onPriceClicked(findViewById(R.id.yelp_price_2), 2);
                break;

            case R.id.yelp_price_3:
                onPriceClicked(findViewById(R.id.yelp_price_3), 3);
                break;

            case R.id.yelp_price_4:
                onPriceClicked(findViewById(R.id.yelp_price_4), 4);
                break;

            case R.id.yelp_search:
                String added_price;
                wanted_price = "";

                for (int i : priceRange.keySet()) {
                    added_price = (priceRange.get(i).length() == 0) ? "" : priceRange.get(i) + ",";
                    wanted_price = wanted_price.concat(added_price);

                    if (i == priceRange.keySet().size() && wanted_price.length() > 0) {
                        wanted_price = wanted_price.substring(0, wanted_price.length() - 1);
                    }
                }

                wanted_price = (wanted_price.length() > 0) ? wanted_price : "1,2,3,4";
                Log.e(TAG, "Wanted Prices : " + wanted_price);

                // make a new thread
                YelpSearch search = new YelpSearch();
                progressBar.setVisibility(v.VISIBLE);
                search.start();
                break;

            case R.id.profileButton:
                Intent profileIntent = new Intent(this, UserProfile.class);
                profileIntent.putExtra("USER_EMAIL", userEmail);
                startActivity(profileIntent);
                break;
            case R.id.friendsButton:
                // friends button shouldn't go directly to find friends, change later
                Intent friendsIntent = new Intent(this, FindFriendsActivity.class);
                startActivity(friendsIntent);
                break;
            case R.id.mainFeedButton:
                Intent mainFeedIntent = new Intent(this, MainFeed.class);
                startActivity(mainFeedIntent);
                break;
            case R.id.makeRecButton:
                Intent makeRecIntent = new Intent(this, MakeRecommendationActivity.class);
                startActivity(makeRecIntent);
                break;
            case R.id.friendFeedButton:
                Intent makeFriendFeedIntent = new Intent(this, FriendFeed.class);
                startActivity(makeFriendFeedIntent);
                break;
            case R.id.mainLoginButton:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.mainLogoutButton:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Signed out successfully!", Toast.LENGTH_LONG).show();
                Intent toLogin = new Intent(this, LoginActivity.class);
                startActivity(toLogin);
                break;


        }
    }

    private class YelpSearch extends Thread {

        @Override
        public void run() {
            String food = (cuisine.getText().toString().length() == 0) ? "food" : cuisine.getText().toString();
            String loc = (location.getText().toString().length() == 0) ? "Boston" : location.getText().toString();
            yelpApiClient = new YelpApiClient();
            List<YelpBusiness> bus = yelpApiClient.getBusinesses(food, loc, wanted_price);

            // In this basic yelp api implementation, yelp will return the same JSON every time
            // if the parameters aren't changed.
            Log.e(TAG, "SIZE OF BUS for RANDO:" + bus.size());
            if (bus.size() <= 0) {
                runOnUiThread(() -> {
                    CharSequence tryAgain = "Failed to retrieve restaurants. Please try again";
                    Toast toast = Toast.makeText(getApplicationContext(), tryAgain, Toast.LENGTH_LONG);
                    runOnUiThread(() -> toast.show());
                    progressBar.setVisibility(View.INVISIBLE);
                });
            } else {
                int rng = new Random().nextInt(bus.size());

                resultHandler.post(() -> {
                    progressBar.setVisibility(View.INVISIBLE);

                    // Randomly pick a business from a returned JSON
                    String bus_name = bus.get(rng).getName();
                    result.setText("Result: " + bus_name);
                    buildFilterPopup(bus.get(rng));
                });
            }
        }
    }

    private void onPriceClicked(View view, int price) {
        boolean wasActive = states.get(price-1);

        if(wasActive) {
            view.setBackgroundColor(Color.GRAY);
            priceRange.put(price, "");
        } else {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            view.setBackgroundColor(color);
            priceRange.put(price, Integer.toString(price));
        }
        states.set(price-1, !wasActive);
    }

    private void buildFilterPopup(YelpBusiness business) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.main_feed_card, null);

        TextView view_title, view_description, view_number, view_address;
        RatingBar view_rating;
        ImageView view_image;

        view_title = contactPopupView.findViewById(R.id.cardTitle);
        view_description = contactPopupView.findViewById(R.id.cardDescription);
        view_number = contactPopupView.findViewById(R.id.cardNumber);
        view_rating = contactPopupView.findViewById(R.id.cardRating);
        view_image = contactPopupView.findViewById(R.id.cardIV);
        view_address = contactPopupView.findViewById(R.id.cardAddress);

        new DownloadImageFromInternet(view_image).execute(business.getImageUrl());

        String number = business.getPhone();
        if(number.length() >= 12) {
            view_number.setText(String.format("(%s) %s-%s", number.substring(2, 5), number.substring(5, 8),
                    number.substring(8, 12)));
        }
        view_title.setText(business.getName());
        view_rating.setRating((float) business.getRating());

        String description = business.getPrice()
                + " ★ "
                + business.getCategories().get("title")
                + " ★ "
                + String.join("/", business.getTransactions());
        view_description.setText(description);

        String address = business.getLocation().get("address1")
                + ", "
                + business.getLocation().get("city")
                + " "
                + business.getLocation().get("zip_code");
        view_address.setText(address);
        builder.setView(contactPopupView);

        alertDialog = builder.create();
        alertDialog.show();
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}