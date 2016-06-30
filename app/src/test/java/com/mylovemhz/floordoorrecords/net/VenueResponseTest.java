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
public class VenueResponseTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        InputStream is = Robolectric.setupActivity(MainActivity.class)
                .getResources().openRawResource(R.raw.testvenue);
        response = Utils.convertStreamToString(is);
    }

    @Test
    public void testResponseIsParsed() throws Exception {
        VenueResponse venueResponse = new VenueResponse(response);
        assertEquals(true, venueResponse.isSuccess());
        assertEquals(2, venueResponse.getId());
        assertEquals("Allan's Crib", venueResponse.getName());
        assertEquals(40.8241769, venueResponse.getLatitude(), 0);
        assertEquals(-73.9457613, venueResponse.getLongitude(), 0);
    }

    @Test
    public void testBadInputIsHandledAsFalse() throws Exception {
        VenueResponse venueResponse = new VenueResponse("{\n" +
                "execution: true,\n" +
                "content: {}\n" +
                "}");
        assertEquals(false,venueResponse.isSuccess());
    }

    @Test
    public void testCanBeSerialized() throws Exception {
        VenueResponse originalResponse = new VenueResponse(response);
        Bundle bundle = new Bundle();
        bundle.putParcelable("test",originalResponse);
        VenueResponse outResponse = bundle.getParcelable("test");
        assertEquals(originalResponse.isSuccess(), outResponse.isSuccess());
        assertEquals(originalResponse.getId(), outResponse.getId());
        assertEquals(originalResponse.getName(), outResponse.getName());
        assertEquals(originalResponse.getLatitude(), outResponse.getLatitude(), 0);
        assertEquals(originalResponse.getLongitude(), outResponse.getLongitude(), 0);
    }
}