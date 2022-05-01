package edu.neu.madcourse.numad22sp_finalproject_feedme.FriendFeed;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.Recommendation;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpApiClient;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

public class FriendFeed extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FriendFeedRVAdapter adapter;
    private Handler handler = new Handler();

    private TextView cardTitle;
    private TextView cardDescription;
    private TextView cardAddress;
    private RatingBar cardRating;
    private ImageView cardIV;

    private List<Recommendation> receivedRecommendations = new ArrayList<Recommendation>();
    private HashMap<String, YelpBusiness> busList = new HashMap<>();

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("RecommendationsReceived");

    private static final String FRIEND_FEED_KEY = "FRIEND_FEED_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_feed);

        recyclerView = findViewById(R.id.friendfeedrecycler);
        LinearLayoutManager recyclerLayout = new LinearLayoutManager(recyclerView.getContext());
        recyclerLayout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recyclerLayout);

        adapter = new FriendFeedRVAdapter(receivedRecommendations, busList);
        recyclerView.setAdapter(adapter);

        fetchReceivedRecommendations(savedInstanceState);
    }

    private class YelpSearchById extends Thread {
        private List<String> ids;
        private HashMap<String, YelpBusiness> busList = new HashMap<>();

        YelpSearchById(List<String> ids) {
            this.ids = ids;
        }

        @Override
        public void run() {
            YelpApiClient yelpApiClient = new YelpApiClient();
            for(String id : ids) {
                YelpBusiness business = yelpApiClient.getBusinessById(id);
                busList.put(id, business);
            }
            handler.post(() -> {
                populateBusiness(busList);
            });
        }
    }

    private void populateBusiness(HashMap<String, YelpBusiness> list) {
        busList.putAll(list);
        adapter.notifyDataSetChanged();
    }

    private void fetchReceivedRecommendations(Bundle savedInstanceState) {
        // fetch friend's recommendations
        if(savedInstanceState != null && savedInstanceState.containsKey(FRIEND_FEED_KEY)) {
            // TODO: used saved data (orientation)
        } else {
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                        snapshot.getChildren().forEach(r -> {
                            receivedRecommendations.add(r.getValue(Recommendation.class));

                        });
                        List<String> restaurantIds = new ArrayList<>();
                        for(Recommendation rec : receivedRecommendations) {
                            restaurantIds.add(rec.getRestaurantID());
                        }
                        new YelpSearchById(restaurantIds).start();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}