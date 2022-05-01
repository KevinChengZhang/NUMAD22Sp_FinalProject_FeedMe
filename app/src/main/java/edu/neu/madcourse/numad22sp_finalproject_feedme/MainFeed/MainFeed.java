package edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.UserProfile;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpApiClient;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainFeed extends AppCompatActivity {
    private static final String TAG = "MainFeed";
    private RecyclerView recyclerView;
    private List<YelpBusiness> businesses;
    private FeedAdapter feedAdapter;
    private Handler handler = new Handler();
    private RecyclerView.LayoutManager rLayoutManager;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;

    String[] sortItemsAPIName = new String[]{"best_match", "rating", "review_count"};
    private String term = "korean";
    private String location = "boston";
    private String price = "1";
    List<Boolean> states = Arrays.asList(true, false, false, false);
    private int sortInd = 0;
    private int distanceInd = 0;
    private int retrievalInd = 0;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar = findViewById(R.id.mainFeedProgressBar);
        initBusinesses();

        findViewById(R.id.filterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildFilterPopup();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkPermissions()) {
            getLocation();
            initBusinesses();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (checkPermissions()) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location == null) {
                    getNewLocation();
                } else {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            });
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void getNewLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2);
        locationRequest.setNumUpdates(1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
                getLocation();
            }
        }
    }

    private void buildFilterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainFeed.this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.filter_popup, null);

        Spinner sortBySpinner = contactPopupView.findViewById(R.id.sortBySpinner);
        String[] sortItems = new String[]{"Best Match", "Rating", "Review Count"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(contactPopupView.getContext(), android.R.layout.simple_spinner_dropdown_item, sortItems);
        sortBySpinner.setAdapter(sortAdapter);
        sortBySpinner.setSelection(sortInd);

        Spinner distanceSpinner = contactPopupView.findViewById(R.id.distanceSpinner);
        String[] distanceItems = new String[]{"1 mi", "5 mi", "10 mi", "20 mi"};
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(contactPopupView.getContext(), android.R.layout.simple_spinner_dropdown_item, distanceItems);
        distanceSpinner.setAdapter(distanceAdapter);
        distanceSpinner.setSelection(distanceInd);

        Spinner retrievalSpinner = contactPopupView.findViewById(R.id.retrievalTypeSpinner);
        String[] orderTypes = new String[]{"Delivery", "Pickup", "Dine in"};
        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<>(contactPopupView.getContext(), android.R.layout.simple_spinner_dropdown_item, orderTypes);
        retrievalSpinner.setAdapter(orderTypeAdapter);
        retrievalSpinner.setSelection(retrievalInd);

        Button buttonCost1 = contactPopupView.findViewById(R.id.filterButton1);
        Button buttonCost2 = contactPopupView.findViewById(R.id.filterButton2);
        Button buttonCost3 = contactPopupView.findViewById(R.id.filterButton3);
        Button buttonCost4 = contactPopupView.findViewById(R.id.filterButton4);
        List<Button> buttons = Arrays.asList(buttonCost1, buttonCost2, buttonCost3, buttonCost4);
        for(int i = 0; i < buttons.size(); i++) {
            if(!states.get(i)) {
                buttons.get(i).setBackgroundColor(Color.GRAY);
            }
        }

        buttonCost1.setOnClickListener(new ButtonClickListener(1, states));
        buttonCost2.setOnClickListener(new ButtonClickListener(2, states));
        buttonCost3.setOnClickListener(new ButtonClickListener(3, states));
        buttonCost4.setOnClickListener(new ButtonClickListener(4, states));
        builder.setView(contactPopupView);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                sortInd = sortBySpinner.getSelectedItemPosition();
                retrievalInd = retrievalSpinner.getSelectedItemPosition();
                distanceInd = distanceSpinner.getSelectedItemPosition();
                int milesDist = Integer.parseInt(distanceSpinner.getSelectedItem().toString().split(" ")[0]);
                String sortBy = sortItemsAPIName[sortBySpinner.getSelectedItemPosition()];
                String orderType = retrievalSpinner.getSelectedItem().toString();
                ArrayList<String> values = new ArrayList<>();
                for(int i = 0; i < states.size(); i++) {
                    if(states.get(i)) {
                        values.add(String.valueOf(i+1));
                    }
                }
                String priceConformed = String.join(",", values);
                price = priceConformed;
                progressBar.setVisibility(View.VISIBLE);
                new YelpSearch(orderType, milesDist).start();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void initBusinesses() {
        getLocation();

        if (checkPermissions()) {
            new YelpSearch().start();
        }
    }
    private void initRecyclerView(List<YelpBusiness> inBusinesses) {

        businesses = inBusinesses;
        progressBar.setVisibility(View.GONE);
        rLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.mainFeedRV);
        recyclerView.setHasFixedSize(true);

        feedAdapter = new FeedAdapter(businesses, new FeedItemListener() {
            @Override
            public void onItemClick(int position) {
                // TODO:
            }
        });

        recyclerView.setAdapter(feedAdapter);
        recyclerView.setLayoutManager(rLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
    private void refillRecyclerView(List<YelpBusiness> inBusinesses) {
        businesses.clear();
        businesses.addAll(inBusinesses);
        feedAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }
    private class YelpSearch extends Thread {
        private String retrieval;
        private int distance;

        YelpSearch() {
            retrieval = "Delivery";
            distance = 20;
        }

        YelpSearch (String retrevial, int distance) {
            this.retrieval = retrevial;
            this.distance = distance;
        }
        @Override
        public void run() {
            YelpApiClient yelpApiClient = new YelpApiClient();
            List<YelpBusiness> bus = yelpApiClient.getBusinesses(term, latitude, longitude, price, sortItemsAPIName[sortInd]);

            // In this basic yelp api implementation, yelp will return the same JSON every time
            // if the parameters aren't changed.
            Log.e(TAG, "SIZE OF BUS for RANDO:" + bus.size());
            if (bus.size() <= 0) {
                runOnUiThread(() -> {
                    CharSequence tryAgain = "Failed to retrieve restaurants. Please try again";
                    Toast toast = Toast.makeText(getApplicationContext(), tryAgain, Toast.LENGTH_LONG);
                    runOnUiThread(() -> toast.show());
                });
            } else {
                double meters = mileToMeter(distance);
                List<YelpBusiness> filtered = filter(bus, retrieval, meters);
                if (filtered.size() <= 0) {
                    runOnUiThread(() -> {
                        CharSequence tryAgain = "Failed to find restaurants that fit the filter. Please try again";
                        Toast toast = Toast.makeText(getApplicationContext(), tryAgain, Toast.LENGTH_LONG);
                        runOnUiThread(() -> toast.show());
                    });
                }

                handler.post(() -> {
                    if(recyclerView != null) {
                        refillRecyclerView(filtered);
                    } else {
                        initRecyclerView(bus);
                    }
                });
            }
        }

        private double mileToMeter(int mile) {
            return mile * 1609.34;
        }

        private List<YelpBusiness> filter(List<YelpBusiness> businesses,String orderType, double meters) {
            List<YelpBusiness> filtered = new ArrayList<YelpBusiness>();
            for (YelpBusiness business: businesses) {
                if (business.getDistance() <= meters && business.getTransactions().contains(orderType.toLowerCase())) {
                    filtered.add(business);
                }
            }

            return filtered;
        }
    }

    private class ButtonClickListener implements View.OnClickListener {

        private int value;
        private List<Boolean> states;
        private Color originalColor;

        public ButtonClickListener(int value, List<Boolean> states) {
            this.value = value;
            this.states = states;
        }

        @Override
        public void onClick(View view) {
            boolean wasActive = states.get(value-1);
            if(wasActive) {
                view.setBackgroundColor(Color.GRAY);
            } else {
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                view.setBackgroundColor(color);
            }
            states.set(value-1, !wasActive);
        }
    }
}