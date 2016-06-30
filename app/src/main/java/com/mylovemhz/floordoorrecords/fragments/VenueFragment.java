package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylovemhz.floordoorrecords.R;

public class VenueFragment extends Fragment {

    private ImageView venueImage;
    private TextView venueNameText;
    private Button continueButton;

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
        }
    }

}
