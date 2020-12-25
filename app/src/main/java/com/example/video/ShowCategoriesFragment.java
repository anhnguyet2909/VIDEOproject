package com.example.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowCategoriesFragment extends Fragment {
    List<HotVideos> list = new ArrayList<>();
    AdapterShowCategories adapterShowCategories;
    FragmentShowCategoriesBinding binding;
    Call<List<HotVideos>> call;

    public ShowCategoriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_categories, container, false);
        Bundle bundle = this.getArguments();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://demo3134737.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetHotVideoAPI getHotVideoAPI = retrofit.create(GetHotVideoAPI.class);

        int id = sharedPreferences.getInt("id", 0);
        if (id == 14)
            call = getHotVideoAPI.getCategories1();
        else
            call=getHotVideoAPI.getCategories2();
        String name = sharedPreferences.getString("name", "");
        binding.tvCategoriesName.setText(name);

        call.enqueue(new Callback<List<HotVideos>>() {
            @Override
            public void onResponse(Call<List<HotVideos>> call, Response<List<HotVideos>> response) {
                binding.load.setVisibility(View.INVISIBLE);
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), response.code() + "", Toast.LENGTH_LONG).show();
                    return;
                }
                list=response.body();
                adapterShowCategories = new AdapterShowCategories(list);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2, RecyclerView.VERTICAL,false);
                binding.rvCategoryItem.setLayoutManager(layoutManager);
                binding.rvCategoryItem.setAdapter(adapterShowCategories);
                adapterShowCategories.setOnItemClick(videos -> {
                    Intent intent = new Intent(getContext(), ShowVideoActivity.class);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("link", videos.getFile_mp4());
                    editor.putString("name", videos.getTitle());
                    editor.putString("avt", videos.getAvatar());
                    editor.putInt("id", videos.getId());
                    editor.commit();

                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(Call<List<HotVideos>> call, Throwable t) {
                binding.load.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                CategoriesFragment fragment = new CategoriesFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            }
        });
        return binding.getRoot();
    }


}
