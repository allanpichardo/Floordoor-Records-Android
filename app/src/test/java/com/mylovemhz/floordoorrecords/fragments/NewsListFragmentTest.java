package com.mylovemhz.floordoorrecords.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mylovemhz.floordoorrecords.BuildConfig;
import com.mylovemhz.floordoorrecords.MainActivity;
import com.mylovemhz.floordoorrecords.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewsListFragmentTest {

    private MockNewsListFragment fragment;
    private SupportFragmentController<MockNewsListFragment> fragmentController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fragment = new MockNewsListFragment();
        fragmentController = SupportFragmentController.of(fragment);
        fragment = fragmentController.get();
    }

    @Test
    public void testFragmentAtaches() throws Exception {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.masterFrame, fragment);
        ft.addToBackStack(null);
        ft.commit();

        assertNotNull(fragment.getView());
        assertNotEquals(0, fragment.newsAdapter.getItemCount());

        Bundle bundle = new Bundle();
        fragment.onSaveInstanceState(bundle);
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroy();
        fragment.onCreate(bundle);
        fragment.onStart();
        fragment.onViewStateRestored(bundle);
        fragment.onResume();

        assertNotNull(fragment.getView());
        assertNotNull(fragment.recyclerView);
        assertNotEquals(0, fragment.newsAdapter.getItemCount());
    }

    @Test
    public void testReadyCallback() throws Exception {
        MockNewsListFragment spyFragment = spy(fragment);
        spyFragment.setOnReadyListener(new NewsListFragment.OnReadyListener() {
            @Override
            public void onReady() {

            }
        });
        verify(spyFragment).onReadyListener.onReady();
    }
}