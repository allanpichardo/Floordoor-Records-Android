package com.mylovemhz.floordoorrecords;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequestCreator;
import com.google.android.gms.location.LocationServices;
import com.mylovemhz.floordoorrecords.adapters.NewsAdapter;
import com.mylovemhz.floordoorrecords.fragments.NewsDetailFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsListFragment;
import com.mylovemhz.floordoorrecords.fragments.NoShowFragment;
import com.mylovemhz.floordoorrecords.fragments.VenueFragment;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;
import com.mylovemhz.floordoorrecords.net.Api;
import com.mylovemhz.floordoorrecords.net.VenueResponse;
import com.pkmmte.pkrss.Article;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsAdapter.Callback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        VenueFragment.Callback {

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private static final int REQUEST_PLAY_SERVICES = 2;
    private static final String STATE_LOCATION = "state_location";
    private static final String STATE_NAVIGATION = "state_navigation";

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private GoogleApiClient googleApiClient;

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

        if(savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState);
        }
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
        }
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
    protected void onStart() {
        super.onStart();
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
            toggleProgressBar(true);
        }
    }

    public void toggleProgressBar(boolean show){
        setProgressBarIndeterminate(true);
        setProgressBarVisibility(show);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                    readLocationFromGoogleClient();
                } else {
                    currentLocation = null;
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        toggleProgressBar(false);
        if(hasLocationPermission()){
            readLocationFromGoogleClient();
        }else{
            requestLocationPermission();
        }
    }

    private void readLocationFromGoogleClient() {
        try {
            LocationRequest request = LocationRequest.create();
            request.setFastestInterval(333);
            request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            request.setNumUpdates(1);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    request,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            currentLocation = location;
                            toggleProgressBar(false);
                        }
                    }
            );
            toggleProgressBar(true);
        }catch(SecurityException e){
            requestLocationPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(this, REQUEST_PLAY_SERVICES);
            } catch (IntentSender.SendIntentException e) {
                //GPS COULD NOT BE RESOLVED
            }
        }
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

    }

    @Override
    public void onNoDownloadsFound() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_NO_DOWNLOADS, true);
        startActivity(intent);
    }
}
