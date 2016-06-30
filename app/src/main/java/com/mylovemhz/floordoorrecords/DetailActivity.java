package com.mylovemhz.floordoorrecords;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Toast;

import com.mylovemhz.floordoorrecords.fragments.NewsDetailFragment;
import com.pkmmte.pkrss.Article;

public class DetailActivity extends AppCompatActivity {

    public static final String ARG_NEWS = "news";
    public static final String ARG_ARTICLE = "article";
    public static final String ARG_STREAM = "stream";
    public static final String ARG_EXCLUSIVE = "exclusive";

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
        if(intent.hasExtra(ARG_NEWS)){
            initNewsDetail(intent);
        }
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
}
