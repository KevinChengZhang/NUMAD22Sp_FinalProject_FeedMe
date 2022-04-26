package edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class UserProfile extends AppCompatActivity {
    private String userEmail = "";
    private UserProfile userProfile = null;
    private List<YelpBusinessPreview> busList = new ArrayList<>();
    private YelpPreviewAdapter adapter;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RecyclerView busListRecyclerView = findViewById(R.id.recyclerView);
        adapter = new YelpPreviewAdapter(busList);
        busListRecyclerView.setAdapter(adapter);
        fetchUserProfileData();
    }

    private void fetchUserProfileData() {
        // fetch user profile data

        // populate user profile
        // populate busList

        adapter.notifyDataSetChanged();
    }
}