package edu.neu.madcourse.numad22sp_finalproject_feedme.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import android.os.Bundle;
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


import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class FindFriendsActivity extends AppCompatActivity {

    private ImageButton searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;

    private DatabaseReference usersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        usersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchResultList = (RecyclerView) findViewById(R.id.searchResultList);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

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
                .setQuery(usersDatabaseRef, FindFriends.class)
                .build();


        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriends model) {
                holder.setFullName(model.getFullName());
                holder.setEmail(model.getEmail());
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
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
}


