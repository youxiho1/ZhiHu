package com.example.youxihouzainali.zhihu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by youxihouzainali on 2017/12/7.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> mReviewList;
    private Context mContext;
    private String Username;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View reviewView;
        ImageView reviewImage;
        TextView reviewAuthor;
        TextView reviewLikes;
        TextView reviewContent;
        TextView reviewTime;
        TextView reviewId;

        public ViewHolder(View view) {
            super(view);
            reviewView = view;
            reviewImage = (ImageView) view.findViewById(R.id.review_image);
            reviewAuthor = (TextView) view.findViewById(R.id.tv_author);
            reviewLikes = (TextView) view.findViewById(R.id.tv_likes);
            reviewContent = (TextView) view.findViewById(R.id.tv_content);
            reviewTime = (TextView) view.findViewById(R.id.tv_time);
            reviewId = (TextView) view.findViewById(R.id.tv_id);
        }
    }
        public ReviewAdapter(List<Review> reviewList, String username) {
            mReviewList = reviewList;
            Username = username;
        }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        mContext = parent.getContext();
        final ReviewAdapter.ViewHolder holder = new ReviewAdapter.ViewHolder(view);
        /*holder.reviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });*/
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mReviewList.get(position);
        String temp;
        temp = "评论时间:" + review.getTime();
        holder.reviewTime.setText(temp);
        holder.reviewContent.setText(review.getContent());
        temp = "有"+review.getLikes()+"人喜欢了该答案";
        holder.reviewLikes.setText(temp);
        holder.reviewAuthor.setText(review.getAuthor());
        temp = "评论者id:" + review.getId();
        holder.reviewId.setText(temp);
        String image = review.getAvatar();
        Glide.with(mContext).load(image).into(holder.reviewImage);
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }
}
