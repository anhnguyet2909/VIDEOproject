package com.example.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.video.databinding.ActivityShowVideoBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ShowVideoActivity extends AppCompatActivity {
    ActivityShowVideoBinding binding;
    private SimpleExoPlayer player;
    private ImaAdsLoader adsLoader;
    ImageView btnFull, btnPause, btnBefore10, btnAfter10;
    boolean flag=false;
    boolean check=false;
    SQLHelperHistory sqlHelperHistory=new SQLHelperHistory(this);
    String name, videoURL, avt;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this, R.layout.activity_show_video);
        adsLoader = new ImaAdsLoader(this, Uri.parse(getString(R.string.ad_tag_url)));

        SharedPreferences sharedPreferences= getBaseContext().getSharedPreferences("data1", Context.MODE_PRIVATE);
        name=sharedPreferences.getString("name", "");
        videoURL=sharedPreferences.getString("link", "");
        avt=sharedPreferences.getString("avt", "");
        id=sharedPreferences.getInt("id", 0);

        btnFull=binding.playerView.findViewById(R.id.btnFull);
        btnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    btnFull.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    flag=false;
                }
                else {
                    btnFull.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    flag=true;
                }
            }
        });
        btnPause=binding.playerView.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(a->{
            if(check){
                player.setPlayWhenReady(true);
                check=false;
                btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
            }
            else{
                player.setPlayWhenReady(false);
                check=true;
                btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            }
        });
        binding.imgUnlike.setOnClickListener(v->{
            binding.imgUnlike.setVisibility(View.INVISIBLE);
            binding.imgLike.setVisibility(View.VISIBLE);
        });
        binding.imgLike.setOnClickListener(v->{
            binding.imgUnlike.setVisibility(View.VISIBLE);
            binding.imgLike.setVisibility(View.INVISIBLE);
        });
    }
    private void releasePlayer() {
        adsLoader.setPlayer(null);
        binding.playerView.setPlayer(null);
        player.release();
        player = null;
    }

    private void initializePlayer() {
        // Create a SimpleExoPlayer and set it as the player for content and ads.
        player = new SimpleExoPlayer.Builder(this).build();

        HotVideos v=new HotVideos(id,name,avt,videoURL);
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