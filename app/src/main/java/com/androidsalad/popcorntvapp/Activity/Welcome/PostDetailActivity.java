package com.androidsalad.popcorntvapp.Activity.Welcome;

import android.app.ProgressDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidsalad.popcorntvapp.Adapter.ImageAdapter;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    //views
    private ViewPager viewPager;

    //adapter for displaying images:
    private ImageAdapter imageAdapter;

    //database reference
    private DatabaseReference mPostPhotoDatabase;

    //post Id from Intent:
    private String postId;

    //list
    List<String> photoList;

    //dialog
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //initializeVariables:
        intializeVariables();

        //download list of photos from firebase:
        downloadPhotoListFromFirebase();

    }


    private void intializeVariables() {

        //get postId from Intent:
        postId = getIntent().getStringExtra("postId");

        //image adapter:
        imageAdapter = new ImageAdapter(this);

        //initialize photoList:
        photoList = new ArrayList<>();

        //views:
        viewPager = (ViewPager) findViewById(R.id.postViewPager);
        viewPager.setAdapter(imageAdapter);

        //database:
        mPostPhotoDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_PHOTOS_DATABASE);

        //progress dialog:
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //method for downloading photos from Firebase:
    private void downloadPhotoListFromFirebase() {

        mPostPhotoDatabase.child(postId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get Photo from Database:
                Photo photo = dataSnapshot.getValue(Photo.class);

                //extract image url from Photo:
                photoList.add(photo.getPhotoFullUrl());

                //dismiss dialog if running:
                if (dialog.isShowing()) dialog.dismiss();

                //update the image adapter:
                imageAdapter.updatePhotoList(photoList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
