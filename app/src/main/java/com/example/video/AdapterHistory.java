package com.example.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {
    List<HotVideos> list;
    onSelectionClick onSelectionClick;

    public AdapterHistory(List<HotVideos> list) {
        this.list = list;
    }
    public void setOnItemClick(onSelectionClick onSelectionClick){
        this.onSelectionClick=onSelectionClick;
    }

    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_favorite, parent, false);

        AdapterHistory.ViewHolder viewHolder = new AdapterHistory.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {
        final HotVideos hv=list.get(position);
        Picasso.get().load(hv.getAvatar()).fit().into(holder.imgVideoThumb);
        holder.tvVideoTitle.setText(hv.getTitle());
        holder.rvVideoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectionClick.onImageViewClick(hv);
            }
        });
        holder.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectionClick.onClearClick(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVideoThumb;
        TextView tvVideoTitle;
        RelativeLayout rvVideoThumb;
        ImageView btnClear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideoThumb=itemView.findViewById(R.id.imgVideoThumb);
            tvVideoTitle=itemView.findViewById(R.id.tvVideoTitle);
            rvVideoThumb=itemView.findViewById(R.id.rlVideoThumb);
            btnClear=itemView.findViewById(R.id.btnClear);
        }
    }
}
