package com.ji.util;

import com.ji.remotedemo.BuildConfig;

public class Log {
    private static final String TAG = "RemoteDemo";

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG || android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            android.util.Log.v(TAG, tag + " - " + msg);
        }
    }

    public static void d(String tag, String msg) {
        android.util.Log.d(TAG, tag + " - " + msg);
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, tag + " - " + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(TAG, tag + " - " + msg, tr);
    }
}
