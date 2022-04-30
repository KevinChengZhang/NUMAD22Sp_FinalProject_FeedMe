package edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

public class FeedAdapter extends RecyclerView.Adapter<FeedHolder> {
    private final List<YelpBusiness> businesses;
    private FeedItemListener listener;

    public FeedAdapter(List<YelpBusiness> businesses) {
        this.businesses = businesses;
    }

    public void setFeedItemListener(FeedItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_feed_card, parent, false);
        return new FeedHolder(view, this.listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedHolder holder, int position) {
        YelpBusiness business = businesses.get(position);

        new DownloadImageFromInternet(holder.image).execute(business.getImageUrl());
        holder.title.setText(business.getName());
        holder.rating.setRating((float) business.getRating());

        String description = business.getPrice()
                        + " ★ "
                        + business.getCategories().get("title")
                        + " ★ "
                        + String.join("/", business.getTransactions());
        holder.description.setText(description);

        String address = business.getLocation().get("address1")
                + ", "
                + business.getLocation().get("state")
                + " "
                + business.getLocation().get("zip_code");
        holder.address.setText(address);
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
