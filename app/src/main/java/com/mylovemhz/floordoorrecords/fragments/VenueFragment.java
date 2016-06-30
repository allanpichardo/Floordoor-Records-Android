package com.mylovemhz.floordoorrecords.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.mylovemhz.floordoorrecords.R;

public class VenueFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private ImageView venueImage;
    private TextView venueNameText;
    private Button continueButton;
    private GoogleApiClient googleApiClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_venue,
                container,
                false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null) {
            venueImage = (ImageView) getView().findViewById(R.id.venueImage);
            venueNameText = (TextView) getView().findViewById(R.id.venueNameText);
            continueButton = (Button) getView().findViewById(R.id.continueButton);
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(hasLocationPermission()){
            googleApiClient.connect();
        }else{
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_CONTACTS)) {
            showPermissionAlert();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSION_LOCATION);
            performPermissionRequest();
        }
    }

    private void showPermissionAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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

    }

    private boolean hasLocationPermission() {
        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
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

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public interface Callback{
        void onNoVenue();
    }
}
