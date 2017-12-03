package com.example.youxihouzainali.zhihu;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        View newsView;
        ImageView newsImage;
        TextView newsName;
        TextView newsDescription;

        public ViewHolder(View view) {
            super(view);
            newsView = view;
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            newsDescription = (TextView) view.findViewById(R.id.news_description);
            newsName = (TextView) view.findViewById(R.id.news_name);
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
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
}
