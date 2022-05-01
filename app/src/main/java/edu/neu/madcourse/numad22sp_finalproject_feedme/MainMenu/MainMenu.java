package edu.neu.madcourse.numad22sp_finalproject_feedme.MainMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.FriendFeed.FriendFeed;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Friends.FindFriendsActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.Login.LoginActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainFeed.MainFeed;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MainMenu.ui.home.HomeFragment;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.MakeRecommendationActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.Recommendation;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;
import edu.neu.madcourse.numad22sp_finalproject_feedme.UserProfile.UserProfile;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.drawerName);
        TextView email = headerView.findViewById(R.id.drawerEmail);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        ImageView profilePic = headerView.findViewById(R.id.drawerPic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getApplicationContext(), UserProfile.class);
                profileIntent.putExtra("USER_EMAIL", email.getText().toString());
                startActivity(profileIntent);
            }
        });

        String selfID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(selfID)
                .child("fullName")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DataSnapshot> task) {
                         name.setText(task.getResult().getValue(String.class));
                     }
                 }
        );


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main_menu,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main_menu,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_friends:
                // friends button shouldn't go directly to find friends, change later
                Intent friendsIntent = new Intent(this, FindFriendsActivity.class);
                startActivity(friendsIntent);
                break;
            case R.id.nav_app_recs:
                Intent mainFeedIntent = new Intent(this, MainFeed.class);
                startActivity(mainFeedIntent);
                break;
            case R.id.nav_friend_recs:
                Intent makeFriendFeedIntent = new Intent(this, FriendFeed.class);
                startActivity(makeFriendFeedIntent);
                break;
            case R.id.nav_make_rec:
                Intent makeRecIntent = new Intent(this, MakeRecommendationActivity.class);
                startActivity(makeRecIntent);
                break;
            case R.id.nav_log_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Signed out successfully!", Toast.LENGTH_LONG).show();
                Intent toLogin = new Intent(this, LoginActivity.class);
                startActivity(toLogin);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}