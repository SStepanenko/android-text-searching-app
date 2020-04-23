package com.sstepanenko.textsearchingapp.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sstepanenko.textsearchingapp.BuildConfig;
import com.sstepanenko.textsearchingapp.data.DownloadedFile;
import com.sstepanenko.textsearchingapp.errors.FileDownloadException;
import com.sstepanenko.textsearchingapp.utils.IoUtils;

import java.util.Objects;

public final class DownloadService extends IntentService {

    private static final String TAG = DownloadService.class.getSimpleName();

    private static final String ACTION_DOWNLOAD_COMPLETE = TAG + "_action_download_complete";

    private static final String EXTRA_FILE_URL = "FileUrl";
    private static final String EXTRA_DOWNLOADED_FILE = "DownloadedFile";
    private static final String EXTRA_FILE_DOWNLOAD_EXCEPTION = "FileDownloadException";

    public DownloadService() {
        super(TAG);
    }

    public static void startService(@NonNull Context context, @NonNull String fileUrl) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(EXTRA_FILE_URL, fileUrl);
        context.startService(intent);
    }

    public static void registerBroadcastReceiver(@NonNull BroadcastReceiver broadcastReceiver, @NonNull Context context) {
        IntentFilter intentFilter = createIntentFilter(ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Nullable
    public static DownloadedFile getDownloadedFile(@NonNull Intent intent) {
        return (DownloadedFile) Objects.requireNonNull(intent.getExtras()).getSerializable(EXTRA_DOWNLOADED_FILE);
    }

    @Nullable
    public static FileDownloadException getFileDownloadError(@NonNull Intent intent) {
        return (FileDownloadException) Objects.requireNonNull(intent.getExtras()).getSerializable(EXTRA_FILE_DOWNLOAD_EXCEPTION);
    }

    @NonNull
    private static IntentFilter createIntentFilter(@NonNull String action) {
        IntentFilter intentFilter = new IntentFilter(action);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        return intentFilter;
    }

    @NonNull
    private static Intent createDownloadCompleteIntent() {
        Intent intent = new Intent();
        intent.setAction(ACTION_DOWNLOAD_COMPLETE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHandleIntent(): " + intent.toString());
        }
        try {
            String fileUrl = Objects.requireNonNull(intent.getStringExtra(EXTRA_FILE_URL));
            DownloadedFile downloadedFile = IoUtils.downloadFile(getApplicationContext(), fileUrl);
            sendDownloadSuccess(downloadedFile);
        } catch (FileDownloadException exception) {
            sendDownloadError(exception);
        }
    }

    private void sendDownloadSuccess(@NonNull DownloadedFile downloadedFile) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "sendDownloadSuccess(): " + downloadedFile.toString());
        }
        Intent intent = createDownloadCompleteIntent();
        intent.putExtra(EXTRA_DOWNLOADED_FILE, downloadedFile);
        sendBroadcast(intent);
    }

    private void sendDownloadError(@NonNull FileDownloadException exception) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "sendDownloadError(): " + exception.getMessage());
        }
        Intent intent = createDownloadCompleteIntent();
        intent.putExtra(EXTRA_FILE_DOWNLOAD_EXCEPTION, exception);
        sendBroadcast(intent);
    }
}
