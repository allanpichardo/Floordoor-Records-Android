package com.mylovemhz.floordoorrecords;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Toast;

import com.mylovemhz.floordoorrecords.fragments.DownloadsFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsDetailFragment;
import com.mylovemhz.floordoorrecords.fragments.NoShowFragment;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;
import com.pkmmte.pkrss.Article;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements DownloadsFragment.Callback {

    public static final String ARG_ARTICLE = "article";
    public static final String ARG_STREAM = "stream";
    public static final String ARG_EXCLUSIVE = "exclusive";
    public static final String ARG_ALBUM_LIST = "album_list";
    public static final String ARG_NO_DOWNLOADS = "no_downloads";

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initWithIntent(getIntent());
    }

    private void initWithIntent(Intent intent) {
        if(intent.hasExtra(ARG_ARTICLE)){
            initNewsDetail(intent);
        }else if(intent.hasExtra(ARG_ALBUM_LIST)){
            initDownloadDetail(intent);
        }else if(intent.hasExtra(ARG_NO_DOWNLOADS)){
            initNoDownloads();
        }
    }

    private void initNoDownloads() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detailFrame, NoShowFragment.newInstance());
        ft.commit();
    }

    private void initDownloadDetail(Intent intent) {
        ArrayList<AlbumResponse> albums = intent.getExtras().getParcelableArrayList(ARG_ALBUM_LIST);
        getSupportActionBar().setTitle(R.string.available_downloads);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detailFrame, DownloadsFragment.newInstance(albums, this));
        ft.commit();
    }

    private void initNewsDetail(Intent intent) {
        Article article = intent.getExtras().getParcelable(ARG_ARTICLE);
        if (article != null) {
            getSupportActionBar().setTitle(article.getTitle());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.detailFrame, NewsDetailFragment.newInstance(article));
            ft.commit();
        }else{
            Toast.makeText(this,R.string.error_article,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownloadsSent() {
        finish();
    }
}
