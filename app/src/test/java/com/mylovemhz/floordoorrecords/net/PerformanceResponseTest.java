package com.mylovemhz.floordoorrecords.net;

import android.os.Bundle;

import com.mylovemhz.floordoorrecords.BuildConfig;
import com.mylovemhz.floordoorrecords.MainActivity;
import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PerformanceResponseTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        InputStream is = Robolectric.setupActivity(MainActivity.class)
                .getResources().openRawResource(R.raw.testperformance);
        response = Utils.convertStreamToString(is);
    }

    @Test
    public void testResponseIsParsed() throws Exception {
        PerformanceResponse performanceResponse = new PerformanceResponse(response);
        assertTrue(performanceResponse.isSuccess());
        assertTrue(performanceResponse.getPerformances().size() > 0);
        assertEquals(1, performanceResponse.getPerformances().get(0).getAlbumId());
    }

    @Test
    public void testBadInputIsHandledAsFalse() throws Exception {
        PerformanceResponse performanceResponse = new PerformanceResponse("{\n" +
                "execution: true,\n" +
                "content: []\n" +
                "}");
        assertEquals(false,performanceResponse.isSuccess());
    }

    @Test
    public void testCanBeSerialized() throws Exception {
        PerformanceResponse originalResponse = new PerformanceResponse(response);
        Bundle bundle = new Bundle();
        bundle.putParcelable("test",originalResponse);
        PerformanceResponse outResponse = bundle.getParcelable("test");
        assertEquals(originalResponse.isSuccess(), outResponse.isSuccess());
        assertEquals(originalResponse.getPerformances(), outResponse.getPerformances());
    }
}