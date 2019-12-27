package com.cybavo.example.auth.service;

import android.content.Context;

import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.example.auth.config.Config;

public class ServiceHolder {
    private static Authenticator sInstance;
    public static Authenticator get(Context context) {
        if (sInstance == null) {
            sInstance = Authenticator.create(
                    context,
                    Config.getEndpoint(context),
                    Config.getApiCode(context));
        }
        return sInstance;
    }
}
