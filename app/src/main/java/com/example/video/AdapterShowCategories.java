package com.example.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterShowCategories extends RecyclerView.Adapter<AdapterShowCategories.ViewHolder> {

    List<HotVideos> list;
    onItemClick onItemClick;

    public AdapterShowCategories(List<HotVideos> list) {
        this.list = list;
    }

    public void setOnItemClick(onItemClick onItemClick){
        this.onItemClick=onItemClick;
    }

    @NonNull
    @Override
    public AdapterShowCategories.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_show_categories, parent, false);

        AdapterShowCategories.ViewHolder viewHolder = new AdapterShowCategories.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShowCategories.ViewHolder holder, int position) {
        final HotVideos hv=list.get(position);
        Picasso.get().load(hv.getAvatar()).into(holder.imgThumbnail);
        holder.tvTitle.setText(hv.getTitle());
        holder.llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onImageViewClick(hv);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTitle;
        LinearLayout llVideo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llVideo=itemView.findViewById(R.id.llVideo);
            imgThumbnail=itemView.findViewById(R.id.ivImgThumbnail);
            tvTitle=itemView.findViewById(R.id.tvTitle);
        }
    }
}
