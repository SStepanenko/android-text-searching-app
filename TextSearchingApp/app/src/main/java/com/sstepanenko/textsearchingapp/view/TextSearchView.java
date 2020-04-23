package com.sstepanenko.textsearchingapp.view;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.data.TextSearchData;
import com.sstepanenko.textsearchingapp.domain.TextSearchInteractor;

import java.util.List;

public interface TextSearchView {

    void setData(@NonNull TextSearchData textSearchData);

    void searchButtonClick();

    void showError(@NonNull Throwable throwable);

    void showEmptyUrlError();

    void showEmptySearchFilterError();

    void setState(@NonNull TextSearchInteractor.State state);

    void setFoundStrings(@NonNull List<String> strings);

    void updateFoundStrings(@NonNull List<String> foundStrings);
}
