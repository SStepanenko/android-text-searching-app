package com.sstepanenko.textsearchingapp.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public final class DownloadedFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mUrl;
    private String mPath;

    public DownloadedFile(@NonNull String url, @NonNull String path) {
        mUrl = url;
        mPath = path;
    }

    @NonNull
    public String getUrl() {
        return mUrl;
    }

    @NonNull
    public String getPath() {
        return mPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadedFile that = (DownloadedFile) o;
        return mUrl.equals(that.mUrl) &&
                mPath.equals(that.mPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUrl, mPath);
    }
}
