package com.sstepanenko.textsearchingapp.di;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.data.TextSearchData;
import com.sstepanenko.textsearchingapp.view.TextSearchFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { TextSearchModule.class })
public interface ApplicationComponent {

    @NonNull
    TextSearchData getTextSearchData();

    void inject(@NonNull TextSearchFragment textSearchFragment);
}
