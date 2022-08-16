package com.ji.utils;

import android.util.Log;

import com.ji.remotedemo.BuildConfig;

public class LogUtils {
    private static final String TAG = "RemoteDemo";

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            Log.v(TAG, tag + " - " + msg);
        }
    }

    public static void d(String tag, String msg) {
        Log.d(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(TAG, tag + " - " + msg, tr);
    }
}
