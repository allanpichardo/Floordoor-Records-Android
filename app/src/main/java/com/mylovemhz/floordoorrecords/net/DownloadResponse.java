package com.mylovemhz.floordoorrecords.net;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadResponse {

    private boolean isSuccess = false;

    public DownloadResponse(String response){
        try {
            JSONObject data = new JSONObject(response);
            isSuccess = data.getBoolean("execution");
        } catch (JSONException e) {
            isSuccess = false;
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
