package com.sstepanenko.textsearchingapp.jni;

import androidx.annotation.NonNull;

public final class ObjectDisposedException extends RuntimeException {

    public ObjectDisposedException() {
        super();
    }

    public ObjectDisposedException(@NonNull String message) {
        super(message);
    }
}

