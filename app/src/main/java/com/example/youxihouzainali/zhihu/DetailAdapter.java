package com.example.youxihouzainali.zhihu;

import android.content.Context;
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

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private List<Detail> mDetailList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView detailImage;
        TextView detailTitle;
        TextView detailDate;
        TextView detailDisplay_date;

        public ViewHolder(View view) {
            super(view);
            detailImage = (ImageView) view.findViewById(R.id.detail_image);
            detailTitle = (TextView) view.findViewById(R.id.detail_title);
            detailDate = (TextView) view.findViewById(R.id.detail_date);
            detailDisplay_date = (TextView) view.findViewById(R.id.detail_display_date);
        }
    }

    public DetailAdapter(List<Detail> detailList) {
        mDetailList = detailList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_item, parent, false);
        mContext = parent.getContext();
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Detail detail = mDetailList.get(position);
        holder.detailTitle.setText(detail.getTitle());
        holder.detailDate.setText(detail.getDate());
        holder.detailDisplay_date.setText(detail.getDisplay_date());
        String image = detail.getImages();
        Glide.with(mContext).load(image).into(holder.detailImage);
        //holder.detailImage.setImageResource(detail.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mDetailList.size();
    }
}
