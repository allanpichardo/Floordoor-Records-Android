package com.mylovemhz.floordoorrecords;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class SplashActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private static final int REQUEST_PLAY_SERVICES = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(availability == ConnectionResult.SUCCESS){
            if(!googleApiClient.isConnected()){
                googleApiClient.connect();
            }
        }else{
            displayGooglePlayErrorDialog(availability);
        }
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
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                    startMainActivityWithLocation(null);
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(hasLocationPermission()){
            readLocationFromGoogleClient();
        }else{
            requestLocationPermission();
        }
    }

    private void readLocationFromGoogleClient() {
        try {
            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setNumUpdates(1);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    request,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            startMainActivityWithLocation(location);
                        }
                    }
            );
        }catch(SecurityException e){
            requestLocationPermission();
        }
    }

    private void startMainActivityWithLocation(Location location) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ARG_LOCATION, location);
        startActivity(intent);
        finish();
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
                displayGooglePlayErrorDialog(connectionResult.getErrorCode());
            }
        }else{
            displayGooglePlayErrorDialog(connectionResult.getErrorCode());
        }
    }

    private void displayGooglePlayErrorDialog(int errorCode) {
        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                this, errorCode, REQUEST_PLAY_SERVICES
        );
        dialog.show();
    }
}
