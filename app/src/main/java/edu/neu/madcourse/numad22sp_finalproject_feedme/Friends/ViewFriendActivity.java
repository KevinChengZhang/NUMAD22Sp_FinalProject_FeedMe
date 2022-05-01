package edu.neu.madcourse.numad22sp_finalproject_feedme.Friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;

import edu.neu.madcourse.numad22sp_finalproject_feedme.MakeRecommendation.MakeRecommendationActivity;
import edu.neu.madcourse.numad22sp_finalproject_feedme.R;

public class ViewFriendActivity extends AppCompatActivity {

    private DatabaseReference usersDatabaseRef;
    private DatabaseReference requestRef;
    private DatabaseReference friendRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String friendFullName;
    String friendEmail;

    TextView friendFullNameTV;
    TextView friendEmailTV;

    AppCompatButton sendButton;
    AppCompatButton declineButton;

    String friendState = "it's complicated";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        final String userID = getIntent().getStringExtra("userKey"); // intent passed from FindFriendsActivity

        friendFullNameTV = findViewById(R.id.fullName);
        friendEmailTV = findViewById(R.id.email);

        sendButton = findViewById(R.id.sendRequestButton);
        declineButton = findViewById(R.id.declineRequestButton);

        usersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getUser();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(userID);
            }
        });

        userExists(userID);

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfriend(userID);
            }
        });
    }

    private void unfriend(String id) {
        if (friendState.equals("friends")) {
            friendRef.child(mUser.getUid()).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    friendRef.child(id).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ViewFriendActivity.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                            friendState = "it's complicated";
                            sendButton.setText("Send Friend Request");
                            declineButton.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
        if (friendState.equals("received request pending")) {
            HashMap map = new HashMap();
            map.put("status", "declined");
            requestRef.child(id).child(mUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Toast.makeText(ViewFriendActivity.this, "Friend Request Declined", Toast.LENGTH_SHORT).show();
                    friendState = "received request declined";
                    sendButton.setVisibility(View.GONE);
                    declineButton.setVisibility(View.GONE);
                }
            });
        }
    }

    private void getUser() {
        usersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendFullName = snapshot.child("fullName").getValue().toString();
                friendEmail = snapshot.child("email").getValue().toString();

                friendFullNameTV.setText(friendFullName);
                friendEmailTV.setText(friendEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest(String id) {
        if (friendState.equals("it's complicated")) {
            HashMap map = new HashMap();
            map.put("status", "pending");
            requestRef.child(mUser.getUid()).child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Toast.makeText(ViewFriendActivity.this, "Friend request sent!", Toast.LENGTH_SHORT).show();
                    declineButton.setVisibility(View.GONE);
                    friendState = "sent request pending";
                    sendButton.setText("Cancel Friend Request");
                }
            });
        }
        if (friendState.equals("received request pending")) {
            requestRef.child(id).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    HashMap map = new HashMap();
                    map.put("status", "friend");
                    map.put("friendFullName", friendFullName);
                    friendRef.child(mUser.getUid()).child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            friendRef.child(id).child(mUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(ViewFriendActivity.this, "Friend Added!", Toast.LENGTH_SHORT).show();
                                    friendState = "friends";
                                    sendButton.setText("Send Recommendation");
                                    declineButton.setText("Remove Friend");
                                    declineButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            });
        }
        if (friendState.equals("sent request declined") || friendState.equals("sent request pending")) {
            requestRef.child(mUser.getUid()).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(ViewFriendActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                    friendState = "it's complicated";
                    sendButton.setText("Send Friend Request");
                    declineButton.setVisibility(View.GONE);
                }
            });
        }
        if (friendState.equals("friends")) {
            HashMap map = new HashMap();
            map.put("status", "friend");
            map.put("friendFullName", friendFullName);
            friendRef.child(mUser.getUid()).child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Intent intent = new Intent(ViewFriendActivity.this, MakeRecommendationActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void userExists(String id) {

        // if friends/request accepted
        friendRef.child(mUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    friendState = "friends";
                    sendButton.setText("Send Recommendation");
                    declineButton.setText("Remove Friend");
                    declineButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendRef.child(id).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    friendState = "friends";
                    sendButton.setText("Send Recommendation");
                    declineButton.setText("Remove Friend");
                    declineButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // if user sent request to other user
        requestRef.child(mUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        friendState = "sent request pending";
                        sendButton.setText("Cancel Friend Request");
                        declineButton.setVisibility(View.GONE);
                    }
                    if (snapshot.child("status").getValue().toString().equals("declined")) {
                        friendState = "sent request declined";
                        sendButton.setText("Cancel Friend Request");
                        declineButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // if user receives a request from another user
        requestRef.child(id).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        friendState = "received request pending";
                        sendButton.setText("Accept Friend Request");
                        declineButton.setText("Decline Friend Request");
                        declineButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (friendState.equals("it's complicated")) {
            friendState = "it's complicated";
            sendButton.setText("Send Friend Request");
            declineButton.setVisibility(View.GONE);
        }
    }
}