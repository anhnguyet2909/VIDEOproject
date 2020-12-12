package com.example.video;

import android.content.Context;
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

import com.example.video.databinding.FragmentCategoriesBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment {
    FragmentCategoriesBinding binding;

    String linkURL = DeFile.CATEGORIES_URL;
    String result = "";
    String jArray = "";
    List<Categories> list=new ArrayList<>();
    AdapterCategories adapterCategories;

    class GetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url=new URL(linkURL);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();

                int byteCharacter;

                while ((byteCharacter = inputStream.read()) != -1) {
                    result+=(char)byteCharacter;
                }
                jArray=result;
                binding.pbLoadCate.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            binding.pbLoadCate.setVisibility(View.INVISIBLE);
            try {
                JSONArray jsonArray=new JSONArray(jArray);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject= jsonArray.getJSONObject(i);
                    int id=jsonObject.getInt(DeFile.ID);
                    String title=jsonObject.getString(DeFile.TITLE);
                    String thumb=jsonObject.getString(DeFile.THUMB);
                    Categories cate=new Categories(id, title, thumb);
                    list.add(cate);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
        new GetData().execute();
        return binding.getRoot();
    }
}
