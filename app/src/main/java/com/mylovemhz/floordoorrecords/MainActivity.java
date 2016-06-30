package com.mylovemhz.floordoorrecords;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mylovemhz.floordoorrecords.adapters.NewsAdapter;
import com.mylovemhz.floordoorrecords.fragments.NewsDetailFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsListFragment;
import com.pkmmte.pkrss.Article;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsAdapter.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private GoogleApiClient googleApiClient;

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
        initGoogleApiClient();
        setSupportActionBar(toolbar);
        configureNavigationDrawer();
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_CONTACTS)) {
            showPermissionAlert();
        } else {
            performPermissionRequest();
        }
    }

    private void showPermissionAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.rationale_location);
        alert.setPositiveButton("Yes, grant access", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performPermissionRequest();
            }
        });
        alert.setNegativeButton("No, don't use my location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    private void performPermissionRequest() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.READ_CONTACTS},
                REQUEST_PERMISSION_LOCATION);
    }

    private boolean hasLocationPermission() {
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
