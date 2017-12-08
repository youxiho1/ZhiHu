package com.example.youxihouzainali.zhihu;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by youxihouzainali on 2017/12/3.
 */

public class HotAdapter extends RecyclerView.Adapter<HotAdapter.ViewHolder> {
    private List<Hot> mHotList;
    private Context mContext;
    private String Username = null;
    private MyDatabaseHelper dbHelper;

    
    static class ViewHolder extends RecyclerView.ViewHolder {
        View hotView;
        ImageView hotImage;
        TextView hotTitle;
        Button hotLikes;
        Button hotCollection;

        public ViewHolder(View view) {
            super(view);
            hotView = view;
            hotImage = (ImageView) view.findViewById(R.id.hot_image);
            hotTitle = (TextView) view.findViewById(R.id.hot_title);
            hotLikes = (Button) view.findViewById(R.id.likes);
            hotCollection = (Button) view.findViewById(R.id.collection);
        }
    }
    
    public HotAdapter(List<Hot> hotList, String username) {
        mHotList = hotList;
        Username = username;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hot_item, parent, false);
        mContext = parent.getContext();
        final HotAdapter.ViewHolder holder = new HotAdapter.ViewHolder(view);
        dbHelper = new MyDatabaseHelper(mContext, "Zhihu.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        holder.hotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Hot hot = mHotList.get(position);
                Intent intent = new Intent(mContext, HotDetailActivity.class);
                intent.putExtra("extra_data", Username);
                intent.putExtra("id", hot.getNews_id());
                intent.putExtra("extra_url", "https://news-at.zhihu.com/api/4/news/"+hot.getNews_id());
                //intent.putExtra("extra_url", "https://news-at.zhihu.com/api/4/news-extra/"+hot.getNews_id());
                mContext.startActivity(intent);
            }
        });
        final Button btn_likes = (Button) view.findViewById(R.id.likes);
        btn_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.hotLikes.getText().toString().equals("喜欢")){
                    ContentValues values = new ContentValues();
                    int position = holder.getAdapterPosition();
                    Hot hot = mHotList.get(position);
                    values.put("username", Username);
                    values.put("url", "https://news-at.zhihu.com/api/4/news/" + hot.getNews_id());
                    values.put("thumbnail", hot.getThumbnail());
                    values.put("name", hot.getTitle());
                    values.put("flag", "2");
                    db.insert("Likes", null, values);
                    btn_likes.setText("取消喜欢");
                }
                else {
                    int position = holder.getAdapterPosition();
                    Hot hot = mHotList.get(position);
                    String uurl = "https://news-at.zhihu.com/api/4/news/" + hot.getNews_id();
                    db.delete("Likes", "username=? and url=?", new String[] {Username, uurl});
                    holder.hotLikes.setText("喜欢");
                }
            }
        });
        final Button btn_collection = (Button)view.findViewById(R.id.collection);
        btn_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.hotCollection.getText().toString().equals("收藏")){
                    ContentValues values = new ContentValues();
                    int position = holder.getAdapterPosition();
                    Hot hot = mHotList.get(position);
                    values.put("username", Username);
                    values.put("url", "https://news-at.zhihu.com/api/4/news/" + hot.getNews_id());
                    values.put("thumbnail", hot.getThumbnail());
                    values.put("name", hot.getTitle());
                    values.put("flag", "2");
                    db.insert("Collection", null, values);
                    btn_collection.setText("取消收藏");
                }
                else {
                    int position = holder.getAdapterPosition();
                    Hot hot = mHotList.get(position);
                    String uurl = "https://news-at.zhihu.com/api/4/news/" + hot.getNews_id();
                    db.delete("Collection", "username=? and url=?", new String[] {Username, uurl});
                    holder.hotCollection.setText("收藏");
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hot hot = mHotList.get(position);
        holder.hotTitle.setText(hot.getTitle());
        String image = hot.getThumbnail();
        Glide.with(mContext).load(image).into(holder.hotImage);
        dbHelper = new MyDatabaseHelper(mContext, "Zhihu.db", null, 1);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        String url1 = "https://news-at.zhihu.com/api/4/news/" + hot.getNews_id();
        int flaglikes = 0;
        Cursor cursor = db.query("Likes", null, "username=?", new String[] {Username}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String temp = cursor.getString(cursor.getColumnIndex("url"));
                if(temp.equals(url1)) {
                    flaglikes = 1;
                }
            } while (cursor.moveToNext());
        }
        if(flaglikes == 1) {
            holder.hotLikes.setText("取消喜欢");
        }
        cursor.close();
        int flagcollection = 0;
        cursor = db.query("Collection", null, "username=?", new String[] {Username}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String temp = cursor.getString(cursor.getColumnIndex("url"));
                if(temp.equals(url1)) {
                    flagcollection = 1;
                }
            } while (cursor.moveToNext());
        }
        if(flagcollection == 1) {
            holder.hotCollection.setText("取消收藏");
        }
        cursor.close();
    }

    @Override
    public int getItemCount() {
        return mHotList.size();
    }

}
