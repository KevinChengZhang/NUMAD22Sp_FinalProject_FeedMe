package edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class FeedHolder extends  RecyclerView.ViewHolder{
    public TextView title, description, number, address;
    public RatingBar rating;
    public ImageView image;

    public FeedHolder(@NonNull View itemView, final FeedItemListener listener) {
        super(itemView);
        title = itemView.findViewById(R.id.cardTitle);
        description = itemView.findViewById(R.id.cardDescription);
        number = itemView.findViewById(R.id.cardNumber);
        rating = itemView.findViewById(R.id.cardRating);
        image = itemView.findViewById(R.id.cardIV);
        address = itemView.findViewById(R.id.cardAddress);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getLayoutPosition();
                if(position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        });
    }
}
