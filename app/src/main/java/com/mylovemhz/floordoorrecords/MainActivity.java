package com.mylovemhz.floordoorrecords;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.mylovemhz.floordoorrecords.adapters.NewsAdapter;
import com.mylovemhz.floordoorrecords.fragments.AboutFragment;
import com.mylovemhz.floordoorrecords.fragments.DownloadsFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsDetailFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsListFragment;
import com.mylovemhz.floordoorrecords.fragments.NoShowFragment;
import com.mylovemhz.floordoorrecords.fragments.VenueFragment;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;
import com.mylovemhz.floordoorrecords.net.Api;
import com.mylovemhz.floordoorrecords.net.VenueResponse;
import com.mylovemhz.floordoorrecords.persistence.LocalStore;
import com.pkmmte.pkrss.Article;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsAdapter.Callback,
        VenueFragment.Callback {

    private static final String STATE_LOCATION = "state_location";
    private static final String STATE_NAVIGATION = "state_navigation";
    public static final String ARG_LOCATION = "location";

    private TextView emailText;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private int currentNavSection = R.id.nav_news;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        emailText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emailText);

        if(savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState);
        }
        init();
    }

    private void init(){
        setSupportActionBar(toolbar);
        configureNavigationDrawer();
        initLocationFromIntent();
    }

    private void initLocationFromIntent() {
        if(getIntent().hasExtra(ARG_LOCATION)){
            currentLocation = getIntent().getParcelableExtra(ARG_LOCATION);
        }
    }


    public boolean isTablet(){
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
            case R.id.nav_live:
                loadVenue();
                break;
            case R.id.nav_about:
                loadAbout();
                break;
        }
    }

    private void loadAbout() {
        attachFragment(
                AboutFragment.newInstance(),R.id.masterFrame,getString(R.string.tag_about)
        );
    }

    private void loadVenue() {
        if(currentLocation != null){
            toggleProgressBar(true);
            Api.with(this).getVenue(
                    currentLocation,
                    new Api.Callback<VenueResponse>() {
                        @Override
                        public void onResponse(VenueResponse response) {
                            if(response.isSuccess()){
                                attachFragment(
                                        VenueFragment.newInstance(response, MainActivity.this),
                                        R.id.masterFrame, getString(R.string.tag_venue));
                            }else{
                                attachNoShowFragment();
                            }
                            toggleProgressBar(false);
                        }

                        @Override
                        public void onError() {
                            attachNoShowFragment();
                            toggleProgressBar(false);
                        }
                    }
            );
        }else{
            attachNoShowFragment();
        }
    }

    private void attachNoShowFragment(){
        attachFragment(
                NoShowFragment.newInstance(),
                R.id.masterFrame, getString(R.string.tag_no_show)
        );
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
            intent.putExtra(DetailActivity.ARG_ARTICLE, article);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lastEmail = LocalStore.with(this).getLastUsedEmail();
        emailText.setText(lastEmail);
    }

    public void toggleProgressBar(boolean show){
        setProgressBarIndeterminate(true);
        setProgressBarVisibility(show);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_LOCATION, currentLocation);
        outState.putInt(STATE_NAVIGATION, currentNavSection);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentLocation = savedInstanceState.getParcelable(STATE_LOCATION);
        currentNavSection = savedInstanceState.getInt(STATE_NAVIGATION);
    }

    @Override
    public void onDownloadsLoaded(List<AlbumResponse> albumResponseList) {
        if(isTablet()){
            loadDownloadsInSideFrame(albumResponseList);
        }else{
            loadDownloadsInDetailActivity((ArrayList<AlbumResponse>) albumResponseList);
        }
    }

    private void loadDownloadsInDetailActivity(ArrayList<AlbumResponse> albumResponseList) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putParcelableArrayListExtra(DetailActivity.ARG_ALBUM_LIST, albumResponseList);
        startActivity(intent);
    }

    private void loadDownloadsInSideFrame(List<AlbumResponse> albumResponseList) {
        attachFragment(
                DownloadsFragment.newInstance(albumResponseList, new DownloadsFragment.Callback() {
                    @Override
                    public void onDownloadsSent() {

                    }
                }),
                R.id.detailFrame, "tag_download_list"
        );
    }

    @Override
    public void onNoDownloadsFound() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_NO_DOWNLOADS, true);
        startActivity(intent);
    }
}
