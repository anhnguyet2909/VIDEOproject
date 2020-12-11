package com.example.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {
    List<HotVideos> list;

    public AdapterHistory(List<HotVideos> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_history, parent, false);

        AdapterHistory.ViewHolder viewHolder = new AdapterHistory.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {
        final HotVideos hv=list.get(position);
        Picasso.get().load(hv.getAvatar()).into(holder.imgVideoThumb);
        holder.tvVideoTitle.setText(hv.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVideoThumb;
        TextView tvVideoTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideoThumb=itemView.findViewById(R.id.imgVideoThumb);
            tvVideoTitle=itemView.findViewById(R.id.tvVideoTitle);
        }
    }
}
