package com.mylovemhz.floordoorrecords.adapters;

import com.mylovemhz.floordoorrecords.net.AlbumResponse;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MockDownloadsAdapter extends DownloadsAdapter {

    protected Map<Integer, ViewHolder> viewHolders;

    public MockDownloadsAdapter(List<AlbumResponse> albumResponses) {
        super(albumResponses);
        viewHolders = new HashMap<>();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        viewHolders.put(position, holder);
    }
}
