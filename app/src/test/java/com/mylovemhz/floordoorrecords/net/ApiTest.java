package com.mylovemhz.floordoorrecords.net;

import android.location.Location;

import com.mylovemhz.floordoorrecords.BuildConfig;
import com.mylovemhz.floordoorrecords.MainActivity;
import com.mylovemhz.floordoorrecords.fragments.VenueFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ApiTest {

    @Captor
    private ArgumentCaptor<Api.Callback<VenueResponse>> venueResponseCaptor;
    private MainActivity context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        context = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testGetVenue() throws Exception {
//        final String fakeRes = "{\n" +
//                "execution: true,\n" +
//                "content: {\n" +
//                "id: 2,\n" +
//                "name: \"Allan's Crib\",\n" +
//                "latitude: \"40.8241769\",\n" +
//                "longitude: \"-73.9457613\",\n" +
//                "created_at: null,\n" +
//                "updated_at: null\n" +
//                "}\n" +
//                "}";
//
//        Api api = mock(Api.class);
//        Api.Callback<VenueResponse> mockCallback = mock(Api.Callback.class);
//        doAnswer(new Answer<Void>(){
//
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                Api.Callback<VenueResponse> callback = (Api.Callback<VenueResponse>) invocation.getArguments()[0];
//                callback.onResponse(new VenueResponse(fakeRes));
//                return null;
//            }
//        }).when(api).getVenue(any(Location.class), any(Api.Callback.class));
//
//        api.getVenue(any(Location.class), eq(mockCallback));
//
//        verify(mockCallback).onResponse(new VenueResponse(fakeRes));
    }

}