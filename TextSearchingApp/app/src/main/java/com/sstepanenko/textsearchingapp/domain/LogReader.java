package com.sstepanenko.textsearchingapp.domain;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.BuildConfig;
import com.sstepanenko.textsearchingapp.jni.NativeDisposable;
import com.sstepanenko.textsearchingapp.jni.ObjectDisposedException;

public class LogReader implements NativeDisposable {

    private static final String TAG = LogReader.class.getSimpleName();

    private long mNativePtr;

    private final StringMatchListener mStringMatchListener;

    static {
        nativeClassInit();
    }

    public LogReader(@NonNull StringMatchListener listener) {
        mNativePtr = nativeCreate();
        mStringMatchListener = listener;
    }

    @Override
    public void finalize() {
        dispose();
    }

    public void setFilter(@NonNull String filter) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setFilter(): " + filter);
        }
        checkIfObjectDisposed();
        if (!nativeSetFilter(mNativePtr, filter)) {
            throw new RuntimeException("Failed to set filter");
        }
    }

    public void readLine(@NonNull String line) {
        if (!nativeReadLine(mNativePtr, line)) {
            throw new RuntimeException("Failed to read line");
        }
    }

    @Override
    public void dispose() {
        if (disposed()) {
            return;
        }
        nativeDestroy(mNativePtr);
        mNativePtr = 0;
    }

    @Override
    public boolean disposed() {
        return mNativePtr == 0;
    }

    private void checkIfObjectDisposed() {
        if (disposed()) {
            throw new ObjectDisposedException("Object is disposed: " + TAG);
        }
    }

    private void onNewStringFound(@NonNull String string) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onNewStringFound(): " + string);
        }
        mStringMatchListener.onMatch(string);
    }

    private static native void nativeClassInit();
    private static native long nativeCreate();
    private static native void nativeDestroy(long nativePtr);
    private static native boolean nativeSetFilter(long nativePtr, @NonNull String filter);

    private native boolean nativeReadLine(long nativePtr, @NonNull String line);

    public interface StringMatchListener {
        void onMatch(@NonNull String string);
    }
}
