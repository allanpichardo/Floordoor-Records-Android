package com.mylovemhz.floordoorrecords.net;

import android.content.Context;
import android.location.Location;

public class MockApi extends Api {

    public String fakeRes = "{\n" +
            "execution: true,\n" +
            "content: {\n" +
            "id: 2,\n" +
            "name: \"Allan's Crib\",\n" +
            "latitude: \"40.8241769\",\n" +
            "longitude: \"-73.9457613\",\n" +
            "created_at: null,\n" +
            "updated_at: null\n" +
            "}\n" +
            "}";

    private MockApi(Context context) {
        super(context);
    }

    @Override
    public void getVenue(Location location, Callback<VenueResponse> callback) {
        callback.onResponse(new VenueResponse(fakeRes));
    }
}
