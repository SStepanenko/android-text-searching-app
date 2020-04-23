package com.sstepanenko.textsearchingapp.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.data.DownloadedFile;
import com.sstepanenko.textsearchingapp.data.TextSearchData;

public class TextSearchDataRepositoryImpl implements TextSearchDataRepository {

    private static final String TEXT_FILE_URL_KEY = "TextFileUrl";
    private static final String SEARCH_FILTER_KEY = "SearchFilter";
    private static final String DOWNLOADED_FILE_URL_KEY = "DownloadedFileUrl";
    private static final String DOWNLOADED_FILE_PATH_KEY = "DownloadedFilePath";

    private final SharedPreferences mSharedPreferences;

    public TextSearchDataRepositoryImpl(@NonNull Context appContext) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    @Override
    public void load(@NonNull TextSearchData textSearchData) {
        String fileUrl = mSharedPreferences.getString(TEXT_FILE_URL_KEY, "");
        String searchFilter = mSharedPreferences.getString(SEARCH_FILTER_KEY, "");
        String downloadedFileUrl = mSharedPreferences.getString(DOWNLOADED_FILE_URL_KEY, "");
        String downloadedFilePath = mSharedPreferences.getString(DOWNLOADED_FILE_PATH_KEY, "");
        textSearchData.setFileUrl(fileUrl);
        textSearchData.setSearchFilter(searchFilter);
        if (!downloadedFileUrl.isEmpty() && !downloadedFilePath.isEmpty()) {
            textSearchData.setDownloadedFile(new DownloadedFile(downloadedFileUrl, downloadedFilePath));
        } else {
            textSearchData.setDownloadedFile(null);
        }
    }

    public void save(@NonNull TextSearchData textSearchData) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TEXT_FILE_URL_KEY, textSearchData.getFileUrl());
        editor.putString(SEARCH_FILTER_KEY, textSearchData.getSearchFilter());
        DownloadedFile downloadedFile = textSearchData.getDownloadedFile();
        if (downloadedFile != null) {
            editor.putString(DOWNLOADED_FILE_URL_KEY, downloadedFile.getUrl());
            editor.putString(DOWNLOADED_FILE_PATH_KEY, downloadedFile.getPath());
        } else {
            editor.remove(DOWNLOADED_FILE_URL_KEY);
            editor.remove(DOWNLOADED_FILE_PATH_KEY);
        }
        editor.apply();
    }
}
