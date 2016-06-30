package com.mylovemhz.floordoorrecords;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mylovemhz.floordoorrecords.adapters.NewsAdapter;
import com.mylovemhz.floordoorrecords.fragments.NewsDetailFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsListFragment;
import com.pkmmte.pkrss.Article;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsAdapter.Callback {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private int currentNavSection = R.id.nav_news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        init();
    }

    private void init(){
        setSupportActionBar(toolbar);
        configureNavigationDrawer();
    }

    protected boolean isTablet(){
        return findViewById(R.id.detailFrame) != null;
    }

    private void configureNavigationDrawer(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigateToSection(currentNavSection);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        currentNavSection = id;
        navigateToSection(id);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToSection(int id) {
        navigationView.setCheckedItem(id);
        switch(id){
            case R.id.nav_news:
                loadNews();
                break;
        }
    }

    private void attachFragment(Fragment fragment, int containerId, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(containerId, fragment, tag);
        ft.commit();
    }

    protected void loadNews() {
        final NewsListFragment newsListFragment = NewsListFragment.newInstance(this);
        newsListFragment.setOnReadyListener(new NewsListFragment.OnReadyListener() {
            @Override
            public void onReady() {
                if(isTablet()){
                    waitUntilListIsPopulatedAndSelectFirst(newsListFragment);
                }
            }
        });
        attachFragment(newsListFragment, R.id.masterFrame, getString(R.string.tag_news_list));
    }

    protected void waitUntilListIsPopulatedAndSelectFirst(final NewsListFragment fragment){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(fragment.getNewsAdapter().getItemCount() > 0){
                    fragment.getNewsAdapter().selectItem(0);
                    handler.removeCallbacks(this);
                }else{
                    handler.postDelayed(this, 200);
                }
            }
        });
    }

    @Override
    public void onNewsItemClicked(Article article) {
        if(isTablet()){
            NewsDetailFragment newsDetailFragment = NewsDetailFragment.newInstance(article);
            attachFragment(newsDetailFragment, R.id.detailFrame, getString(R.string.tag_news_detail));
        }else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.ARG_NEWS,true);
            intent.putExtra(DetailActivity.ARG_ARTICLE, article);
            startActivity(intent);
        }
    }
}
