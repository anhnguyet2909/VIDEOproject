package com.example.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.ViewHolder>{

    List<Categories> list;
    onCategoriesClick onCategoriesClick;

    public AdapterCategories(List<Categories> list) {
        this.list = list;
    }

    public void setOnCategoriesClick(onCategoriesClick onCategoriesClick){
        this.onCategoriesClick=onCategoriesClick;
    }

    @NonNull
    @Override
    public AdapterCategories.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_categories, parent, false);

        AdapterCategories.ViewHolder viewHolder = new AdapterCategories.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCategories.ViewHolder holder, int position) {
        final Categories cate=list.get(position);
        Picasso.get().load(cate.getThumb()).fit().into(holder.imgThumb);
        holder.tvThumb.setText(cate.getTitle());
        holder.rlCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoriesClick.onImageClick(cate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvThumb;
        RelativeLayout rlCate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlCate=itemView.findViewById(R.id.rlCate);
            imgThumb=itemView.findViewById(R.id.imgThumb);
            tvThumb=itemView.findViewById(R.id.tvThumb);
        }
    }
}
