package com.example.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.example.video.databinding.FragmentFavoriteBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    boolean check=false;
    FragmentFavoriteBinding binding;
    List<HotVideos> list=new ArrayList<>();
    AdapterFavorite adapterFavorite;
    SQLHelperFavorite sqlHelperFavorite;

    public FavoriteFragment(){}

    public static FavoriteFragment newInstance() {

        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);

        sqlHelperFavorite=new SQLHelperFavorite(getContext());

        click();

        binding.refresh.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        binding.refresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refresh.setRefreshing(false);
                        sqlHelperFavorite=new SQLHelperFavorite(getContext());
                        click();
                    }
                }, 200);
            }
        });

        return binding.getRoot();
    }

    public void click(){
        list=sqlHelperFavorite.getAllVideos();
        adapterFavorite=new AdapterFavorite(list);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rvFavorite.setLayoutManager(layoutManager);
        binding.rvFavorite.setAdapter(adapterFavorite);
        adapterFavorite.setOnItemClick(new onItemClick() {
            @Override
            public void onImageViewClick(HotVideos videos) {
                Intent intent=new Intent(getContext(), ShowVideoActivity.class);
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("link", videos.getFile_mp4());
                editor.putString("name", videos.getTitle());
                editor.putString("avt", videos.getAvatar());
                editor.putInt("id", videos.getId());
                editor.putInt("flag", 1);
                editor.commit();
                startActivity(intent);
            }
        });
    }

}
