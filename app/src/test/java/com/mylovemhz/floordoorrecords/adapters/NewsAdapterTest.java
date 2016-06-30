package com.mylovemhz.floordoorrecords.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylovemhz.floordoorrecords.BuildConfig;
import com.mylovemhz.floordoorrecords.MainActivity;
import com.mylovemhz.floordoorrecords.R;
import com.pkmmte.pkrss.parser.Rss2Parser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewsAdapterTest {

    private MainActivity context;
    private MockNewsAdapter newsAdapter;
    private String xmlResponse;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.setupActivity(MainActivity.class);
        newsAdapter = new MockNewsAdapter(context);
        InputStream is = context.getResources().openRawResource(R.raw.testfeed);
        xmlResponse = convertStreamToString(is);
    }

    @Test
    public void testAdapterShowsItem(){
        assertNotEquals(0, newsAdapter.getItemCount());
        assertEquals(NewsAdapter.TYPE_NEWS_ITEM, newsAdapter.getItemViewType(0));
        NewsAdapter.ViewHolder holder = newsAdapter.onCreateViewHolder(new LinearLayout(context),NewsAdapter.TYPE_NEWS_ITEM);
        newsAdapter.onBindViewHolder(holder,0);
        View itemView = newsAdapter.viewHolders.get(0).itemView;
        assertNotNull(itemView);
        printItem(itemView);
    }

    public void printItem(View itemView){
        TextView dateText = (TextView) itemView.findViewById(R.id.dateText);
        TextView titleText = (TextView) itemView.findViewById(R.id.titleText);
        TextView summaryText = (TextView) itemView.findViewById(R.id.summaryText);

        System.out.println(dateText.getText().toString());
        System.out.println(titleText.getText().toString());
        System.out.println(summaryText.getText().toString());
    }

    @Test
    public void testAdapterShowsLoadingCard() throws Exception {
        newsAdapter.onPreload();
        assertTrue(newsAdapter.isLoading());
        assertEquals(newsAdapter.articles.size() + 1, newsAdapter.getItemCount());
        assertEquals(NewsAdapter.TYPE_LOADING, newsAdapter.getItemViewType(0));
        assertEquals(NewsAdapter.TYPE_NEWS_ITEM, newsAdapter.getItemViewType(1));

        NewsAdapter.ViewHolder holder = newsAdapter.onCreateViewHolder(new LinearLayout(context),NewsAdapter.TYPE_LOADING);
        newsAdapter.onBindViewHolder(holder,0);
        View loadingView = newsAdapter.viewHolders.get(0).itemView;
        assertNotNull(loadingView);
        assertNotNull(loadingView.findViewById(R.id.progressBar));
        assertNull(loadingView.findViewById(R.id.titleText));

        newsAdapter.onLoaded(new Rss2Parser().parse(xmlResponse));
        assertEquals(newsAdapter.articles.size(), newsAdapter.getItemCount());
        assertEquals(NewsAdapter.TYPE_NEWS_ITEM, newsAdapter.getItemViewType(0));
        assertEquals(NewsAdapter.TYPE_NEWS_ITEM, newsAdapter.getItemViewType(1));
    }

    String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}