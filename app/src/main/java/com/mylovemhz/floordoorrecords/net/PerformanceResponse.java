package com.mylovemhz.floordoorrecords.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PerformanceResponse implements Parcelable {

    private boolean isSuccess = false;
    private int[] albumIds;
    private ArrayList<Performance> performances;

    public PerformanceResponse(String response){
        performances = new ArrayList<>();
        try{
            JSONObject data = new JSONObject(response);
            JSONArray content = data.getJSONArray("content");
            isSuccess = data.getBoolean("execution") && content.length() > 0;
            albumIds = new int[content.length()];
            for(int i = 0; i < content.length(); ++i){
                int id = content.getJSONObject(i).getInt("album_id");
                performances.add(new Performance(id));
                albumIds[i] = content.getJSONObject(i).getInt("album_id");
            }
        }catch(JSONException e){
            this.isSuccess = false;
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public List<Performance> getPerformances() {
        return performances;
    }

    protected PerformanceResponse(Parcel in) {
        performances = new ArrayList<>();
        isSuccess = in.readInt() == 1;
        in.writeIntArray(albumIds);
        for (int albumId : albumIds) {
            performances.add(new Performance(albumId));
        }
    }

    public static final Creator<PerformanceResponse> CREATOR = new Creator<PerformanceResponse>() {
        @Override
        public PerformanceResponse createFromParcel(Parcel in) {
            return new PerformanceResponse(in);
        }

        @Override
        public PerformanceResponse[] newArray(int size) {
            return new PerformanceResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isSuccess ? 1 : 0);
        dest.writeIntArray(albumIds);
    }

    public class Performance{
        protected int albumId;

        protected Performance(int albumId){
            this.albumId = albumId;
        }

        public int getAlbumId() {
            return albumId;
        }
    }
}
