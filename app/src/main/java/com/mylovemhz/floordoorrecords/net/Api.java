package com.mylovemhz.floordoorrecords.net;

import android.content.Context;
import android.location.Location;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Api {

    public static final String URL_GET_VENUE = "https://download.floordoorrecords.com/api/venue/";
    public static final String URL_GET_PERFORMANCES = "https://download.floordoorrecords.com/api/performances/";
    public static final String URL_GET_ALBUM = "https://download.floordoorrecords.com/api/album/";

    protected Context context;

    protected Api(Context context){
        this.context = context;
    }

    public static Api with(Context context){
        return new Api(context);
    }

    /**
     * Returns the nearest venue adjacent to the location provided
     * @param location a location to search
     * @param callback a callback typed to VenueResponse
     */
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

    /**
     * Returns a list of Performances, if any, that are happening at the
     * specified venue. Performance includes the album id of the gift
     * @param venueId where to search for performances
     * @param callback a callback with type PerformanceResponse
     */
    public void getPerformances(int venueId, final Callback<PerformanceResponse> callback){
        String url = URL_GET_PERFORMANCES + venueId;

        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponse(new PerformanceResponse(response));
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

    /**
     * Returns metadata about an album
     * @param albumId
     * @param callback
     */
    public void getAlbumInfo(int albumId, final Callback<AlbumResponse> callback){
        String url = URL_GET_ALBUM + albumId;

        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponse(new AlbumResponse(response));
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

    public void requestDownloads(int[] albumIds, final Callback<DownloadResponse> callback){

    }

    public interface Callback<T>{
        void onResponse(T response);
        void onError();
    }
}
