package com.mylovemhz.floordoorrecords.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylovemhz.floordoorrecords.R;

public class AboutFragment extends Fragment implements View.OnClickListener{

    public AboutFragment(){}

    public static AboutFragment newInstance(){
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_about, container, false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null){
            TextView linkText = (TextView) getView().findViewById(R.id.linkText);
            linkText.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.floordoorurl)));
        startActivity(browserIntent);
    }
}
