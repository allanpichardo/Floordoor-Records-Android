package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.adapters.DownloadsAdapter;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.Set;

public class DownloadsFragment extends Fragment
        implements DownloadsAdapter.Callback, View.OnClickListener {

    private RecyclerView downloadRecycler;
    private Button downloadButton;
    private EditText emailText;
    private DownloadsAdapter adapter;
    private Set<Integer> selection;

    public DownloadsFragment(){}

    public static DownloadsFragment newInstance(List<AlbumResponse> albums){
        DownloadsFragment fragment = new DownloadsFragment();
        fragment.adapter = new DownloadsAdapter(albums, fragment);
        fragment.selection = fragment.adapter.getSelections();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_download_list, container, false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null) {
            downloadRecycler = (RecyclerView) getView().findViewById(R.id.downloadRecycler);
            downloadButton = (Button) getView().findViewById(R.id.downloadButton);
            emailText = (EditText) getView().findViewById(R.id.emailText);
            init();
        }
    }

    private void init(){
        downloadButton.setEnabled(false);
        downloadRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        downloadRecycler.setAdapter(adapter);
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEntries();
            }
        });

    }

    @Override
    public void onSelectionChanged(Set<Integer> selection) {
        this.selection = selection;
        validateEntries();
    }

    private void validateEntries(){
        EmailValidator validator = EmailValidator.getInstance(false);
        boolean isButtonEnabled = emailText.getText().length() > 0 &&
                validator.isValid(emailText.getText().toString()) &&
                selection.size() > 0;
        downloadButton.setEnabled(isButtonEnabled);
    }

    @Override
    public void onClick(View v) {
        if(selection.size() > 0){
            //todo go to email entry
        }
    }
}
