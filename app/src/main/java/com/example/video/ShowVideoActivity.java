package com.example.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class ShowVideoActivity extends AppCompatActivity {
    ActivityShowVideoBinding binding;
    private SimpleExoPlayer player;
    private ImaAdsLoader adsLoader;
    ImageView btnFull, btnPause;
    boolean flag=false;
    boolean check=false;
    SQLHelperHistory sqlHelperHistory=new SQLHelperHistory(this);
    String name, videoURL, avt;
    int id;
    String relatedVideos= DeFile.GET_RELATED_VIDEOS_URL;
    String result = "";
    String jArray = "";
    List<HotVideos> list=new ArrayList<>();
    AdapterRelatedVideos adapterRelatedVideos;
    HotVideos video;
    SQLHelperFavorite sqlHelperFavorite=new SQLHelperFavorite(this);

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
        video=new HotVideos(id, name, avt, videoURL);

        new GetData().execute();

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
    }
    private void releasePlayer() {
        adsLoader.setPlayer(null);
        binding.playerView.setPlayer(null);
        player.release();
        player = null;
    }

    public void like(){
        binding.imgUnlike.setOnClickListener(v->{
            binding.imgUnlike.setVisibility(View.INVISIBLE);
            binding.imgLike.setVisibility(View.VISIBLE);
            sqlHelperFavorite.insertVideo(video);
            Toast.makeText(getBaseContext(), getString(R.string.add), Toast.LENGTH_LONG).show();
        });
        binding.imgLike.setOnClickListener(v->{
            binding.imgUnlike.setVisibility(View.VISIBLE);
            binding.imgLike.setVisibility(View.INVISIBLE);
            sqlHelperFavorite.deleteVideo(video.getId());
            Toast.makeText(getBaseContext(), getString(R.string.remove), Toast.LENGTH_LONG).show();
        });
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
        like();
        if(sqlHelperFavorite.checkLike(v)){
            binding.imgUnlike.setVisibility(View.INVISIBLE);
            binding.imgLike.setVisibility(View.VISIBLE);
        }
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

    class GetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(relatedVideos);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();

                int byteCharacter;

                while ((byteCharacter = inputStream.read()) != -1) {
                    result+=(char)byteCharacter;
                }
                jArray=result;
                binding.pbLoading.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            binding.pbLoading.setVisibility(View.INVISIBLE);
            try{
                JSONArray jsonArray=new JSONArray(jArray);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    int id=jsonObject.getInt(DeFile.ID);
                    String title=jsonObject.getString(DeFile.TITLE);
                    String avatar=jsonObject.getString(DeFile.AVATAR);
                    String file_mp4=jsonObject.getString(DeFile.FILE_MP4);
                    HotVideos hv=new HotVideos(id,title,avatar,file_mp4);
                    list.add(hv);
                }
                adapterRelatedVideos=new AdapterRelatedVideos(list);
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
                binding.rvRelativeVideos.setLayoutManager(layoutManager);
                binding.rvRelativeVideos.setAdapter(adapterRelatedVideos);
                adapterRelatedVideos.setOnItemClick(new onItemClick() {
                    @Override
                    public void onImageViewClick(HotVideos videos) {
                        id=videos.getId();
                        name=videos.getTitle();
                        avt=videos.getAvatar();
                        videoURL=videos.getFile_mp4();
                        player.setPlayWhenReady(false);
                        binding.imgUnlike.setVisibility(View.VISIBLE);
                        binding.imgLike.setVisibility(View.INVISIBLE);
                        initializePlayer();
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}