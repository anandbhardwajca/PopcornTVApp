package com.androidsalad.popcorntvapp.Activity.Welcome;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.androidsalad.popcorntvapp.Activity.NewCeleb.NewCelebActivity;
import com.androidsalad.popcorntvapp.Activity.NewPost.AddPostImageActivity;
import com.androidsalad.popcorntvapp.Activity.NewPost.NewPostActivity;
import com.androidsalad.popcorntvapp.Adapter.PostListAdapter;
import com.androidsalad.popcorntvapp.Model.AppPost;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.Model.Post;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.androidsalad.popcorntvapp.Util.InternetUtil;
import com.androidsalad.popcorntvapp.Util.ItemClickListener;
import com.androidsalad.popcorntvapp.Util.OnLoadMoreListener;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity implements ItemClickListener {

    private static final String TAG = "WelcomeActivity";

    //views
    private RecyclerView recyclerView;

    //post list and adapter for recycler view:
    private List<AppPost> postList;
    private PostListAdapter mAdapter;

    //firebase database:
    private DatabaseReference mPostDatabase, mPostPhotosDatabase;

    //progress dialog:
    private ProgressDialog dialog;

    //items per page:
    private int mPostPerPage = 20;

    //handler:
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //set up toolbar and fab for adding celeb and post:
        setUpToolbarAndFAB();

        //initialize Variables:
        initializeVariables();

        //check Internet Connection:
        if (InternetUtil.isNetworkAvailable(this)) {

            //download post list from firebase database:
            loadDataFromFirebase(null);
        } else {

            showAlertDialog();
        }

        //set up on load more listener:
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                //add null first, so the adapter will check view_type and show progress bar at bottom:
                postList.add(null);
                mAdapter.notifyItemInserted(postList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //remove progress item:
                        postList.remove(postList.size() - 1);
                        mAdapter.notifyItemRemoved(postList.size());

                        //add Items one by one:
                        String nodeId = mAdapter.getLastItemId();
                        loadDataFromFirebase(nodeId);

                    }
                }, Constants.SPLASH_DISPLAY_LENGTH);


            }
        });
    }

    private void setUpToolbarAndFAB() {

        //initialize and set up toolbar:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up fab buttons for adding new celeb and post:
        FloatingActionButton addCelebFAB = (FloatingActionButton) findViewById(R.id.addCelebFAB);
        addCelebFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, NewCelebActivity.class));
            }
        });

        FloatingActionButton addPostFAB = (FloatingActionButton) findViewById(R.id.addPostFAB);
        addPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, NewPostActivity.class));
            }
        });


        FloatingActionButton addImageFAB = (FloatingActionButton) findViewById(R.id.addImageFAB);
        addImageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, AddPostImageActivity.class));
            }
        });
    }


    private void initializeVariables() {

        //recycler view methods:
        recyclerView = (RecyclerView) findViewById(R.id.welcomeActivityPostsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //initialize post list:
        postList = new ArrayList<>();

        //firebase database:
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_DATABASE);
        mPostPhotosDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_PHOTOS_DATABASE);

        //adapter for recycler view:
        mAdapter = new PostListAdapter(recyclerView);
        recyclerView.setAdapter(mAdapter);

//        //initiate item click listener:
//        mAdapter.setClickListener(this);

        //progress dialog: show:
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //handler
        handler = new Handler();
    }


    private void loadDataFromFirebase(String nodeId) {

        //create firebase query:
        Query query;

        //check if node passed:
        if (nodeId == null) {
            query = mPostDatabase.orderByKey()
                    .limitToLast(mPostPerPage);
        } else {

            query = mPostDatabase.orderByKey()
                    .endAt(nodeId)
                    .limitToLast(mPostPerPage);
        }

        //run query:
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //initiate new post List:
                List<AppPost> newPosts = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Post post = postSnapshot.getValue(Post.class);
                    //check if post is live:
                    if (post != null) {
                        //implement app post to get photos from post_photos database in additon to the post:
                        AppPost appPost = new AppPost(post.getPostId(), post.getCelebId(), post.getCelebName(), post.getCelebThumbUrl(), post.getPostDesc(), post.getPostViews(), getPhotoList(post.getPostId()));

                        //add to new post List:
                        newPosts.add(appPost);

                        Log.d(TAG, "onPostAdded: " + appPost.getPostDesc());
                    }


                }
                if (dialog.isShowing()) dialog.dismiss();

                Collections.reverse(newPosts);
                mAdapter.addAll(newPosts);
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
                mAdapter.notifyDataSetChanged();
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

        //check what is clicked
        switch (view.getId()) {
            case R.id.singleListItemImageContainer:

                startPostDetailActivity(position);
                break;

            case R.id.singleListItemCelebNameTextView:

                startCelebPostActivity(position);
                break;

            default:
                startPostDetailActivity(position);
                break;
        }


    }

    public void startPostDetailActivity(int position) {

        //get post from postList which is clicked:
        final AppPost post = postList.get(position);

        //start activity and pass the post id:
        Intent intent = new Intent(WelcomeActivity.this, PostDetailActivity.class);
        intent.putExtra("postId", post.getPostId());

        //start activity
        startActivity(intent);
    }

    public void startCelebPostActivity(int position) {

        //get post from postList which is clicked:
        final AppPost post = postList.get(position);

        //start activity and pass the celeb id:
        Intent intent = new Intent(WelcomeActivity.this, CelebPostActivity.class);
        intent.putExtra("celebId", post.getCelebId());

        //start activity
        startActivity(intent);

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                WelcomeActivity.this);
        builder.setMessage("Internet Connection Required")
                .setCancelable(false)
                .setPositiveButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {

                                // Restart the activity
                                Intent intent = new Intent(
                                        WelcomeActivity.this,
                                        WelcomeActivity.class);
                                finish();
                                startActivity(intent);

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }


}