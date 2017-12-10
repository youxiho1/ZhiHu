package com.example.youxihouzainali.zhihu;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by youxihouzainali on 2017/12/10.
 */

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
    private List<Likes> mLikesList;
    private Context mContext;
    private String u = null;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View likesView;
        ImageView likesImage;
        TextView likesName;
        TextView likesDescription;
        Button Likes;

        public ViewHolder(View view) {
            super(view);
            likesView = view;
            likesImage = (ImageView) view.findViewById(R.id.likes_image);
            likesDescription = (TextView) view.findViewById(R.id.likes_description);
            likesName = (TextView) view.findViewById(R.id.likes_name);
            Likes = (Button) view.findViewById(R.id.likes);
        }
    }
    public LikesAdapter(List<Likes> likesList, String username) {
        mLikesList = likesList;
        u = username;
    }

    @Override
    public LikesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.likes_item, parent, false);
        mContext = parent.getContext();
        dbHelper = new MyDatabaseHelper(mContext, "Zhihu.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final LikesAdapter.ViewHolder holder = new LikesAdapter.ViewHolder(view);
        holder.likesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Likes likes = mLikesList.get(position);
                if(likes.getFlag().equals("1")) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("extra_url", likes.getUrl());
                    intent.putExtra("extra_data", u);
                    mContext.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(mContext, HotDetailActivity.class);
                    intent.putExtra("extra_url", likes.getUrl());
                    intent.putExtra("extra_data", u);
                    intent.putExtra("id",likes.getNewsid());
                    mContext.startActivity(intent);
                }
            }
        });
        final Button btn_likes = (Button) view.findViewById(R.id.likes);
        btn_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Likes likes = mLikesList.get(position);
                    String uurl = likes.getUrl();
                    db.delete("Likes", "username=? and url=?", new String[] {u, uurl});
                    Intent intent =new Intent(mContext, LikesActivity.class);
                    intent.putExtra("extra_data", u);
                    mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(LikesAdapter.ViewHolder holder, int position) {
        Likes likes = mLikesList.get(position);
        holder.likesDescription.setText(likes.getDescription());
        holder.likesName.setText(likes.getName());
        String image = likes.getThumbnail();
        Glide.with(mContext).load(image).into(holder.likesImage);
    }

    @Override
    public int getItemCount() {
        return mLikesList.size();
    }
}
