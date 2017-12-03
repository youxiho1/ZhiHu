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

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by youxihouzainali on 2017/12/3.
 */

public class HotAdapter extends RecyclerView.Adapter<HotAdapter.ViewHolder> {
    private List<Hot> mHotList;
    private Context mContext;
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        View hotView;
        ImageView hotImage;
        TextView hotTitle;

        public ViewHolder(View view) {
            super(view);
            hotView = view;
            hotImage = (ImageView) view.findViewById(R.id.hot_image);
            hotTitle = (TextView) view.findViewById(R.id.hot_title);
        }
    }
    
    public HotAdapter(List<Hot> hotList) {
        mHotList = hotList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hot_item, parent, false);
        mContext = parent.getContext();
        final HotAdapter.ViewHolder holder = new HotAdapter.ViewHolder(view);
        holder.hotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Hot hot = mHotList.get(position);
                Intent intent = new Intent(mContext, HotDetailActivity.class);
                intent.putExtra("extra_data", "https://hot-at.zhihu.com/api/4/hot/"+hot.getNews_id());
                mContext.startActivity(intent);
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
        //holder.hotImage.setImageResource(hot.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mHotList.size();
    }

}
