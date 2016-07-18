package com.mylovemhz.floordoorrecords.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStore {

    public static final String TAG = "floordoor_preferences";

    private static LocalStore instance;
    private Context context;

    private LocalStore(Context context){
        this.context = context.getApplicationContext();
    }

    public static LocalStore with(Context context){
        if(instance == null){
            instance = new LocalStore(context);
        }
        return instance;
    }

    public String getLastUsedEmail(){
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString("last_email", "");
    }

    public void setLastUsedEmail(String email){
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_email", email);
        editor.apply();
    }
}
