package com.example.video;

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

        binding.btnDelete.setOnClickListener(v->{
            sqlHelperHistory.deleteAllVideo();
            list=sqlHelperHistory.getAllFoods();
            adapterHistory = new AdapterHistory(list);
            binding.rvHistory.setLayoutManager(layoutManager);
            binding.rvHistory.setAdapter(adapterHistory);
        });
        return binding.getRoot();
    }
}
