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

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.ViewHolder> {

    List<HotVideos> list;
    onItemClick onItemClick;

    public AdapterFavorite(List<HotVideos> list) {
        this.list = list;
    }

    public void setOnItemClick(onItemClick onItemClick){
        this.onItemClick=onItemClick;
    }
    @NonNull
    @Override
    public AdapterFavorite.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_history, parent, false);

        AdapterFavorite.ViewHolder viewHolder = new AdapterFavorite.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFavorite.ViewHolder holder, int position) {
        final HotVideos hv=list.get(position);
        Picasso.get().load(hv.getAvatar()).fit().into(holder.imgVideoThumb);
        holder.tvVideoTitle.setText(hv.getTitle());
        holder.llVideos.setOnClickListener(new View.OnClickListener() {
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
        LinearLayout llVideos;
        ImageView imgVideoThumb;
        TextView tvVideoTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llVideos=itemView.findViewById(R.id.llVideos);
            imgVideoThumb=itemView.findViewById(R.id.imgVideoThumb);
            tvVideoTitle=itemView.findViewById(R.id.tvVideoTitle);
        }
    }
}
