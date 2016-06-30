package com.mylovemhz.floordoorrecords.adapters;

import android.content.Context;

import com.mylovemhz.floordoorrecords.R;
import com.pkmmte.pkrss.parser.Rss2Parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockNewsAdapter extends NewsAdapter {

    protected Map<Integer, ViewHolder> viewHolders;

    public MockNewsAdapter(Context context) {
        super(context, null);
        viewHolders = new HashMap<>();
    }

    @Override
    public void refresh() {
        InputStream is = context.getResources().openRawResource(R.raw.testfeed);
        String xml = convertStreamToString(is);
        Rss2Parser parser = new Rss2Parser();
        this.articles.addAll(parser.parse(xml));
    }

    String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        viewHolders.put(position,holder);
    }
}
