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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video.databinding.FragmentShowCategoriesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ShowCategoriesFragment extends Fragment {
    String getCategoriesURL;
    String result = "";
    String jArray = "";
    List<HotVideos> list=new ArrayList<>();
    AdapterHotVideos adapterHotVideos;
    FragmentShowCategoriesBinding binding;
    SQLHelperHistory sqlHelperHistory=new SQLHelperHistory(getContext());

    class GetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(getCategoriesURL);
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
                binding.rvCategoryItem.setLayoutManager(layoutManager);
                binding.rvCategoryItem.setAdapter(adapterHotVideos);
                adapterHotVideos.setOnItemClick(videos -> {
                    Intent intent=new Intent(getContext(), ShowVideoActivity.class);
                    SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("link", videos.getFile_mp4());
                    editor.putString("name", videos.getTitle());
                    editor.putString("avt", videos.getAvatar());
                    editor.putInt("id", videos.getId());
                    editor.commit();

                    startActivity(intent);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ShowCategoriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_categories, container, false);
        Bundle bundle=this.getArguments();
        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        int id=sharedPreferences.getInt("id", 0);
        if (id==14)
            getCategoriesURL=DeFile.GET_CATEGORIES1_URL;
        else
            getCategoriesURL=DeFile.GET_CATEGORIES2_URL;
        String name=sharedPreferences.getString("name", "");
        binding.tvCategoriesName.setText(name);

        new GetData().execute();
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                CategoriesFragment fragment=new CategoriesFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            }
        });
        return binding.getRoot();
    }


}
