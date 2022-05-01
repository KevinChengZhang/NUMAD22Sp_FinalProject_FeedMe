package edu.neu.madcourse.numad22sp_finalproject_feedme.FriendFeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numad22sp_finalproject_feedme.Login.User;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed.FeedAdapter;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.Recommendation;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

public class FriendFeedRVAdapter extends RecyclerView.Adapter<FriendFeedRVAdapter.MyViewHolder> {
    private List<Recommendation> receivedRecommendation = new ArrayList<>();
    private HashMap<String, YelpBusiness> busList = new HashMap<>();

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        TextView cardDescription;
        TextView cardAddress;
        RatingBar cardRating;
        ImageView cardIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardFriendTitle);
            cardDescription = itemView.findViewById(R.id.cardFriendDescription);
            cardAddress = itemView.findViewById(R.id.cardFriendAddress);
            cardRating = itemView.findViewById(R.id.cardFriendRating);
            cardIV = itemView.findViewById(R.id.cardFriendIV);
        }
    }

    public FriendFeedRVAdapter (List<Recommendation> recList, HashMap<String, YelpBusiness> busList) {
        this.receivedRecommendation = recList;
        this.busList = busList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_recommendation_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recommendation rec = receivedRecommendation.get(position);
        YelpBusiness business = busList.get(rec.getRestaurantID());

        DatabaseReference friendDb = FirebaseDatabase.getInstance().getReference("Users")
                .child(rec.getFriendID());

        friendDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String friendName = snapshot.getValue(User.class).getFullName();
                String titleText = friendName + " recommends " + business.getName();
                holder.cardTitle.setText(titleText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String description = rec.getComments();
        holder.cardDescription.setText(description);

        new FeedAdapter.DownloadImageFromInternet(holder.cardIV).execute(business.getImageUrl());

        HashMap<String, String> location = business.getLocation();
        String address = "";
        if(business.getLocation() != null) {
            address = business.getLocation().get("address1")
                    + ", "
                    + business.getLocation().get("city")
                    + " "
                    + business.getLocation().get("zip_code");
        }
        holder.cardAddress.setText(address);

        holder.cardRating.setRating(rec.getRating());
    }

    @Override
    public int getItemCount() {
        return receivedRecommendation.size();
    }
}
