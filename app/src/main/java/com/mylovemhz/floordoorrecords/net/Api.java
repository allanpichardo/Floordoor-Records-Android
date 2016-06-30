package com.mylovemhz.floordoorrecords.net;

import android.content.Context;
import android.location.Location;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Api {

    public static final String URL_GET_VENUE = "https://download.floordoorrecords.com/api/venue/";

    protected Context context;

    protected Api(Context context){
        this.context = context;
    }

    public static Api with(Context context){
        return new Api(context);
    }

    public void getVenue(Location location, final Callback<VenueResponse> callback){
        String url = URL_GET_VENUE + location.getLatitude() + "," + location.getLongitude();

        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponse(new VenueResponse(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError();
                    }
                }
        );
        RequestManager.getInstance(context.getApplicationContext()).addToRequestQueue(request);

    }

    public interface Callback<T>{
        void onResponse(T response);
        void onError();
    }
}
