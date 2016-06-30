package com.mylovemhz.floordoorrecords;

import android.content.res.Configuration;
import android.os.Bundle;

import com.mylovemhz.floordoorrecords.fragments.MockNewsListFragment;
import com.mylovemhz.floordoorrecords.fragments.NewsListFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

    private ActivityController<MainActivity> activityController;
    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        buildActivity();
    }

    private void buildActivity(){
        activityController = Robolectric.buildActivity(MainActivity.class).create().start();
        mainActivity = activityController.get();
    }

    @Test
    @Config(qualifiers = "large")
    public void testTabletLayout() throws Exception {
        buildActivity();
        assertNotNull(mainActivity.findViewById(R.id.detailFrame));
    }

    @Test
    @Config(qualifiers = "large")
    public void testFirstNewsItemSelectedOnTabletLayout() throws Exception {
//        MainActivity mockActivity = spy(mainActivity);
//        verify(mockActivity).loadNews();
//        verify(mockActivity).waitUntilListIsPopulatedAndSelectFirst(NewsListFragment.newInstance(mockActivity));
    }

    @Test
    public void testPhoneLayout(){
        buildActivity();
        assertNull(mainActivity.findViewById(R.id.detailFrame));
    }

    public void changeOrientation(){
        int currentOrientation = mainActivity.getResources().getConfiguration().orientation;
        boolean isPortraitOrUndefined = currentOrientation == Configuration.ORIENTATION_PORTRAIT || currentOrientation == Configuration.ORIENTATION_UNDEFINED;
        mainActivity.getResources().getConfiguration().orientation = isPortraitOrUndefined ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT;

        Bundle bundle = new Bundle();
        activityController.saveInstanceState(bundle).pause().stop().destroy();
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class).create(bundle).start().restoreInstanceState(bundle).resume().visible();
        mainActivity = controller.get();
    }
}