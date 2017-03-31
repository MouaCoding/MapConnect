package com.example.rhrn.RightHereRightNow.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.rhrn.RightHereRightNow.R;
import com.example.rhrn.RightHereRightNow.firebase_entry.FollowingUser;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Bradley Wang on 3/6/2017.
 */

public class UserMiniHeaderView extends FrameLayout {
    private ImageView miniProfilePicView;
    private TextView displayNameView;
    private TextView userHandleView;
    private ImageButton moreButton;
    private ImageButton addButton;

    private String otherUserID;
    private String curUserID;

    public UserMiniHeaderView(Context context) {
        super(context);
        createView();
    }

    public UserMiniHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public void createView() {
        inflate(getContext(), R.layout.user_mini_header_layout, this);
        miniProfilePicView = (ImageView) findViewById(R.id.mini_profile_picture);
        displayNameView = (TextView) findViewById(R.id.mini_name);
        userHandleView = (TextView) findViewById(R.id.mini_user_handle);
        moreButton = (ImageButton) findViewById(R.id.mini_profile_more_button);

        if (otherUserID != FirebaseAuth.getInstance().getCurrentUser().getUid()) {
            addButton = (ImageButton) findViewById(R.id.mini_profile_add_button);
            followButton();
        }
    }

    public void getUser(String userID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(userID);
        if (ref == null)
            return;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                displayNameView.setText(user.DisplayName);
                userHandleView.setText(user.handle);
                otherUserID = user.uid;
                curUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //Try if user has profile pic
                try {
                    //Convert the URL to aa Bitmap using function, then set the profile picture
                    miniProfilePicView.setImageBitmap(getBitmapFromURL(user.ProfilePicture));
                }catch (Exception e){}
                // eventMiniImageView.setImageBitmap(ev.image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void followButton() {
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curUserID != null && curUserID != otherUserID) {
                    FirebaseDatabase.getInstance().getReference("Following")
                            .child(curUserID).child(otherUserID).setValue(new FollowingUser());
                }

            }
        });
    }

    //stackoverflow function
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch( Exception e) {
            return null;
        }
    }

}
// TODO use params to get user data and fill fields.
