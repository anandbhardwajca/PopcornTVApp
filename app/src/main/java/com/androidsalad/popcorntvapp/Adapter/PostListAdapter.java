package com.androidsalad.popcorntvapp.Adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsalad.popcorntvapp.Model.AppPost;
import com.androidsalad.popcorntvapp.R;
import com.androidsalad.popcorntvapp.Util.ItemClickListener;
import com.androidsalad.popcorntvapp.Util.OnLoadMoreListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter {

    // views for checking whether loading or displaying:
    private final int VIEW_ITEM = 1;
    private final int VIEW_LOAD = 0;

    //get scroll position:
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private boolean mIsLoading = false;
    private int mItemsPerPage = 20;

    //onloadmore interface:
    private OnLoadMoreListener onLoadMoreListener;

    //list of posts:
    private List<AppPost> postList;

    //view holder
    private RecyclerView.ViewHolder mViewHolder;

    //constructor:
    public PostListAdapter(RecyclerView recyclerView) {

        //initiate post List:
        this.postList = new ArrayList<>();

        //set up scroll listener to recycler view:
        final LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItemPosition + mItemsPerPage)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    mIsLoading = true;
                }

            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position) != null ? VIEW_ITEM : VIEW_LOAD;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        ((PostViewHolder) mViewHolder).setClickListener(itemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_list_item, parent, false);
            mViewHolder = new PostViewHolder(convertView);
        } else {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progress_bar, parent, false);
            mViewHolder = new ProgressViewHolder(convertView);
        }
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).setData(postList.get(position));
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addAll(List<AppPost> newPosts) {
        int initialSize = postList.size();
        postList.addAll(newPosts);
        mIsLoading = false;
        notifyItemRangeInserted(initialSize, newPosts.size());
//        notifyDataSetChanged();

    }

    public String getLastItemId() {
        return postList.get(postList.size() - 1).getPostId();
    }

    public void setLoaded() {
        mIsLoading = false;
    }

}
