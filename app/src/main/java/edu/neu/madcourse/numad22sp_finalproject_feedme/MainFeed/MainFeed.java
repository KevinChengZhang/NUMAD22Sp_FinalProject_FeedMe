package edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.UserProfile;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpApiClient;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Yelp.YelpBusiness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainFeed extends AppCompatActivity {
    private static final String TAG = "MainFeed";
    private RecyclerView recyclerView;
    private List<YelpBusiness> businesses;
    private FeedAdapter feedAdapter;
    private Handler handler = new Handler();
    private RecyclerView.LayoutManager rLayoutManager;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;

    String[] sortItemsAPIName = new String[]{"best_match", "rating", "review_count"};
    private String term = "korean";
    private String location = "boston";
    private String price = "1";
    List<Boolean> states = Arrays.asList(true, false, false, false);
    private int sortInd = 0;
    private int distanceInd = 0;
    private int retrievalInd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);

        progressBar = findViewById(R.id.mainFeedProgressBar);
        initBusinesses();
        findViewById(R.id.filterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              buildFilterPopup();

            }
        });
    }

    private void buildFilterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainFeed.this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.filter_popup, null);

        Spinner sortBySpinner = contactPopupView.findViewById(R.id.sortBySpinner);
        String[] sortItems = new String[]{"Best Match", "Rating", "Review Count"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(contactPopupView.getContext(), android.R.layout.simple_spinner_dropdown_item, sortItems);
        sortBySpinner.setAdapter(sortAdapter);
        sortBySpinner.setSelection(sortInd);

        Spinner distanceSpinner = contactPopupView.findViewById(R.id.distanceSpinner);
        String[] distanceItems = new String[]{"1 mi", "5 mi", "10 mi", "20 mi"};
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(contactPopupView.getContext(), android.R.layout.simple_spinner_dropdown_item, distanceItems);
        distanceSpinner.setAdapter(distanceAdapter);
        distanceSpinner.setSelection(distanceInd);

        Spinner retrievalSpinner = contactPopupView.findViewById(R.id.retrievalTypeSpinner);
        String[] orderTypes = new String[]{"Delivery", "Pickup", "Dine in"};
        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<>(contactPopupView.getContext(), android.R.layout.simple_spinner_dropdown_item, orderTypes);
        retrievalSpinner.setAdapter(orderTypeAdapter);
        retrievalSpinner.setSelection(retrievalInd);

        Button buttonCost1 = contactPopupView.findViewById(R.id.filterButton1);
        Button buttonCost2 = contactPopupView.findViewById(R.id.filterButton2);
        Button buttonCost3 = contactPopupView.findViewById(R.id.filterButton3);
        Button buttonCost4 = contactPopupView.findViewById(R.id.filterButton4);
        List<Button> buttons = Arrays.asList(buttonCost1, buttonCost2, buttonCost3, buttonCost4);
        for(int i = 0; i < buttons.size(); i++) {
            if(!states.get(i)) {
                buttons.get(i).setBackgroundColor(Color.GRAY);
            }
        }

        buttonCost1.setOnClickListener(new ButtonClickListener(1, states));
        buttonCost2.setOnClickListener(new ButtonClickListener(2, states));
        buttonCost3.setOnClickListener(new ButtonClickListener(3, states));
        buttonCost4.setOnClickListener(new ButtonClickListener(4, states));
        builder.setView(contactPopupView);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                sortInd = sortBySpinner.getSelectedItemPosition();
                retrievalInd = retrievalSpinner.getSelectedItemPosition();
                distanceInd = distanceSpinner.getSelectedItemPosition();
                int milesDist = Integer.parseInt(distanceSpinner.getSelectedItem().toString().split(" ")[0]);
                String sortBy = sortItemsAPIName[sortBySpinner.getSelectedItemPosition()];
                String orderType = retrievalSpinner.getSelectedItem().toString();
                ArrayList<String> values = new ArrayList<>();
                for(int i = 0; i < states.size(); i++) {
                    if(states.get(i)) {
                        values.add(String.valueOf(i+1));
                    }
                }
                String priceConformed = String.join(",", values);
                price = priceConformed;
                progressBar.setVisibility(View.VISIBLE);
                new YelpSearch().start();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void initBusinesses() {
        new YelpSearch().start();
    }
    private void initRecyclerView(List<YelpBusiness> inBusinesses) {

        businesses = inBusinesses;
        progressBar.setVisibility(View.GONE);
        rLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.mainFeedRV);
        recyclerView.setHasFixedSize(true);

        feedAdapter = new FeedAdapter(businesses, new FeedItemListener() {
            @Override
            public void onItemClick(int position) {
                // TODO:
            }
        });

        recyclerView.setAdapter(feedAdapter);
        recyclerView.setLayoutManager(rLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
    private void refillRecyclerView(List<YelpBusiness> inBusinesses) {
        businesses.clear();
        businesses.addAll(inBusinesses);
        feedAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }
    private class YelpSearch extends Thread {


        @Override
        public void run() {
            YelpApiClient yelpApiClient = new YelpApiClient();
            List<YelpBusiness> bus = yelpApiClient.getBusinesses(term, location, price, sortItemsAPIName[sortInd]);

            // In this basic yelp api implementation, yelp will return the same JSON every time
            // if the parameters aren't changed.
            Log.e(TAG, "SIZE OF BUS for RANDO:" + bus.size());
            if (bus.size() <= 0) {
                runOnUiThread(() -> {
                    CharSequence tryAgain = "Failed to retrieve restaurants. Please try again";
                    Toast toast = Toast.makeText(getApplicationContext(), tryAgain, Toast.LENGTH_LONG);
                    runOnUiThread(() -> toast.show());
                });
            } else {

                handler.post(() -> {
                    if(recyclerView != null) {
                        refillRecyclerView(bus);
                    } else {
                        initRecyclerView(bus);
                    }
                });
            }
        }
    }

    private class ButtonClickListener implements View.OnClickListener {

        private int value;
        private List<Boolean> states;
        private Color originalColor;

        public ButtonClickListener(int value, List<Boolean> states) {
            this.value = value;
            this.states = states;
        }

        @Override
        public void onClick(View view) {
            boolean wasActive = states.get(value-1);
            if(wasActive) {
                view.setBackgroundColor(Color.GRAY);
            } else {
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                view.setBackgroundColor(color);
            }
            states.set(value-1, !wasActive);
        }
    }
}