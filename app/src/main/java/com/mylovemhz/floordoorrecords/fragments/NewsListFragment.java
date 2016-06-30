package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.adapters.NewsAdapter;
import com.pkmmte.pkrss.Article;

public class NewsListFragment extends Fragment {

    public static final String STATE_NEWS_LIST = "news_list";

    protected RecyclerView recyclerView;
    protected NewsAdapter newsAdapter;
    protected NewsAdapter.Callback callback;
    protected OnReadyListener onReadyListener;

    public NewsListFragment(){
        super();
    }

    public static NewsListFragment newInstance(NewsAdapter.Callback callback){
        NewsListFragment fragment = new NewsListFragment();
        fragment.setCallback(callback);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list,
                container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null) {
            recyclerView = (RecyclerView) getView().findViewById(R.id.newsRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            newsAdapter = new NewsAdapter(getContext(), callback);
        }
    }

    public NewsAdapter getNewsAdapter() {
        return newsAdapter;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(newsAdapter);
        if(onReadyListener != null){
            onReadyListener.onReady();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_NEWS_LIST, newsAdapter.getArticles());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            newsAdapter.setArticles(savedInstanceState.<Article>getParcelableArrayList(STATE_NEWS_LIST));
        }
    }

    public void setOnReadyListener(OnReadyListener listener){
        this.onReadyListener = listener;
    }

    public void setCallback(NewsAdapter.Callback callback) {
        this.callback = callback;
    }

    public interface OnReadyListener{
        void onReady();
    }
}
