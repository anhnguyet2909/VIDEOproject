
package com.example.video;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video.databinding.FragmentCategoriesBinding;

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


public class CategoriesFragment extends Fragment {
    FragmentCategoriesBinding binding;

    List<Categories> list=new ArrayList<>();
    AdapterCategories adapterCategories;

    public CategoriesFragment(){

    }

    public static CategoriesFragment newInstance() {

        Bundle args = new Bundle();

        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://demo3134737.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetCategoriesAPI getCategoriesAPI=retrofit.create(GetCategoriesAPI.class);
        Call<List<Categories>> call=getCategoriesAPI.getCategories();
        call.enqueue(new Callback<List<Categories>>() {
            @Override
            public void onResponse(Call<List<Categories>> call, Response<List<Categories>> response) {
                binding.pbLoadCate.setVisibility(View.INVISIBLE);
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), response.code()+"", Toast.LENGTH_LONG).show();
                    return;
                }
                list=response.body();
                adapterCategories=new AdapterCategories(list);
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                binding.rvCategories.setLayoutManager(layoutManager);
                binding.rvCategories.setAdapter(adapterCategories);
                adapterCategories.setOnCategoriesClick(new onCategoriesClick() {
                    @Override
                    public void onImageClick(Categories categories) {
                        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("id", categories.getId());
                        editor.putString("name", categories.getTitle());
                        editor.commit();
                        Bundle bundle=new Bundle();
                        ShowCategoriesFragment fragment=new ShowCategoriesFragment();
                        fragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Categories>> call, Throwable t) {
                binding.pbLoadCate.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return binding.getRoot();
    }
}
