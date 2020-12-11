package com.example.video;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.video.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);

        getFragment(HotVideosFragment.newInstance());
        binding.llHotVideos.setOnClickListener(v-> {
            getFragment(HotVideosFragment.newInstance());
            binding.v1.setVisibility(View.VISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
        });
        binding.llCategories.setOnClickListener(v->{
            getFragment(CategoriesFragment.newInstance());
            binding.v2.setVisibility(View.VISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
        });
        binding.llHistory.setOnClickListener(v->{
            getFragment(HistoryFragment.newInstance());
            binding.v3.setVisibility(View.VISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
        });
        binding.llSetting.setOnClickListener(v->{
            getFragment(SettingFragment.newInstance());
            binding.v4.setVisibility(View.VISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
        });
    }
    public void getFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }
}