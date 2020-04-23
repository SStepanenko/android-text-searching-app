package com.sstepanenko.textsearchingapp.domain;

import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface TextSearchInteractor {

    @NonNull
    Single<List<String>> search();

    @NonNull
    Observable<State> observeState();

    @NonNull
    State getState();

    @NonNull
    Observable<List<String>> observeFoundStrings();

    @NonNull
    List<String> getFoundStrings();

    enum State {
        IDLE,
        DOWNLOADING,
        SEARCHING;

        public boolean isProgress() {
            return this != IDLE;
        }
    }
}
