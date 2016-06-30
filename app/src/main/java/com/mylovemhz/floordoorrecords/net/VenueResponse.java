package com.mylovemhz.floordoorrecords.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class VenueResponse implements Parcelable{

    private boolean isSuccess = true;
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public VenueResponse(String response){
        try {
            JSONObject data = new JSONObject(response);
            JSONObject content = data.getJSONObject("content");
            isSuccess = data.getBoolean("execution") && data.getJSONObject("content").has("name");
            id = content.getInt("id");
            name = content.getString("name");
            latitude = content.getDouble("latitude");
            longitude = content.getDouble("longitude");
        } catch (JSONException e) {
            isSuccess = false;
        }
    }

    public VenueResponse(Parcel in){
        isSuccess = in.readInt() == 1;
        id = in.readInt();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isSuccess ? 1 : 0);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<VenueResponse> CREATOR
            = new Parcelable.Creator<VenueResponse>(){

        @Override
        public VenueResponse createFromParcel(Parcel source) {
            return new VenueResponse(source);
        }

        @Override
        public VenueResponse[] newArray(int size) {
            return new VenueResponse[0];
        }
    };
}
