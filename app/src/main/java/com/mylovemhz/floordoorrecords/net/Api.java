package com.mylovemhz.floordoorrecords.net;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    /**
     * Returns metadata about multiple albums
     * @param peformances
     * @param callback
     */
    public void getAlbumInfo(List<PerformanceResponse.Performance> peformances, final Callback<List<AlbumResponse>> callback){
        new AsyncTask<List<PerformanceResponse.Performance>, Void, List<AlbumResponse>>() {

            @Override
            protected List<AlbumResponse> doInBackground(List<PerformanceResponse.Performance>... params) {
                List<AlbumResponse> responses = new ArrayList<>();
                for(PerformanceResponse.Performance performance : params[0]){
                    String url = URL_GET_ALBUM + performance.albumId;
                    RequestFuture<String> future =   RequestFuture.newFuture();
                    StringRequest request = new StringRequest(url,future,future);
                    RequestManager.getInstance(context.getApplicationContext()).addToRequestQueue(request);
                    try {
                        String response = future.get(2, TimeUnit.SECONDS);
                        responses.add(new AlbumResponse(response));
                    } catch (InterruptedException e) {
                        return null;
                    } catch (ExecutionException e) {
                        return null;
                    } catch (TimeoutException e) {
                        continue;
                    }
                }
                return responses;
            }

            @Override
            protected void onPostExecute(List<AlbumResponse> albumResponseList) {
                if(albumResponseList != null){
                    callback.onResponse(albumResponseList);
                }else{
                    callback.onError();
                }
            }
        }.execute(peformances);

    }

    /**
     * Requests download links to the given album id's
     * @param albumIds What the user wants to download
     * @param email Who to send it to
     * @param callback
     */
    public void requestDownloads(final List<Integer> albumIds, final String email, final Callback<DownloadResponse> callback){
        String url = URL_GET_ALBUM;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponse(new DownloadResponse(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("json", new JSONArray(albumIds).toString());
                return params;
            }
        };
        RequestManager.getInstance(context.getApplicationContext()).addToRequestQueue(request);
    }

    public interface Callback<T>{
        void onResponse(T response);
        void onError();
    }
}
