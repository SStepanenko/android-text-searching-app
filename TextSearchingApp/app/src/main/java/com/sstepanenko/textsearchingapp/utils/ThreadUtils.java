package com.sstepanenko.textsearchingapp.utils;

import android.util.Log;

import com.sstepanenko.textsearchingapp.BuildConfig;

public final class ThreadUtils {

    private static final String TAG = ThreadUtils.class.getSimpleName();

    private ThreadUtils() {
    }

    public static void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "sleepThread() error: " + exception.toString());
            }
        }
    }
}
