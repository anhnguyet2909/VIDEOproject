package com.example.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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



        if(!checkInternetConnection()){
            binding.btnReturn.setVisibility(View.VISIBLE);
            binding.tvCheck.setVisibility(View.VISIBLE);
            binding.svMain.setVisibility(View.INVISIBLE);
        }
        else{
            setClick();
        }
        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection()){
                    Toast.makeText(getBaseContext(), getText(R.string.network_confirm), Toast.LENGTH_LONG).show();
                    binding.svMain.setVisibility(View.VISIBLE);
                    setClick();
                    binding.btnReturn.setVisibility(View.INVISIBLE);
                    binding.tvCheck.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    public void setClick(){
        getFragment(HotVideosFragment.newInstance());
        binding.llHotVideos.setOnClickListener(v-> {
            getFragment(HotVideosFragment.newInstance());
            binding.v1.setVisibility(View.VISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
            binding.v5.setVisibility(View.INVISIBLE);
        });
        binding.llCategories.setOnClickListener(v->{
            getFragment(CategoriesFragment.newInstance());
            binding.v2.setVisibility(View.VISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
            binding.v5.setVisibility(View.INVISIBLE);
        });
        binding.llHistory.setOnClickListener(v->{
            getFragment(HistoryFragment.newInstance());
            binding.v3.setVisibility(View.VISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
            binding.v5.setVisibility(View.INVISIBLE);
        });
        binding.llSetting.setOnClickListener(v->{
            getFragment(SettingFragment.newInstance());
            binding.v4.setVisibility(View.VISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
            binding.v5.setVisibility(View.INVISIBLE);
        });

        binding.llFavorite.setOnClickListener(v->{
            getFragment(FavoriteFragment.newInstance());
            binding.v5.setVisibility(View.VISIBLE);
            binding.v4.setVisibility(View.INVISIBLE);
            binding.v1.setVisibility(View.INVISIBLE);
            binding.v2.setVisibility(View.INVISIBLE);
            binding.v3.setVisibility(View.INVISIBLE);
        });
    }

    public void getFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    private boolean checkInternetConnection() {
        // Get Connectivity Manager
        ConnectivityManager connManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Details about the currently active default data network
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(this, getText(R.string.network_info), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(this, getText(R.string.network_connected), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(this, getText(R.string.network_available), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}