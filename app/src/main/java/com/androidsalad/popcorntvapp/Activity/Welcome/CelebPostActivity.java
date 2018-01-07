package com.androidsalad.popcorntvapp.Activity.Welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsalad.popcorntvapp.Adapter.PostListAdapter;
import com.androidsalad.popcorntvapp.Model.AppPost;
import com.androidsalad.popcorntvapp.Model.Celeb;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.Model.Post;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.androidsalad.popcorntvapp.Util.ItemClickListener;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CelebPostActivity extends AppCompatActivity implements ItemClickListener {


    //views:
    private ImageView celebImageView;
    private TextView celebNameTextView;
    private RecyclerView postRecyclerView;

    //celeb Id from Intent:
    private String celebId;

    //post list and adapter for recycler view:
    private List<AppPost> postList;
    private PostListAdapter adapter;

    //firebase database:
    private DatabaseReference mCelebDatabase, mCelebPostDatabase, mPostPhotosDatabase;

    //progress dialog:
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celeb_posts);

        //initialize Variables:
        initializeVariables();

        //download post list from firebase database:
        downloadPostListFromFirebase();

    }


    private void initializeVariables() {

        //views:
        celebNameTextView = (TextView) findViewById(R.id.celebPostActivityCelebNameTextView);
        celebImageView = (ImageView) findViewById(R.id.celebPostActivityCelebImageView);

        //get postId from Intent:
        celebId = getIntent().getStringExtra("celebId");

        //recycler view methods:
        postRecyclerView = (RecyclerView) findViewById(R.id.celebPostActivityPostRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        postRecyclerView.setLayoutManager(mLayoutManager);
        postRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        postRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //initialize post list:
        postList = new ArrayList<>();

        //firebase database:
        mCelebDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_DATABASE);
        mCelebPostDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_POSTS_DATABASE);
        mPostPhotosDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_PHOTOS_DATABASE);

        //adapter for recycler view:
        adapter = new PostListAdapter(postRecyclerView);
        postRecyclerView.setAdapter(adapter);

        //initiate item click listener:
        adapter.setClickListener(this);

        //progress dialog: show:
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void downloadPostListFromFirebase() {
        //show celeb Image View and Name:
        //get celeb profile pic from database:
        mCelebDatabase.child(celebId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                dialog.dismiss();

                celebNameTextView.setText(celeb.getCelebName());
                Glide.with(getApplicationContext()).load(celeb.getCelebFullUrl()).into(celebImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //add child event listener to post database:
        mCelebPostDatabase.child(celebId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get all the posts from post database:
                Post post = dataSnapshot.getValue(Post.class);

//                //check if post is live:
//                if (post != null) {
//
//                    //implement app post to get photos from post_photos database in additon to the post:
//                    AppPost appPost = new AppPost(post.getPostId(), post.getCelebId(), post.getCelebName(), post.getCelebThumbUrl(), post.getPostDesc(), post.getPostViews(), getPhotoList(post.getPostId()));
//
//                    //add to first position in post List:
//                    postList.add(0, appPost);
//
//                    //dismiss dialog if showing:
//                    if (dialog.isShowing()) dialog.dismiss();
//
//                    //notify the adapter and update post List:
//                    adapter.updatePostList(postList);
//                }

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

    //get photo list for each post from post_photos firebase database:
    public List<String> getPhotoList(String postId) {

        //create list of photos to return to post:
        final List<String> photoList = new ArrayList<>();

        //add child event listener to post_photos database to get list of photos:
        mPostPhotosDatabase.child(postId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //create photos from database:
                Photo photo = dataSnapshot.getValue(Photo.class);

                //get list of thumb urls and add to photo List:
                photoList.add(photo.getPhotoThumbUrl());

                //update the adapter about change:
                adapter.notifyDataSetChanged();
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

        return photoList;
    }

    //method for implementing on recycler view click listener:
    @Override
    public void onClick(View view, int position) {
        startPostDetailActivity(position);
    }

    public void startPostDetailActivity(int position) {

        //get post from postList which is clicked:
        final AppPost post = postList.get(position);

        //start activity and pass the post id:
        Intent intent = new Intent(CelebPostActivity.this, PostDetailActivity.class);
        intent.putExtra("postId", post.getPostId());

        //start activity
        startActivity(intent);
    }


}
