package com.example.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.video.databinding.FragmentHotvideosBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HotVideosFragment extends Fragment {
    FragmentHotvideosBinding binding;

    AdapterPhoto adapterPhoto;
    List<Image> imageList;
    Timer timer;

    String hotVideosURL= DeFile.HOT_VIDEOS_URL;
    String result = "";
    String jArray = "";
    List<HotVideos> list=new ArrayList<>();
    AdapterHotVideos adapterHotVideos;

    class GetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(hotVideosURL);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();

                int byteCharacter;

                while ((byteCharacter = inputStream.read()) != -1) {
                    result+=(char)byteCharacter;
                }
                jArray=result;
                binding.load.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            binding.load.setVisibility(View.INVISIBLE);
            try {
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
                        editor.commit();
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static HotVideosFragment newInstance() {

        Bundle args = new Bundle();

        HotVideosFragment fragment = new HotVideosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_hotvideos, container, false);
        imageList=getListPhoto();
        adapterPhoto=new AdapterPhoto(getContext(), imageList);
        binding.viewPhoto.setAdapter(adapterPhoto);
        binding.circleIndicator.setViewPager(binding.viewPhoto);
        adapterPhoto.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());
        autoSlide();
        new GetData().execute();

        return binding.getRoot();
    }

    private List<Image> getListPhoto() {
        List<Image> list=new ArrayList<>();

        list.add(new Image(R.drawable.op1));
        list.add(new Image(R.drawable.it2));
        list.add(new Image(R.drawable.dctc));
        list.add(new Image(R.drawable.conan));
        list.add(new Image(R.drawable.trailer4));

        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    private void autoSlide(){
        if(imageList==null  || imageList.isEmpty() || binding.viewPhoto==null){
            return;
        }
        if(timer==null){
            timer=new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem=binding.viewPhoto.getCurrentItem();
                        int totalItem=imageList.size()-1;
                        if(currentItem<totalItem){
                            currentItem++;
                            binding.viewPhoto.setCurrentItem(currentItem);
                        }
                        else{
                            binding.viewPhoto.setCurrentItem(0);
                        }
                    }
                });
            }
        }, 500, 3000);

    }

}
