package com.cybavo.example.auth.config;

import android.content.Context;

import com.cybavo.example.auth.R;

import androidx.preference.PreferenceManager;

public class Config {

    public final static String PREFKEY_ENDPOINT = "pref_endpoint";
    public final static String PREFKEY_API_CODE = "pref_api_code";


    public static String getEndpoint(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREFKEY_ENDPOINT,
                context.getString(R.string.default_endpoint));
    }

    public static String getApiCode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREFKEY_API_CODE,
                context.getString(R.string.default_api_code));
    }
}
