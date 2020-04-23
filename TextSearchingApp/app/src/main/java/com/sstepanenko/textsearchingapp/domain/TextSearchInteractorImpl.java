package com.sstepanenko.textsearchingapp.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.BuildConfig;
import com.sstepanenko.textsearchingapp.data.DownloadedFile;
import com.sstepanenko.textsearchingapp.data.TextSearchData;
import com.sstepanenko.textsearchingapp.errors.FileDownloadException;
import com.sstepanenko.textsearchingapp.errors.TextSearchException;
import com.sstepanenko.textsearchingapp.services.DownloadService;
import com.sstepanenko.textsearchingapp.utils.IoUtils;
import com.sstepanenko.textsearchingapp.utils.RxDisposableWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.SingleSubject;

public final class TextSearchInteractorImpl implements TextSearchInteractor {

    private static final String TAG = TextSearchInteractorImpl.class.getSimpleName();

    private final TextSearchData mTextSearchData;

    private final Context mContext;

    private final LogReader mLogReader;

    private final List<String> mFoundStrings;

    private final DownloadServiceBroadcastReceiver mDownloadServiceBroadcastReceiver;

    private SingleSubject<DownloadedFile> mDownloadedFileSingleSubject;
    private SingleSubject<List<String>> mTextSearchSingleSubject;

    private final RxDisposableWrapper mSearchDisposableWrapper;

    private final BehaviorSubject<State> mStateBehaviorSubject;
    private final BehaviorSubject<List<String>> mFoundStringsBehaviorSubject;

    public TextSearchInteractorImpl(@NonNull TextSearchData textSearchData, @NonNull Context context) {
        mTextSearchData = textSearchData;
        mContext = context;

        mLogReader = new LogReader(this::onNewStringFound);

        mFoundStrings = new Vector<>();

        mDownloadServiceBroadcastReceiver = new DownloadServiceBroadcastReceiver();
        DownloadService.registerBroadcastReceiver(mDownloadServiceBroadcastReceiver, mContext);

        mSearchDisposableWrapper = new RxDisposableWrapper();

        mStateBehaviorSubject = BehaviorSubject.createDefault(State.IDLE);
        mFoundStringsBehaviorSubject = BehaviorSubject.createDefault(mFoundStrings);
    }

    @Override
    @NonNull
    public Single<List<String>> search() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "search()");
        }
        if (!getState().isProgress()) {
            clearSearchResults();
        }
        return downloadFile()
                .flatMap(file -> startSearch());
    }

    @Override
    @NonNull
    public Observable<State> observeState() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "observeState()");
        }
        return mStateBehaviorSubject;
    }

    @Override
    @NonNull
    public State getState() {
        return mStateBehaviorSubject.getValue();
    }

    @Override
    @NonNull
    public Observable<List<String>> observeFoundStrings() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "observeFoundStrings()");
        }
        return mFoundStringsBehaviorSubject;
    }

    @Override
    @NonNull
    public List<String> getFoundStrings() {
        return new ArrayList<>(mFoundStrings);
    }

    @NonNull
    private Single<DownloadedFile> downloadFile() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "downloadFile()");
        }
        DownloadedFile downloadedFile = mTextSearchData.getDownloadedFile();
        if (downloadedFile != null && downloadedFile.getUrl().equals(mTextSearchData.getFileUrl())) {
            return Single.just(downloadedFile);
        } else {
            if (getState() != State.DOWNLOADING) {
                setState(State.DOWNLOADING);
                mTextSearchData.setDownloadedFile(null);
                mDownloadedFileSingleSubject = SingleSubject.create();
                DownloadService.startService(mContext, mTextSearchData.getFileUrl());
            }
            return mDownloadedFileSingleSubject;
        }
    }

    private void onFileDownloadSuccess(@NonNull DownloadedFile downloadedFile) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onFileDownloadSuccess(): " + downloadedFile.toString());
        }
        mTextSearchData.setDownloadedFile(downloadedFile);
        setState(State.IDLE);
        mDownloadedFileSingleSubject.onSuccess(downloadedFile);
    }

    private void onFileDownloadError(@NonNull Exception exception) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onFileDownloadError(): " + exception.getMessage());
        }
        setState(State.IDLE);
        mDownloadedFileSingleSubject.onError(exception);
    }

    private void setState(@NonNull State state) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setState(): " + state);
        }
        mStateBehaviorSubject.onNext(state);
    }

    private void emitFoundStrings() {
        mFoundStringsBehaviorSubject.onNext(new ArrayList<>(mFoundStrings));
    }

    private void clearSearchResults() {
        mFoundStrings.clear();
        emitFoundStrings();
    }

    private void onFoundStringsUpdate() {
        emitFoundStrings();
    }

    private void onNewStringFound(@NonNull String string) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onNewStringFound(): " + string);
        }
        mFoundStrings.add(string);
        emitFoundStrings();
    }

    private void searchText() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "searchText()");
        }
        mFoundStrings.clear();
        onFoundStringsUpdate();

        setState(State.SEARCHING);

        File downloadedFile = IoUtils.getDownloadedFile(mContext);
        TextSearchException textSearchException = null;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(downloadedFile))) {
            mLogReader.setFilter(mTextSearchData.getSearchFilter());
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                mLogReader.readLine(string);
            }
        } catch (FileNotFoundException exception) {
            textSearchException = new TextSearchException("File not found: " + downloadedFile.getAbsolutePath(),
                    exception, TextSearchException.ErrorType.FILE_NOT_FOUND_ERROR);
        } catch (IOException exception) {
            textSearchException = new TextSearchException("File reading error: " + downloadedFile.getAbsolutePath(),
                    exception, TextSearchException.ErrorType.FILE_READ_ERROR);
        } catch (Exception exception) {
            textSearchException = new TextSearchException("Text search error",
                    exception, TextSearchException.ErrorType.UNKNOWN_ERROR);
        } finally {
            setState(State.IDLE);
        }
        if (textSearchException != null) {
            mTextSearchSingleSubject.onError(textSearchException);
        } else {
            mTextSearchSingleSubject.onSuccess(mFoundStrings);
        }
    }

    @NonNull
    private Single<List<String>> startSearch() {
        if (!getState().equals(State.SEARCHING)) {
            mTextSearchSingleSubject = SingleSubject.create();
            Disposable disposable = Completable.fromAction(this::searchText)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe();
            mSearchDisposableWrapper.set(disposable);
        }
        return mTextSearchSingleSubject;
    }

    private class DownloadServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) {
                Log.d(DownloadServiceBroadcastReceiver.class.getSimpleName(), "onReceive(): " + intent.toString());
            }
            DownloadedFile downloadedFile = DownloadService.getDownloadedFile(intent);
            if (downloadedFile != null) {
                onFileDownloadSuccess(downloadedFile);
            } else {
                FileDownloadException exception = Objects.requireNonNull(
                        DownloadService.getFileDownloadError(intent));
                onFileDownloadError(exception);
            }
        }
    }
}
