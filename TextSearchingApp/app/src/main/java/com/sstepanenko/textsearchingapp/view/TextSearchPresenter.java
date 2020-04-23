package com.sstepanenko.textsearchingapp.view;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.BuildConfig;
import com.sstepanenko.textsearchingapp.data.TextSearchData;
import com.sstepanenko.textsearchingapp.domain.TextSearchDataRepository;
import com.sstepanenko.textsearchingapp.domain.TextSearchInteractor;
import com.sstepanenko.textsearchingapp.utils.RxDisposableWrapper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TextSearchPresenter {

    private static final String TAG = TextSearchPresenter.class.getSimpleName();

    private final TextSearchData mTextSearchData;
    private final TextSearchDataRepository mTextSearchDataRepository;
    private final TextSearchInteractor mTextSearchInteractor;

    private final CompositeDisposable mCompositeDisposable;

    private RxDisposableWrapper mSearchDisposableWrapper;

    private TextSearchView mTextSearchView;

    @Inject
    public TextSearchPresenter(@NonNull TextSearchData textSearchData,
                               @NonNull TextSearchDataRepository textSearchDataRepository,
                               @NonNull TextSearchInteractor textSearchInteractor) {
        mTextSearchData = textSearchData;
        mTextSearchDataRepository = textSearchDataRepository;
        mTextSearchInteractor = textSearchInteractor;
        mCompositeDisposable = new CompositeDisposable();
        mSearchDisposableWrapper = new RxDisposableWrapper();
    }

    public void onAttachView(@NonNull TextSearchView view) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onAttachView()");
        }
        attachView(view);
        loadDataFromRepository();

        Disposable disposable = mTextSearchInteractor.observeState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setState);
        mCompositeDisposable.add(disposable);

        disposable = mTextSearchInteractor.observeFoundStrings()
                .startWith(mTextSearchInteractor.getFoundStrings())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateFoundStrings);
        mCompositeDisposable.add(disposable);

        restoreState();
    }

    public void onDetachView(@NonNull TextSearchView view) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDetachView()");
        }
        disposeSubscriptions();
        detachView(view);
        saveDataToRepository();
    }

    public void setTextFileUrl(@NonNull String textFileUrl) {
        mTextSearchData.setFileUrl(textFileUrl);
    }

    public void setSearchFilter(@NonNull String filter) {
        mTextSearchData.setSearchFilter(filter);
    }

    public void searchText() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "searchText()");
        }

        if (mTextSearchData.getFileUrl().isEmpty()) {
            getView().showEmptyUrlError();
        } else if (mTextSearchData.getSearchFilter().isEmpty()) {
            getView().showEmptySearchFilterError();
        } else {
            startSearching();
        }
    }

    private void attachView(@NonNull TextSearchView view) {
        mTextSearchView = view;
    }

    private void detachView(@NonNull TextSearchView view) {
        mTextSearchView = null;
    }

    private TextSearchView getView() {
        return mTextSearchView;
    }

    private void setState(@NonNull TextSearchInteractor.State state) {
        getView().setState(state);
    }

    private void loadDataFromRepository() {
        mTextSearchDataRepository.load(mTextSearchData);
        getView().setData(mTextSearchData);
    }

    private void saveDataToRepository() {
        mTextSearchDataRepository.save(mTextSearchData);
    }

    private void showError(@NonNull Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "showError()");
        }
        getView().showError(throwable);
    }

    private void disposeSubscriptions() {
        mCompositeDisposable.clear();
        mSearchDisposableWrapper.dispose();
    }

    private void startSearching() {
        Disposable disposable = mTextSearchInteractor.search()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateFoundStrings, this::showError);
        mSearchDisposableWrapper.set(disposable);
    }

    private void updateFoundStrings(@NonNull List<String> foundStrings) {
        getView().updateFoundStrings(foundStrings);
    }

    private void restoreState() {
        if (mTextSearchInteractor.getState().isProgress()) {
            startSearching();
        }
    }
}
