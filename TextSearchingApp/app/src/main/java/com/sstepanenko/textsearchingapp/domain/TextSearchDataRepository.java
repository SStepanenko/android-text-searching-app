package com.sstepanenko.textsearchingapp.domain;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.data.TextSearchData;

public interface TextSearchDataRepository {

    void load(@NonNull TextSearchData textSearchData);

    void save(@NonNull TextSearchData textSearchData);
}
