package edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Login.RegisterUserActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_recommendation);

        restaurantNameET = findViewById(R.id.recName);
        ratingET = findViewById(R.id.recRating);
        commentsET = findViewById(R.id.recComments);

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
        String restaurantName = restaurantNameET.getText().toString().trim();
        String rating = ratingET.getText().toString().trim();
        String comment = commentsET.getText().toString().trim();
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
        // TODO yelp API get restaurantid from name
        String restaurantID = "E8RJkjfdcwgtyoPMjQ_Olg";
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
}