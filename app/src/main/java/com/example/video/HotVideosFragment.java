package com.example.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video.databinding.FragmentHotvideosBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class HotVideosFragment extends Fragment {
    FragmentHotvideosBinding binding;

    AdapterPhoto adapterPhoto;
    List<Image> imageList;
    Timer timer;
    List<HotVideos> list = new ArrayList<>();
    AdapterHotVideos adapterHotVideos;

    public static HotVideosFragment newInstance() {

        Bundle args = new Bundle();

        HotVideosFragment fragment = new HotVideosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hotvideos, container, false);
        imageList = getListPhoto();
        adapterPhoto = new AdapterPhoto(getContext(), imageList);
        binding.viewPhoto.setAdapter(adapterPhoto);
        binding.circleIndicator.setViewPager(binding.viewPhoto);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        adapterPhoto.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());
        autoSlide();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://demo3134737.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetHotVideoAPI getHotVideoAPI = retrofit.create(GetHotVideoAPI.class);
        Call<List<HotVideos>> call = getHotVideoAPI.getHotVideo();
        call.enqueue(new Callback<List<HotVideos>>() {
            @Override
            public void onResponse(Call<List<HotVideos>> call, Response<List<HotVideos>> response) {
                if (!response.isSuccessful()) {
                    binding.load.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), response.code() + "", Toast.LENGTH_LONG).show();
                    return;
                }
                binding.load.setVisibility(View.INVISIBLE);
                list=response.body();
                adapterHotVideos=new AdapterHotVideos(list);
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                binding.rvHotVideos.setLayoutManager(layoutManager);
                binding.rvHotVideos.setAdapter(adapterHotVideos);
                adapterHotVideos.setOnItemClick(new onItemClick() {
                    @Override
                    public void onImageViewClick(HotVideos videos) {

                        Intent intent=new Intent(getContext(), ShowVideoActivity.class);
                        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data1", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("link", videos.getFile_mp4());
                        editor.putString("name", videos.getTitle());
                        editor.putString("avt", videos.getAvatar());
                        editor.putInt("id", videos.getId());
                        editor.putInt("flag", 0);
                        editor.commit();
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<HotVideos>> call, Throwable t) {
                binding.load.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return binding.getRoot();
    }

    private List<Image> getListPhoto() {
        List<Image> list = new ArrayList<>();

        list.add(new Image(R.drawable.onepiece));
        list.add(new Image(R.drawable.it2_banner));
        list.add(new Image(R.drawable.vdlh));
        list.add(new Image(R.drawable.casau));
        list.add(new Image(R.drawable.conan_banner));

        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void autoSlide() {
        if (imageList == null || imageList.isEmpty() || binding.viewPhoto == null) {
            return;
        }
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = binding.viewPhoto.getCurrentItem();
                        int totalItem = imageList.size() - 1;
                        if (currentItem < totalItem) {
                            currentItem++;
                            binding.viewPhoto.setCurrentItem(currentItem);
                        } else {
                            binding.viewPhoto.setCurrentItem(0);
                        }
                    }
                });
            }
        }, 500, 3000);

    }

}
