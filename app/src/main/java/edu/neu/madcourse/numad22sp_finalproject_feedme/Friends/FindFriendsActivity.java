package edu.neu.madcourse.numad22sp_finalproject_feedme.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;



import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class FindFriendsActivity extends AppCompatActivity {

    private ImageButton searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

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
        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter;
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            this.mView = mView;
        }
    }
}


