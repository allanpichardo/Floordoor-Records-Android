package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mylovemhz.floordoorrecords.R;

public class NoShowFragment extends Fragment{

    public NoShowFragment(){}

    public static NoShowFragment newInstance(){
        return new NoShowFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_no_show,
                container,
                false
        );
    }
}
