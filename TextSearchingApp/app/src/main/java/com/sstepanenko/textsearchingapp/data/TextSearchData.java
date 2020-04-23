package com.sstepanenko.textsearchingapp.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class TextSearchData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mFileUrl = "";
    private String mSearchFilter = "";

    @Nullable
    private DownloadedFile mDownloadedFile;

    public TextSearchData() {
    }

    @NonNull
    public String getFileUrl() {
        return mFileUrl;
    }

    public void setFileUrl(@NonNull String fileUrl) {
        mFileUrl = fileUrl;
    }

    @NonNull
    public String getSearchFilter() {
        return mSearchFilter;
    }

    public void setSearchFilter(@NonNull String filter) {
        mSearchFilter = filter;
    }

    @Nullable
    public DownloadedFile getDownloadedFile() {
        return mDownloadedFile;
    }

    public void setDownloadedFile(@Nullable DownloadedFile downloadedFile) {
        mDownloadedFile = downloadedFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextSearchData that = (TextSearchData) o;
        return mFileUrl.equals(that.mFileUrl) &&
                mSearchFilter.equals(that.mSearchFilter) &&
                Objects.equals(mDownloadedFile, that.mDownloadedFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mFileUrl, mSearchFilter, mDownloadedFile);
    }
}
