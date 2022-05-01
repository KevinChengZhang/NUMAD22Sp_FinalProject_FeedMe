package edu.neu.madcourse.numad22sp_finalproject_feedme.Friends;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class FindFriendsActivity extends AppCompatActivity {

    private ImageButton searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;
    private FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter;

    private DatabaseReference usersDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        usersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        searchResultList = (RecyclerView) findViewById(R.id.searchResultList);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        searchForFriends("");

        searchInputText = (EditText) findViewById(R.id.searchBarInput);

        searchButton = (ImageButton) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchInput = searchInputText.getText().toString();
                searchForFriends(searchInput);
            }
        });
    }



    private void searchForFriends(String input) {

        Toast.makeText(this, "Finding friends...", Toast.LENGTH_LONG).show();

        Query searchFriendsQuery = usersDatabaseRef.orderByChild("fullName")
                .startAt(input).endAt(input + "\uf8ff"); // \uf8ff is a java unicode escape character

        FirebaseRecyclerOptions<FindFriends> options = new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(searchFriendsQuery, FindFriends.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull FindFriends model) {
                if (mUser.getUid().equals(getRef(position).getKey().toString())) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                else {
                    holder.setFullName(model.getFullName());
                    holder.setEmail(model.getEmail());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FindFriendsActivity.this, ViewFriendActivity.class);
                        intent.putExtra("userKey", getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_friends_display, parent, false);
                return new FindFriendsViewHolder(view);
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullName(String fullName) {
            TextView nameTV = (TextView) mView.findViewById(R.id.fullNameTV);
            nameTV.setText(fullName);
        }

        public void setEmail(String email) {
            TextView emailTV = (TextView) mView.findViewById(R.id.emailTV);
            emailTV.setText(email);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}


