package com.sstepanenko.textsearchingapp.utils;

import androidx.annotation.Nullable;

import io.reactivex.disposables.Disposable;

public class RxDisposableWrapper implements Disposable{

    private Disposable mDisposable;

    public RxDisposableWrapper() {
    }

    public RxDisposableWrapper(@Nullable Disposable disposable) {
        mDisposable = disposable;
    }

    @Override
    public void dispose() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    public boolean isDisposed() {
        return mDisposable == null || mDisposable.isDisposed();
    }

    @Override
    public void finalize() {
        dispose();
    }

    @Nullable
    public Disposable get() {
        return mDisposable;
    }

    public void set(@Nullable Disposable disposable) {
        dispose();
        mDisposable = disposable;
    }
}
