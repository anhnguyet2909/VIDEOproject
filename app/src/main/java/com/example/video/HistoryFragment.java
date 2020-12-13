package com.example.video;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    FragmentHistoryBinding binding;
    List<HotVideos> list = new ArrayList<>();
    AdapterHistory adapterHistory;
    SQLHelperHistory sqlHelperHistory;


    public static HistoryFragment newInstance() {

        Bundle args = new Bundle();

        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);

        sqlHelperHistory = new SQLHelperHistory(getContext());

        list=sqlHelperHistory.getAllFoods();
        adapterHistory = new AdapterHistory(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rvHistory.setLayoutManager(layoutManager);
        binding.rvHistory.setAdapter(adapterHistory);

        adapterHistory.setOnItemClick(new onItemClick() {
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

        binding.btnDelete.setOnClickListener(v->{
            AlertDialog alertDialog=new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.delete_confirm))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sqlHelperHistory.deleteAllVideo();
                            list=sqlHelperHistory.getAllFoods();
                            adapterHistory = new AdapterHistory(list);
                            binding.rvHistory.setLayoutManager(layoutManager);
                            binding.rvHistory.setAdapter(adapterHistory);
                            Toast.makeText(getContext(), getString(R.string.deleted), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            alertDialog.show();

        });
        return binding.getRoot();
    }
}
