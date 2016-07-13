package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.adapters.DownloadsAdapter;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;

import java.util.List;
import java.util.Set;

public class DownloadsFragment extends Fragment implements DownloadsAdapter.Callback {

    private RecyclerView downloadRecycler;
    private Button downloadButton;
    private DownloadsAdapter adapter;
    private Set<Integer> selection;

    public DownloadsFragment(){}

    public static DownloadsFragment newInstance(List<AlbumResponse> albums){
        DownloadsFragment fragment = new DownloadsFragment();
        fragment.adapter = new DownloadsAdapter(albums, fragment);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_download_list, container, false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSelectionChanged(Set<Integer> selection) {

    }
}
