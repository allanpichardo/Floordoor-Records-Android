package com.mylovemhz.floordoorrecords.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class AlbumResponse implements Parcelable{

    private boolean isSuccess = false;
    private String artist;
    private String title;
    private String imageUrl;

    public AlbumResponse(String response){
        try {
            JSONObject data = new JSONObject(response);
            JSONObject content = data.getJSONObject("content");
            isSuccess = data.getBoolean("execution") && content.has("title");
            artist = content.getString("artist");
            title = content.getString("title");
            imageUrl = content.getString("image_url");
        } catch (JSONException e) {
            isSuccess = false;
        }
    }

    protected AlbumResponse(Parcel in) {
        isSuccess = in.readInt() == 1;
        artist = in.readString();
        title = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<AlbumResponse> CREATOR = new Creator<AlbumResponse>() {
        @Override
        public AlbumResponse createFromParcel(Parcel in) {
            return new AlbumResponse(in);
        }

        @Override
        public AlbumResponse[] newArray(int size) {
            return new AlbumResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isSuccess ? 1 : 0);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(imageUrl);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
