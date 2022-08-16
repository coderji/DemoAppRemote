package com.ji.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtils {
    private static final String TAG = "ThreadUtils";
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static AtomicInteger sAtomicInteger = new AtomicInteger(0);

    public static void workExecute(final Runnable runnable) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                int workCount = sAtomicInteger.incrementAndGet();
                if (workCount >= 3) {
                    LogUtils.v(TAG, "workExecute workCount:" + workCount);
                }
                runnable.run();
                sAtomicInteger.decrementAndGet();
            }
        });
    }

    public static void uiExecute(Runnable runnable) {
        sHandler.post(runnable);
    }
}
