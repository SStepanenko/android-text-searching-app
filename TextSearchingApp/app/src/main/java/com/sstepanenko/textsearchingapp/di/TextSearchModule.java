package com.sstepanenko.textsearchingapp.di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.data.TextSearchData;
import com.sstepanenko.textsearchingapp.domain.TextSearchDataRepository;
import com.sstepanenko.textsearchingapp.domain.TextSearchDataRepositoryImpl;
import com.sstepanenko.textsearchingapp.domain.TextSearchInteractor;
import com.sstepanenko.textsearchingapp.domain.TextSearchInteractorImpl;
import com.sstepanenko.textsearchingapp.view.TextSearchPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TextSearchModule {

    // Application Context.
    private final Context mContext;

    public TextSearchModule(@NonNull Context context) {
        mContext = context;
    }

    @Provides
    @NonNull
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    @NonNull
    TextSearchData provideTextSearchData() {
        return new TextSearchData();
    }

    @Provides
    @Singleton
    @NonNull
    TextSearchDataRepository provideTextSearchDataRepository(@NonNull Context appContext) {
        return new TextSearchDataRepositoryImpl(appContext);
    }

    // Note: presenter is singleton, this is not good in general but for this task is ok.
    @Provides
    @Singleton
    @NonNull
    TextSearchPresenter provideTextSearchPresenter(@NonNull TextSearchData textSearchData,
                                                   @NonNull TextSearchDataRepository textSearchDataRepository,
                                                   @NonNull TextSearchInteractor textSearchInteractor) {
        return new TextSearchPresenter(textSearchData, textSearchDataRepository, textSearchInteractor);
    }

    @Provides
    @Singleton
    @NonNull
    TextSearchInteractor provideTextSearchInteractor(@NonNull TextSearchData textSearchData, @NonNull Context context) {
        return new TextSearchInteractorImpl(textSearchData, context);
    }
}
