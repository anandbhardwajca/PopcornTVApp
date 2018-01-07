package com.androidsalad.popcorntvapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidsalad.popcorntvapp.Model.AppPost;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.ItemClickListener;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //item click listener:
    private ItemClickListener clickListener;

    //context
    private Context context;

    //views:
    private CircleImageView celebImageView;
    private TextView celebNameTextView, postDescTextView;
    private ImageView postImageView1, postImageView2, postImageView3, postImageView4;
    private LinearLayout imageContainer1, imageContainer2, imageContainer;

    //constructor:
    public PostViewHolder(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    //initialize views:
    private void findViews(View itemView) {

        celebImageView = (CircleImageView) itemView.findViewById(R.id.singleListItemCelebProfileImageView);
        celebNameTextView = (TextView) itemView.findViewById(R.id.singleListItemCelebNameTextView);
        postDescTextView = (TextView) itemView.findViewById(R.id.singleListItemPostDescTextView);
        postImageView1 = (ImageView) itemView.findViewById(R.id.singleListItemImage1);
        postImageView2 = (ImageView) itemView.findViewById(R.id.singleListItemImage2);
        postImageView3 = (ImageView) itemView.findViewById(R.id.singleListItemImage3);
        postImageView4 = (ImageView) itemView.findViewById(R.id.singleListItemImage4);
        imageContainer = (LinearLayout) itemView.findViewById(R.id.singleListItemImageContainer);
        imageContainer1 = (LinearLayout) itemView.findViewById(R.id.singleListItemImageContainer1);
        imageContainer2 = (LinearLayout) itemView.findViewById(R.id.singleListItemImageContainer2);

        itemView.setTag(itemView);
        imageContainer.setOnClickListener(this);
        celebNameTextView.setOnClickListener(this);

        context = postImageView1.getContext();
    }


    //on click method:
    @Override
    public void onClick(View view) {

        if (clickListener != null) {
            clickListener.onClick(view, getAdapterPosition());
        }
    }

    public void setData(AppPost post) {

        postDescTextView.setText(post.getPostDesc());
        celebNameTextView.setText(post.getCelebName());

        Glide.with(context).load(post.getCelebThumbUrl()).into(celebImageView);

        if (!post.getPhotoList().isEmpty()) {

            switch (post.getPhotoList().size()) {
                case 1:
                    imageContainer2.setVisibility(View.GONE);
                    Glide.with(context).load(post.getPhotoList().get(0)).thumbnail(0.1f).into(postImageView1);
                    break;

                case 2:
                    Glide.with(context).load(post.getPhotoList().get(0)).thumbnail(0.1f).into(postImageView1);
                    Glide.with(context).load(post.getPhotoList().get(1)).thumbnail(0.1f).into(postImageView2);
                    break;

                case 3:
                    postImageView3.setVisibility(View.VISIBLE);
                    Glide.with(context).load(post.getPhotoList().get(0)).thumbnail(0.1f).into(postImageView1);
                    Glide.with(context).load(post.getPhotoList().get(1)).thumbnail(0.1f).into(postImageView2);
                    Glide.with(context).load(post.getPhotoList().get(2)).thumbnail(0.1f).into(postImageView3);
                    break;

                case 4:
                    postImageView3.setVisibility(View.VISIBLE);
                    postImageView4.setVisibility(View.VISIBLE);
                    Glide.with(context).load(post.getPhotoList().get(0)).thumbnail(0.1f).into(postImageView1);
                    Glide.with(context).load(post.getPhotoList().get(1)).thumbnail(0.1f).into(postImageView2);
                    Glide.with(context).load(post.getPhotoList().get(2)).thumbnail(0.1f).into(postImageView3);
                    Glide.with(context).load(post.getPhotoList().get(3)).thumbnail(0.1f).into(postImageView4);
                    break;

                default:
                    postImageView3.setVisibility(View.VISIBLE);
                    postImageView4.setVisibility(View.VISIBLE);
                    Glide.with(context).load(post.getPhotoList().get(0)).thumbnail(0.1f).into(postImageView1);
                    Glide.with(context).load(post.getPhotoList().get(1)).thumbnail(0.1f).into(postImageView2);
                    Glide.with(context).load(post.getPhotoList().get(2)).thumbnail(0.1f).into(postImageView3);
                    Glide.with(context).load(post.getPhotoList().get(3)).thumbnail(0.1f).into(postImageView4);
                    break;
            }
        }


    }

}
