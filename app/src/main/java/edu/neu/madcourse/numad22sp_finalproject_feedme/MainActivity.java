package edu.neu.madcourse.numad22sp_finalproject_feedme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.UserProfile;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpApiClient;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "YelpActivity";
    private YelpApiClient yelpApiClient;

    private String userEmail = "";

    private HashMap<Integer, String> priceRange = new HashMap<Integer, String>();
    private String wanted_price;
    private Handler resultHandler = new Handler();

    private EditText cuisine;
    private EditText location;
    private TextView result;
    private ProgressBar progressBar;
    private Button profileButton;


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
        CheckBox price1 = findViewById(R.id.yelp_price_1);
        CheckBox price2 = findViewById(R.id.yelp_price_2);
        CheckBox price3 = findViewById(R.id.yelp_price_3);
        CheckBox price4 = findViewById(R.id.yelp_price_4);
        Button search = findViewById(R.id.yelp_search);
        Button profileButton = findViewById(R.id.profileButton);

        price1.setOnClickListener(this);
        price2.setOnClickListener(this);
        price3.setOnClickListener(this);
        price4.setOnClickListener(this);
        search.setOnClickListener(this);
        profileButton.setOnClickListener(this);

        cuisine = findViewById(R.id.yelp_cuisine_text);
        location = findViewById(R.id.yelp_location_text);
        result = findViewById(R.id.yelp_result_text);
        progressBar = findViewById(R.id.yelp_progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yelp_price_1:
                onCheckboxClicked(findViewById(R.id.yelp_price_1), 1);
                break;

            case R.id.yelp_price_2:
                onCheckboxClicked(findViewById(R.id.yelp_price_2), 2);
                break;

            case R.id.yelp_price_3:
                onCheckboxClicked(findViewById(R.id.yelp_price_3), 3);
                break;

            case R.id.yelp_price_4:
                onCheckboxClicked(findViewById(R.id.yelp_price_4), 4);
                break;

            case R.id.yelp_search:
                String added_price;
                wanted_price = "";

                for (int i : priceRange.keySet()) {
                    added_price = (priceRange.get(i).length() == 0) ? "" : priceRange.get(i) + ",";
                    wanted_price = wanted_price.concat(added_price);
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
        }
    }

    private class YelpSearch extends Thread {

        @Override
        public void run() {
            List<YelpBusiness> bus = yelpApiClient.getBusinesses(cuisine.getText().toString(), location.getText().toString(), wanted_price);

            // In this basic yelp api implementation, yelp will return the same JSON every time
            // if the parameters aren't changed.
            int rng = new Random().nextInt(bus.size());

            resultHandler.post(() -> {
                progressBar.setVisibility(View.INVISIBLE);

                // Randomly pick a business from a returned JSON
                String bus_name = bus.get(rng).getName();
                result.setText("Result: " + bus_name);
            });
        }
    }

    private void onCheckboxClicked(View view, int price) {
        boolean checked = ((CheckBox) view).isChecked();
        String new_price = (checked) ? Integer.toString(price) : "";
        priceRange.put(price, new_price);
    }
}