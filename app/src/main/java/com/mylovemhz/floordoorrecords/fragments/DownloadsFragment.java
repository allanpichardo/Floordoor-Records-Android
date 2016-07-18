package com.mylovemhz.floordoorrecords.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.mylovemhz.floordoorrecords.net.Api;
import com.mylovemhz.floordoorrecords.net.DownloadResponse;
import com.mylovemhz.floordoorrecords.persistence.LocalStore;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DownloadsFragment extends Fragment
        implements DownloadsAdapter.Callback, View.OnClickListener {

    private RecyclerView downloadRecycler;
    private Button downloadButton;
    private EditText emailText;
    private DownloadsAdapter adapter;
    private Set<Integer> selection;
    private ProgressDialog progressDialog;
    private Callback callback;

    public DownloadsFragment(){}

    public static DownloadsFragment newInstance(List<AlbumResponse> albums, Callback callback){
        DownloadsFragment fragment = new DownloadsFragment();
        fragment.adapter = new DownloadsAdapter(albums, fragment);
        fragment.selection = fragment.adapter.getSelections();
        fragment.callback = callback;
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
        downloadButton.setOnClickListener(this);
        downloadButton.setEnabled(false);
        downloadRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        downloadRecycler.setAdapter(adapter);
        initProgressDialog();
        initEmailTextBox();
    }

    private void initEmailTextBox(){
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
        String lastEmail = LocalStore.with(getContext()).getLastUsedEmail();
        emailText.setText(lastEmail);
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
        progressDialog.show();
        Api.with(getContext())
                .requestDownloads(
                        new ArrayList<>(selection),
                        emailText.getText().toString(),
                        new Api.Callback<DownloadResponse>() {
                            @Override
                            public void onResponse(DownloadResponse response) {
                                if(response.isSuccess()){
                                    showSnackbar(getResources().getString(R.string.check_email));
                                    LocalStore.with(getContext()).setLastUsedEmail(emailText.getText().toString());
                                    dismissProgressDialog();
                                    if(callback != null) callback.onDownloadsSent();
                                }else{
                                    showSnackbar(getResources().getString(R.string.error_download));
                                    dismissProgressDialog();
                                }
                            }

                            @Override
                            public void onError() {
                                showSnackbar(getResources().getString(R.string.error_download));
                                dismissProgressDialog();
                            }
                        }
                );
    }

    private void showSnackbar(String message){
        Toast.makeText(
                getContext(),
                message, Toast.LENGTH_SHORT
        ).show();
    }

    private void dismissProgressDialog(){
        if(getActivity() != null && !getActivity().isFinishing()) {
            progressDialog.dismiss();
        }
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.download_progress));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public interface Callback{
        void onDownloadsSent();
    }
}
