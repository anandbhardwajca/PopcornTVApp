package com.androidsalad.popcorntvapp.Activity.NewPost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidsalad.popcorntvapp.Activity.Welcome.WelcomeActivity;
import com.androidsalad.popcorntvapp.Model.Celeb;
import com.androidsalad.popcorntvapp.Model.Photo;
import com.androidsalad.popcorntvapp.Model.Post;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostImageActivity extends AppCompatActivity {

    private static final String TAG = "AddPostImageActivity";

    //views
    private Button saveButton;
    private ImageButton addImageButton;
    private Spinner celebSpinner, postSpinner;
    private TextView postDescTextView;

    //database storage
    private StorageReference mPhotoStorage;
    private DatabaseReference mPostDatabase, mBaseDatabase, mCelebDatabase, mPhotoDatabase, mCelebPostsDatabase, mPostPhotosDatabase;

    //select celeb spinner items:
    private List<String> celebNamesList, postDescList;
    private String celebId, postId;
    ArrayAdapter<String> celebSpinnerAdapter, postSpinnerAdapter;

    // bitmap
    private Bitmap fullSizeBitmap, thumbnailBitmap;

    //progress dialog
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_image);

        //initializeVariables:
        initializeVariables();


        //set up spinner on Item selected listeners:
        setUpSpinnerItemSelectedListeners();


        //initialize list of celebs in spinner:
        addCelebNamesToSpinner();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        //save To Firebase:
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                saveToFirebase();
            }
        });
    }

    private void initializeVariables() {

        //views
        saveButton = (Button) findViewById(R.id.addPostImageSaveButton);
        addImageButton = (ImageButton) findViewById(R.id.addPostImageButton);
        celebSpinner = (Spinner) findViewById(R.id.addPostImageCelebSpinner);
        postSpinner = (Spinner) findViewById(R.id.addPostImagePostSpinner);

        postDescTextView = (TextView) findViewById(R.id.addPostImagePostDescriptionTextView);

        //spinner adapters:
        celebNamesList = new ArrayList<>();
        celebSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, celebNamesList);
        celebSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebSpinner.setAdapter(celebSpinnerAdapter);

        postDescList = new ArrayList<>();
        postSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, postDescList);
        postSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postSpinner.setAdapter(postSpinnerAdapter);

        //initialize firebase:
        mBaseDatabase = FirebaseDatabase.getInstance().getReference();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_DATABASE);
        mPhotoDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PHOTO_DATABASE);
        mCelebDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_DATABASE);
        mCelebPostsDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CELEB_POSTS_DATABASE);
        mPostPhotosDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_POST_PHOTOS_DATABASE);
        mPhotoStorage = FirebaseStorage.getInstance().getReference().child(Constants.FIREBASE_PHOTO_DATABASE);

        //progress dialog:
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
    }


    private void setUpSpinnerItemSelectedListeners() {

        celebSpinner.post(new Runnable() {
            @Override
            public void run() {

                celebSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //get selected celeb name for saving in post:
                        String celebName = parent.getItemAtPosition(position).toString();

                        postDescList.clear();
                        retrievePostDescriptionList(celebName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        postSpinner.post(new Runnable() {
            @Override
            public void run() {
                postSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String postDesc = postDescList.get(position);

                        //set post description
                        postDescTextView.setText(postDesc);
                        //retrieve post id from description:
                        retrievePostId(postDesc);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });


    }

    private void retrievePostId(String postDesc) {

        mCelebPostsDatabase.child(celebId).orderByChild("postDesc").equalTo(postDesc).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Post post = dataSnapshot.getValue(Post.class);
                postId = post.getPostId();

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

    private void retrievePostDescriptionList(String celebName) {

        //get celeb profile pic from database:
        mCelebDatabase.orderByChild("celebName").equalTo(celebName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get selected celeb:
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                celebId = celeb.getCelebId();

                mCelebPostsDatabase.child(celebId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);

                        postDescList.add(post.getPostDesc());
                        postSpinnerAdapter.notifyDataSetChanged();
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

    private void selectImageFromGallery() {
        //start intent for selecting image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.INT_ACTION_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.INT_ACTION_PICK && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();

            //create full size and thumbnail bitmaps
            try {
                fullSizeBitmap = decodeSampledBitmapFromUri(imageUri, 1280, 1280);
                thumbnailBitmap = decodeSampledBitmapFromUri(imageUri, 128, 128);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //display image
            Glide.with(this).load(imageUri).into(addImageButton);
        }
    }

    //methods to extract bitmap from uris:
    private Bitmap decodeSampledBitmapFromUri(Uri fileUri, int reqWidth, int reqHeight) throws IOException {
        InputStream stream = new BufferedInputStream(
                getApplicationContext().getContentResolver().openInputStream(fileUri));
        stream.mark(stream.available());
        BitmapFactory.Options options = new BitmapFactory.Options();
        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        stream.reset();

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(stream, null, options);
    }

    //method to calculate in sample size for extracting bitmaps from uri:
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void addCelebNamesToSpinner() {

        mCelebDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //get celeb list from database:
                Celeb celeb = dataSnapshot.getValue(Celeb.class);

                //add to celebNames List:
                celebNamesList.add(celeb.getCelebName());

                //notify the adapter to update:
                celebSpinnerAdapter.notifyDataSetChanged();
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


    //finally save to firebase:
    private void saveToFirebase() {

        //disable save button to avoid repetition in saving
        saveButton.setEnabled(false);

        //get unique id for new photo:
        final String photoId = mPhotoDatabase.push().getKey();

        //compress bitmap for full upload
        ByteArrayOutputStream fullSizeStream = new ByteArrayOutputStream();
        fullSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fullSizeStream);

        //upload full size first to firebase storage:
        mPhotoStorage.child(postId).child(photoId).child("full").putBytes(fullSizeStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //get full size download url from firebase:
                final String fullSizeUrl = taskSnapshot.getDownloadUrl().toString();

                //compress bitmap for full upload
                ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 50, thumbnailStream);

                mPhotoStorage.child(postId).child(photoId).child("thumb").putBytes(thumbnailStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //get thumbnail download url from firebase:
                        String thumbnailUrl = taskSnapshot.getDownloadUrl().toString();

                        //create new photo:
                        Photo photo = new Photo(photoId, fullSizeUrl, thumbnailUrl);

                        //child updates to save post and photo:
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Object> photoValues = photo.toMap();

                        childUpdates.put("/photos/" + photoId, photoValues);

                        //save photo again in post_photos to retrieve post wise photos:
                        childUpdates.put("/post_photos/" + postId + "/" + photoId, photoValues);

                        //update all the children of the firebase database:
                        mBaseDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //dismiss dialog if running
                                if (dialog.isShowing()) dialog.dismiss();

                                //start welcome activity and finish:
                                startActivity(new Intent(AddPostImageActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        }); // end of update children on complete listener
                    }
                }); // end of thumbnail success listener

            }
        }); // end of full size success listener


    }

}
