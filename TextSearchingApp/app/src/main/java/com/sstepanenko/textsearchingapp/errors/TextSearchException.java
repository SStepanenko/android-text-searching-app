package com.sstepanenko.textsearchingapp.errors;

import androidx.annotation.NonNull;

public final class TextSearchException extends RuntimeException {

    private final ErrorType mErrorType;

    public TextSearchException(@NonNull String message, @NonNull ErrorType errorType) {
        super(message);
        mErrorType = errorType;
    }

    public TextSearchException(@NonNull String message, @NonNull Throwable cause, @NonNull ErrorType errorType) {
        super(message, cause);
        mErrorType = errorType;
    }

    @NonNull
    public ErrorType getErrorType() {
        return mErrorType;
    }

    public enum ErrorType {
        FILE_NOT_FOUND_ERROR,
        FILE_READ_ERROR,
        EMPTY_SEARCH_FILTER_ERROR,
        UNKNOWN_ERROR
    }
}
