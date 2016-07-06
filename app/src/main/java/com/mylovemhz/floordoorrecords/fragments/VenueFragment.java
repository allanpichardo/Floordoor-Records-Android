package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mylovemhz.floordoorrecords.MainActivity;
import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;
import com.mylovemhz.floordoorrecords.net.Api;
import com.mylovemhz.floordoorrecords.net.PerformanceResponse;
import com.mylovemhz.floordoorrecords.net.VenueResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class VenueFragment extends Fragment implements View.OnClickListener{

    private static final String STATE_VENUE = "state_venue";
    private ImageView venueImage;
    private TextView venueNameText;
    private Button continueButton;
    private VenueResponse venueResponse;
    private Callback callback;

    public VenueFragment(){}

    public static VenueFragment newInstance(VenueResponse venueResponse){
        VenueFragment fragment = new VenueFragment();
        fragment.setVenueResponse(venueResponse);
        return fragment;
    }

    public void setVenueResponse(VenueResponse venueResponse) {
        this.venueResponse = venueResponse;
    }

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
        if(savedInstanceState != null){
            onViewStateRestored(savedInstanceState);
        }
        if(getView() != null) {
            venueImage = (ImageView) getView().findViewById(R.id.venueImage);
            venueNameText = (TextView) getView().findViewById(R.id.venueNameText);
            continueButton = (Button) getView().findViewById(R.id.continueButton);

            init();
        }
    }

    private void init(){
        continueButton.setVisibility(
                isTablet() ? View.INVISIBLE : View.VISIBLE
        );
        continueButton.setOnClickListener(this);
        venueNameText.setText(venueResponse.getName());
        attachMapToCard();
        try {
            Picasso.with(getContext())
                    .load(venueResponse.getImageUrl())
                    .transform(new BlurTransformation(getContext(), 10))
                    .into(venueImage);
        }catch(IllegalArgumentException e){
            //skip image
        }
    }

    protected void attachMapToCard(){
        GoogleMapOptions options = new GoogleMapOptions();
        options.liteMode(true);
        options.mapToolbarEnabled(false);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                zoomIntoVenue(googleMap);
            }
        });
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.mapCard, mapFragment);
        ft.commit();
    }

    private void zoomIntoVenue(GoogleMap googleMap){
        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(venueResponse.getLatitude(), venueResponse.getLongitude()),
                        13
                )
        );
        googleMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(venueResponse.getLatitude(), venueResponse.getLongitude()))
        );
    }

    protected boolean isTablet(){
        return ((MainActivity) getActivity()).isTablet();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            venueResponse = savedInstanceState.getParcelable(STATE_VENUE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_VENUE, venueResponse);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        loadGifts();
    }

    public void toggleProgressBar(boolean visible){
        ((MainActivity)getActivity()).toggleProgressBar(visible);
    }

    private void loadGifts() {
        toggleProgressBar(true);
        Api.with(getContext())
                .getPerformances(
                        venueResponse.getId(),
                        new Api.Callback<PerformanceResponse>() {
                            @Override
                            public void onResponse(PerformanceResponse response) {
                                if(response.isSuccess()){
                                    loadMetadata(response.getPerformances());
                                }else{
                                    toggleProgressBar(false);
                                    if(callback != null) callback.onNoDownloadsFound();
                                }
                            }

                            @Override
                            public void onError() {
                                toggleProgressBar(false);
                                if(callback != null) callback.onNoDownloadsFound();
                            }
                        }
                );
    }

    private void loadMetadata(List<PerformanceResponse.Performance> performances) {
        Api.with(getContext())
                .getAlbumInfo(performances, new Api.Callback<List<AlbumResponse>>() {
                    @Override
                    public void onResponse(List<AlbumResponse> response) {
                        if(callback != null) callback.onDownloadsLoaded(response);
                    }

                    @Override
                    public void onError() {
                        if(callback != null) callback.onNoDownloadsFound();
                    }
                });
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public interface Callback{
        void onDownloadsLoaded(List<AlbumResponse> albumResponseList);
        void onNoDownloadsFound();
    }
}
