package com.example.youxihouzainali.zhihu;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.provider.Contacts;
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
 * Created by youxihouzainali on 2017/11/27.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> mNewsList;
    private Context mContext;
    private String u = null;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View newsView;
        ImageView newsImage;
        TextView newsName;
        TextView newsDescription;
        Button newsLikes;
        Button newsCollection;

        public ViewHolder(View view) {
            super(view);
            newsView = view;
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            newsDescription = (TextView) view.findViewById(R.id.news_description);
            newsName = (TextView) view.findViewById(R.id.news_name);
            newsLikes = (Button) view.findViewById(R.id.likes);
            newsCollection = (Button) view.findViewById(R.id.collection);
        }
    }

    public NewsAdapter(List<News> newsList, String username) {
        mNewsList = newsList;
        u = username;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        mContext = parent.getContext();
        dbHelper = new MyDatabaseHelper(mContext, "Zhihu.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ViewHolder holder = new ViewHolder(view);
        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                News news = mNewsList.get(position);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("extra_url", "http://news-at.zhihu.com/api/3/section/"+news.getId());
                intent.putExtra("extra_data", u);
                mContext.startActivity(intent);
            }
        });
        final Button btn_likes = (Button) view.findViewById(R.id.likes);
        btn_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.newsLikes.getText().toString().equals("喜欢")){
                    ContentValues values = new ContentValues();
                    int position = holder.getAdapterPosition();
                    News news = mNewsList.get(position);
                    values.put("username", u);
                    values.put("url", "https://news-at.zhihu.com/api/3/section/" + news.getId());
                    values.put("thumbnail", news.getThumbnail());
                    values.put("name", news.getName());
                    values.put("newsid", news.getId());
                    values.put("description", news.getDescription());
                    values.put("flag", "1");
                    db.insert("Likes", null, values);
                    btn_likes.setText("取消喜欢");
                }
                else {
                    int position = holder.getAdapterPosition();
                    News news = mNewsList.get(position);
                    String uurl = "https://news-at.zhihu.com/api/3/section/" + news.getId();
                    db.delete("Likes", "username=? and url=?", new String[] {u, uurl});
                    holder.newsLikes.setText("喜欢");
                }
            }
        });

        final Button btn_collection = (Button)view.findViewById(R.id.collection);
        btn_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.newsCollection.getText().toString().equals("收藏")){
                    ContentValues values = new ContentValues();
                    int position = holder.getAdapterPosition();
                    News news = mNewsList.get(position);
                    values.put("username", u);
                    values.put("url", "https://news-at.zhihu.com/api/3/section/" +news.getId());
                    values.put("thumbnail", news.getThumbnail());
                    values.put("name", news.getName());
                    values.put("description", news.getDescription());
                    values.put("newsid", news.getId());
                    values.put("flag", "1");
                    db.insert("Collection", null, values);
                    btn_collection.setText("取消收藏");
                }
                else {
                    int position = holder.getAdapterPosition();
                    News news = mNewsList.get(position);
                    String uurl = "https://news-at.zhihu.com/api/3/section/" + news.getId();
                    db.delete("Collection", "username=? and url=?", new String[] {u, uurl});
                    holder.newsCollection.setText("收藏");
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = mNewsList.get(position);
        holder.newsName.setText(news.getName());
        holder.newsDescription.setText(news.getDescription());
        String image = news.getThumbnail();
        Glide.with(mContext).load(image).into(holder.newsImage);
        //holder.newsImage.setImageResource(news.getThumbnail());
        dbHelper = new MyDatabaseHelper(mContext, "Zhihu.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        String url1 = "https://news-at.zhihu.com/api/3/section/" + news.getId();
        int flaglikes = 0;
        Cursor cursor = db.query("Likes", null, "username=?", new String[] {u}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String temp = cursor.getString(cursor.getColumnIndex("url"));
                if(temp.equals(url1)) {
                    flaglikes = 1;
                }
            } while (cursor.moveToNext());
        }
        if(flaglikes == 1) {
            holder.newsLikes.setText("取消喜欢");
        }
        cursor.close();
        int flagcollection = 0;
        cursor = db.query("Collection", null, "username=?", new String[] {u}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String temp = cursor.getString(cursor.getColumnIndex("url"));
                if(temp.equals(url1)) {
                    flagcollection = 1;
                }
            } while (cursor.moveToNext());
        }
        if(flagcollection == 1) {
            holder.newsCollection.setText("取消收藏");
        }
        cursor.close();
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
}
