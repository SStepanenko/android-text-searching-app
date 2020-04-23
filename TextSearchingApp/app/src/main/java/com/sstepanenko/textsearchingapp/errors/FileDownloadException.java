package com.sstepanenko.textsearchingapp.errors;

import androidx.annotation.NonNull;

public final class FileDownloadException extends RuntimeException {

    private final ErrorType mErrorType;

    public FileDownloadException(@NonNull String message, @NonNull ErrorType errorType) {
        super(message);
        mErrorType = errorType;
    }

    public FileDownloadException(@NonNull String message, @NonNull Throwable cause, @NonNull ErrorType errorType) {
        super(message, cause);
        mErrorType = errorType;
    }

    @NonNull
    public ErrorType getErrorType() {
        return mErrorType;
    }

    public enum ErrorType {
        INVALID_URL_FORMAT,
        CREATE_DOWNLOADS_FOLDER_ERROR,
        DELETE_DOWNLOADED_FILE_ERROR,
        OPEN_URL_STREAM_ERROR,
        OPEN_OUTPUT_FILE_ERROR,
        OPEN_OUTPUT_FILE_SECURITY_ERROR,
        READ_URL_DATA_ERROR,
        WRITE_URL_DATA_ERROR,
        UNKNOWN_ERROR
    }
}
