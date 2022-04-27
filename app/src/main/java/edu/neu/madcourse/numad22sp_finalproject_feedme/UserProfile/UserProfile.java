package edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numad22sp_finalproject_feedme.Login.User;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class UserProfile extends AppCompatActivity {
    private User userProfile;
    private List<YelpBusinessPreview> busList = new ArrayList<>();
    private YelpPreviewAdapter adapter;
    TextView fullNameTextView;
    TextView emailTextView;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private static final String PROFILE_KEY = "PROFILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RecyclerView busListRecyclerView = findViewById(R.id.recyclerView);
        fullNameTextView = findViewById(R.id.fullNameText);
        emailTextView = findViewById(R.id.emailText);

        LinearLayoutManager recyclerLayout = new LinearLayoutManager(busListRecyclerView.getContext());
        recyclerLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        busListRecyclerView.setLayoutManager(recyclerLayout);

        adapter = new YelpPreviewAdapter(busList);
        busListRecyclerView.setAdapter(adapter);

        fetchUserProfileData(savedInstanceState);
    }

    private void fetchUserProfileData(Bundle savedInstanceState) {
        // fetch user profile data
        if(savedInstanceState != null && savedInstanceState.containsKey(PROFILE_KEY)) {
            // TODO: used saved data (orientation)
        } else {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userProfile = snapshot.getValue(User.class);
                    busList.addAll(userProfile.getFavorites());
                    updateProfile();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void updateProfile() {
        adapter.notifyDataSetChanged();
        fullNameTextView.setText(userProfile.getFullName());
        emailTextView.setText(userProfile.getEmail());
    }
}