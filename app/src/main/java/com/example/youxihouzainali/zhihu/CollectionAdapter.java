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

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private List<Likes> mCollectionList;
    private Context mContext;
    private String u = null;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View collectionView;
        ImageView collectionImage;
        TextView collectionName;
        TextView collectionDescription;
        Button Collection;

        public ViewHolder(View view) {
            super(view);
            collectionView = view;
            collectionImage = (ImageView) view.findViewById(R.id.collection_image);
            collectionDescription = (TextView) view.findViewById(R.id.collection_description);
            collectionName = (TextView) view.findViewById(R.id.collection_name);
            Collection = (Button) view.findViewById(R.id.collection);
        }
    }
    public CollectionAdapter(List<Likes> collectionList, String username) {
        mCollectionList = collectionList;
        u = username;
    }

    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_item, parent, false);
        mContext = parent.getContext();
        dbHelper = new MyDatabaseHelper(mContext, "Zhihu.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final CollectionAdapter.ViewHolder holder = new CollectionAdapter.ViewHolder(view);
        holder.collectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Likes collection = mCollectionList.get(position);
                if(collection.getFlag().equals("1")) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("extra_url", collection.getUrl());
                    intent.putExtra("extra_data", u);
                    mContext.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(mContext, HotDetailActivity.class);
                    intent.putExtra("extra_url", collection.getUrl());
                    intent.putExtra("extra_data", u);
                    intent.putExtra("id",collection.getNewsid());
                    mContext.startActivity(intent);
                }
            }
        });
        final Button btn_collection = (Button) view.findViewById(R.id.collection);
        btn_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Likes collection = mCollectionList.get(position);
                String uurl = collection.getUrl();
                db.delete("Collection", "username=? and url=?", new String[] {u, uurl});
                Intent intent =new Intent(mContext, CollectionActivity.class);
                intent.putExtra("extra_data", u);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(CollectionAdapter.ViewHolder holder, int position) {
        Likes collection = mCollectionList.get(position);
        holder.collectionDescription.setText(collection.getDescription());
        holder.collectionName.setText(collection.getName());
        String image = collection.getThumbnail();
        Glide.with(mContext).load(image).into(holder.collectionImage);
    }

    @Override
    public int getItemCount() {
        return mCollectionList.size();
    }
}
