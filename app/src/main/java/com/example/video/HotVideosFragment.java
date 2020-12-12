package com.example.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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


import com.example.video.databinding.FragmentHotvideosBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class HotVideosFragment extends Fragment {
    FragmentHotvideosBinding binding;

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
        new GetData().execute();

        return binding.getRoot();
    }

}
