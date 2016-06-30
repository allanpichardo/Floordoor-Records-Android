package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.adapters.MockNewsAdapter;

public class MockNewsListFragment extends NewsListFragment{
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null) {
            recyclerView = (RecyclerView) getView().findViewById(R.id.newsRecycler);
            newsAdapter = new MockNewsAdapter(getContext());
        }
    }
}
