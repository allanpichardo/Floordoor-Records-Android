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
public class AlbumResponseTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        InputStream is = Robolectric.setupActivity(MainActivity.class)
                .getResources().openRawResource(R.raw.testalbum);
        response = Utils.convertStreamToString(is);
    }

    @Test
    public void testResponseIsParsed() throws Exception {
        AlbumResponse albumResponse = new AlbumResponse(response);
        assertTrue(albumResponse.isSuccess());
        assertEquals(albumResponse.getArtist(), "My Love MHz");
        assertEquals(albumResponse.getTitle(), "0x766");
        assertEquals(albumResponse.getImageUrl(), "http://www.example.com/image.jpg");
    }

    @Test
    public void testBadInputIsHandledAsFalse() throws Exception {
        AlbumResponse albumResponse = new AlbumResponse("{\n" +
                "execution: true,\n" +
                "content: {}\n" +
                "}");
        assertEquals(false,albumResponse.isSuccess());
    }

    @Test
    public void testCanBeSerialized() throws Exception {
        AlbumResponse originalResponse = new AlbumResponse(response);
        Bundle bundle = new Bundle();
        bundle.putParcelable("test",originalResponse);
        AlbumResponse outResponse = bundle.getParcelable("test");
        assertEquals(originalResponse.isSuccess(), outResponse.isSuccess());
        assertEquals(originalResponse.getArtist(), outResponse.getArtist());
        assertEquals(originalResponse.getTitle(), outResponse.getTitle());
        assertEquals(originalResponse.getImageUrl(), outResponse.getImageUrl());
    }
}