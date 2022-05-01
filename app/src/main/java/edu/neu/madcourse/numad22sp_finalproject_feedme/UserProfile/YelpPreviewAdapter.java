package edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed.FeedAdapter;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class YelpPreviewAdapter extends RecyclerView.Adapter<YelpPreviewAdapter.MyViewHolder> {
    private List<YelpBusinessPreview> busList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.bus_name);
            image = itemView.findViewById(R.id.bus_image);
        }
    }

    public YelpPreviewAdapter(List<YelpBusinessPreview> busList) {
        this.busList = busList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_preview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        YelpBusinessPreview bus = busList.get(position);
        holder.name.setText(bus.getName());
        new FeedAdapter.DownloadImageFromInternet(holder.image).execute(bus.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
