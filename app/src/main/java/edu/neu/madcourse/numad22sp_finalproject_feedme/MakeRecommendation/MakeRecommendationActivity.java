package edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Login.RegisterUserActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed.MainFeed;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpApiClient;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.security.AccessController.getContext;

public class MakeRecommendationActivity extends AppCompatActivity {

    private EditText restaurantNameET, ratingET, commentsET;
    private Spinner recSpinner;
    private Button sendButton;
    private List<String> friendIDs = new ArrayList<>();
    private List<String> friendNames = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;
    private Handler handler = new Handler();
    String restaurantName;
    String rating;
    String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_recommendation);

        restaurantNameET = findViewById(R.id.recName);
        ratingET = findViewById(R.id.recRating);
        commentsET = findViewById(R.id.recComments);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        InitSpinner();
        sendButton = findViewById(R.id.recSubmit);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttemptRecommendation();
            }
        });
    }

    private void InitSpinner() {
        String selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recSpinner = findViewById(R.id.recSpinner);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(selfID)
                .child("Friends")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    task.getResult().getChildren().forEach(entry -> {
                        friendIDs.add(entry.getKey());
                        friendNames.add(entry.getValue(String.class));

                    });
                    ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(MakeRecommendationActivity.this, android.R.layout.simple_spinner_dropdown_item, friendNames);
                    recSpinner.setAdapter(namesAdapter);
                    recSpinner.setSelection(0);
                }
            }
        );
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    private void AttemptRecommendation() {
        restaurantName = restaurantNameET.getText().toString().trim();
        rating = ratingET.getText().toString().trim();
        comment = commentsET.getText().toString().trim();
        if(restaurantName.isEmpty()) {
            restaurantNameET.setError("Name is required!");
            restaurantNameET.requestFocus();
            return;
        }
        if(rating.isEmpty()) {
            ratingET.setError("Rating is required!");
            ratingET.requestFocus();
            return;
        }
        if(comment.isEmpty()) {
            ratingET.setError("Comments are required!");
            ratingET.requestFocus();
            return;
        }
        if(!isInt(rating) || Integer.parseInt(rating) > 5 || Integer.parseInt(rating) < 0 ) {
            ratingET.setError("Invalid Rating: choose [0:5]");
            ratingET.requestFocus();
            return;
        }

        getLocationThenAttemptSubmit();


    }

    private void verifyBusinessThenTryRecommendation(YelpBusiness business) {
        if(business == null) {
            restaurantNameET.setError("Unknown Restaurant");
            restaurantNameET.requestFocus();
            return;
        }
        String restaurantID = business.getId();
        String selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String friendID = friendIDs.get(recSpinner.getSelectedItemPosition()).trim();
        Recommendation recommendation = new Recommendation(
                restaurantID,
                Integer.parseInt(rating),
                comment,
                selfID);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(friendID)
                .child("RecommendationsReceived")
                .push()
                .setValue(recommendation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MakeRecommendationActivity.this, "Recommendation Sent!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MakeRecommendationActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(MakeRecommendationActivity.this, "Recommendation Failed! Try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
        recommendation.friendID = friendID;
        FirebaseDatabase.getInstance().getReference("Users")
                .child(selfID)
                .child("RecommendationsSent")
                .push()
                .setValue(recommendation);
    }


    @SuppressLint("MissingPermission")
    private void getLocationThenAttemptSubmit() {
        if (checkPermissions()) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);
            CancellationTokenSource cts = new CancellationTokenSource();
            Task<Location> locationTask = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.getToken());
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(@NonNull Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    new YelpSearch().start();
                }
            });
            locationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // TODO
                    Log.e("feed", "location retrieval failed");
                }
            });
        } else {
            requestPermissions();
        }
    }
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    private class YelpSearch extends Thread {
        @Override
        public void run() {
            YelpApiClient yelpApiClient = new YelpApiClient();
            YelpBusiness bus = yelpApiClient.searchBusiness(restaurantName, latitude, longitude);

            handler.post(() -> {
                verifyBusinessThenTryRecommendation(bus);
            });
        }

    }
}