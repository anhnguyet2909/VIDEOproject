package com.example.video;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video.databinding.ActivityShowVideoBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowVideoActivity extends AppCompatActivity {
    ActivityShowVideoBinding binding;
    private SimpleExoPlayer player;
    private ImaAdsLoader adsLoader;
    ImageView btnFull, btnPause;
    boolean flag = false;
    boolean check = false;
    SQLHelperHistory sqlHelperHistory = new SQLHelperHistory(this);
    String name, videoURL, avt;
    int id;
    int print;
    String relatedVideos = DeFile.GET_RELATED_VIDEOS_URL;
    String result = "";
    String jArray = "";
    List<HotVideos> list = new ArrayList<>();
    AdapterRelatedVideos adapterRelatedVideos;
    HotVideos video;
    RatingVideo ratingVideo;
    SQLHelperFavorite sqlHelperFavorite = new SQLHelperFavorite(this);
    SQLHelperRating sqlHelperRating = new SQLHelperRating(this);
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_video);


        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("data1", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("name", "");
        videoURL = sharedPreferences.getString("link", "");
        avt = sharedPreferences.getString("avt", "");
        id = sharedPreferences.getInt("id", 0);
        print=sharedPreferences.getInt("flag", 0);
        video = new HotVideos(id, name, avt, videoURL);

        dialog=new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen){
            @Override
            public void onBackPressed() {
                if(flag){
                    closeFullscreenDialog();
                }
                super.onBackPressed();
            }
        };

        btnFull = binding.playerView.findViewById(R.id.btnFull);
        btnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag) {
                    openFullscreenDialog();
                } else {
                    closeFullscreenDialog();
                }
            }
        });
        btnPause = binding.playerView.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(a -> {
            if (check) {
                player.setPlayWhenReady(true);
                check = false;
                btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
            } else {
                player.setPlayWhenReady(false);
                check = true;
                btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            }
        });
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://demo3134737.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetHotVideoAPI getHotVideoAPI=retrofit.create(GetHotVideoAPI.class);
        Call<List<HotVideos>> call=getHotVideoAPI.getRelatedVideo();
        call.enqueue(new Callback<List<HotVideos>>() {
            @Override
            public void onResponse(Call<List<HotVideos>> call, Response<List<HotVideos>> response) {
                binding.pbLoading.setVisibility(View.INVISIBLE);
                if(!response.isSuccessful()){
                    Toast.makeText(getBaseContext(), response.code() + "", Toast.LENGTH_LONG).show();
                    return;
                }
                list=response.body();
                adapterRelatedVideos = new AdapterRelatedVideos(list);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
                binding.rvRelativeVideos.setLayoutManager(layoutManager);
                binding.rvRelativeVideos.setAdapter(adapterRelatedVideos);
                adapterRelatedVideos.setOnItemClick(new onItemClick() {
                    @Override
                    public void onImageViewClick(HotVideos videos) {
                        id = videos.getId();
                        name = videos.getTitle();
                        avt = videos.getAvatar();
                        videoURL = videos.getFile_mp4();
                        video=new HotVideos(id, name,avt, videoURL);
                        player.setPlayWhenReady(false);
                        binding.imgUnlike.setVisibility(View.VISIBLE);
                        binding.imgLike.setVisibility(View.INVISIBLE);
                        initializePlayer();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<HotVideos>> call, Throwable t) {
                binding.pbLoading.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeFullscreenDialog() {
        btnFull.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_24));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((ViewGroup) binding.playerView.getParent()).removeView(binding.playerView);
        binding.rlVideo.addView(binding.playerView);
        dialog.dismiss();
        flag=false;
    }

    private void openFullscreenDialog() {
        btnFull.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ((ViewGroup) binding.playerView.getParent()).removeView(binding.playerView);
        dialog.addContentView(binding.playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ViewGroup.LayoutParams params= binding.playerView.findViewById(R.id.rlControl).getLayoutParams();
        params.width=ViewGroup.LayoutParams.MATCH_PARENT;
        params.height=ViewGroup.LayoutParams.MATCH_PARENT;
        binding.playerView.findViewById(R.id.rlControl).setLayoutParams(params);
        dialog.show();
        flag=true;
    }



    private void releasePlayer() {
        adsLoader.setPlayer(null);
        binding.playerView.setPlayer(null);
        player.release();
        player = null;
    }

    public void rate() {
        if(sqlHelperRating.checkRated(video.getId())){
            binding.ratingBar.setRating(sqlHelperRating.getRating(video.getId()));
        }
        RatingVideo ratingVideo = new RatingVideo(video.getId(), binding.ratingBar.getRating());
        sqlHelperRating.insert(ratingVideo);
        sqlHelperRating.update(ratingVideo.getId(), binding.ratingBar.getRating());
        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                sqlHelperRating.update(video.getId(), v);
            }
        });
    }

    public void like() {
        binding.imgUnlike.setOnClickListener(v -> {
            binding.imgUnlike.setVisibility(View.INVISIBLE);
            binding.imgLike.setVisibility(View.VISIBLE);
            sqlHelperFavorite.insertVideo(video);
            Toast.makeText(getBaseContext(), getString(R.string.add), Toast.LENGTH_LONG).show();
        });
        binding.imgLike.setOnClickListener(v -> {
            binding.imgUnlike.setVisibility(View.VISIBLE);
            binding.imgLike.setVisibility(View.INVISIBLE);
            sqlHelperFavorite.deleteVideo(video.getId());
            Toast.makeText(getBaseContext(), getString(R.string.remove), Toast.LENGTH_LONG).show();
        });
    }

    private void initializePlayer() {
        // Create a SimpleExoPlayer and set it as the player for content and ads.
        adsLoader = new ImaAdsLoader(this, Uri.parse(getString(R.string.ad_tag_url)));
        player = new SimpleExoPlayer.Builder(this).build();

        HotVideos v = new HotVideos(id, name, avt, videoURL);
        sqlHelperHistory.insertVideo(v);

        binding.tvVideoName.setText(name);
        binding.playerView.setPlayer(player);
        adsLoader.setPlayer(player);

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));

        ProgressiveMediaSource.Factory mediaSourceFactory =
                new ProgressiveMediaSource.Factory(dataSourceFactory);

        // Create the MediaSource for the content you wish to play.
        MediaSource mediaSource =
                mediaSourceFactory.createMediaSource(Uri.parse(videoURL));

        // Create the AdsMediaSource using the AdsLoader and the MediaSource.
        AdsMediaSource adsMediaSource =
                new AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, binding.playerView);

        // Prepare the content and ad to be played with the SimpleExoPlayer.
        player.prepare(adsMediaSource);

        // Set PlayWhenReady. If true, content and ads autoplay.
        player.setPlayWhenReady(true);

        if (sqlHelperFavorite.checkLike(id)) {
            binding.imgUnlike.setVisibility(View.INVISIBLE);
            binding.imgLike.setVisibility(View.VISIBLE);
        }
        like();
        rate();
    }

    @Override
    public void onStart() {
        super.onStart();
        //
        if (Util.SDK_INT > 23) {
            initializePlayer();
            if (binding.playerView != null) {
                binding.playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (binding.playerView != null) {
                binding.playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (binding.playerView != null) {
                binding.playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (binding.playerView != null) {
                binding.playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adsLoader.release();
    }
}