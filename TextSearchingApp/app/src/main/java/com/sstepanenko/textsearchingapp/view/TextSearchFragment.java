package com.sstepanenko.textsearchingapp.view;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sstepanenko.textsearchingapp.BuildConfig;
import com.sstepanenko.textsearchingapp.R;
import com.sstepanenko.textsearchingapp.app.App;
import com.sstepanenko.textsearchingapp.data.TextSearchData;
import com.sstepanenko.textsearchingapp.domain.TextSearchInteractor;
import com.sstepanenko.textsearchingapp.errors.FileDownloadException;
import com.sstepanenko.textsearchingapp.errors.TextSearchException;
import com.sstepanenko.textsearchingapp.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TextSearchFragment extends Fragment implements TextSearchView {

    private static final String TAG = TextSearchFragment.class.getSimpleName();

    private static final int LAYOUT_ID = R.layout.fragment_text_search;

    @Inject
    TextSearchPresenter mPresenter;

    private LinearLayout mProgressLayout;

    private EditText mTextFileUrlEditText;
    private EditText mSearchFilterEditText;

    private Button mSearchButton;

    private TextView mSearchResultsTextView;
    private ListView mFoundStringsListView;
    private ArrayAdapter<String> mFoundStringsAdapter;

    private TextView mProgressTextView;

    public TextSearchFragment() {
        // Required empty public constructor
    }

    public static TextSearchFragment newInstance() {
        return new TextSearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getApplicationComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreateView()");
        }
        View view = inflater.inflate(LAYOUT_ID, container, false);

        mProgressLayout = view.findViewById(R.id.progress_layout);

        mTextFileUrlEditText = view.findViewById(R.id.text_file_url_edit_text);
        addEditTextChangeListener(mTextFileUrlEditText, newText -> mPresenter.setTextFileUrl(newText));

        mSearchFilterEditText = view.findViewById(R.id.filter_edit_text);
        addEditTextChangeListener(mSearchFilterEditText, newText -> mPresenter.setSearchFilter(newText));

        mSearchButton = view.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(v -> searchButtonClick());

        mSearchResultsTextView = view.findViewById(R.id.search_results_text);

        mFoundStringsListView = view.findViewById(R.id.found_strings_list_view);
        initFoundStringsListView();

        mProgressTextView = view.findViewById(R.id.progress_text);

        setupViewToHideKeyboardOnTouchIfFocusNotInEditText(view);

        mPresenter.onAttachView(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroyView()");
        }
        mPresenter.onDetachView(this);
        super.onDestroyView();
    }

    @Override
    public void setData(@NonNull TextSearchData textSearchData) {
        mTextFileUrlEditText.setText(textSearchData.getFileUrl());
        mSearchFilterEditText.setText(textSearchData.getSearchFilter());
    }

    @Override
    public void searchButtonClick() {
        mPresenter.searchText();
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        if (throwable instanceof FileDownloadException) {
            showFileDownloadError((FileDownloadException) throwable);
        } else if (throwable instanceof TextSearchException) {
            showTextSearchError((TextSearchException)throwable);
        } else {
            showUnknownError();
        }
    }

    @Override
    public void showEmptyUrlError() {
        showError(R.string.download_error_title, R.string.download_error_empty_url);
    }

    @Override
    public void showEmptySearchFilterError() {
        showError(R.string.search_error_title, R.string.search_error_empty_filter);
    }

    @Override
    public void setState(@NonNull TextSearchInteractor.State state) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setState(): " + state);
        }
        switch (state) {
            case IDLE:
                setIdleState();
                break;
            case DOWNLOADING:
                setProgressState(R.string.state_downloading);
                break;
            case SEARCHING:
                setProgressState(R.string.state_searching);
                break;
            default:
                throw new IllegalArgumentException("Unknown value of State: " + state);
        }
    }

    @Override
    public void setFoundStrings(@NonNull List<String> strings) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "setFoundStrings()");
        }
        setSearchResultsText(strings.size());
        mFoundStringsAdapter.clear();
        mFoundStringsAdapter.addAll(strings);
        mFoundStringsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateFoundStrings(@NonNull List<String> foundStrings) {
        int currentStringsCount = mFoundStringsAdapter.getCount();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("updateFoundStrings(): %d -> %d", currentStringsCount, foundStrings.size()));
        }
        if (foundStrings.size() < currentStringsCount) {
            setFoundStrings(foundStrings);
        } else if (foundStrings.size() > currentStringsCount){
            mFoundStringsAdapter.addAll(foundStrings.subList(currentStringsCount, foundStrings.size()));
            mFoundStringsAdapter.notifyDataSetChanged();
        } else {
            setSearchResultsText(foundStrings.size());
        }
    }

    private void setSearchResultsText(int count) {
        String text = String.format(getString(R.string.search_results_label_format, count), count);
        mSearchResultsTextView.setText(text);
    }

    private void initFoundStringsListView() {
        mFoundStringsAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        mFoundStringsListView.setAdapter(mFoundStringsAdapter);
    }

    private void setProgressState(int textResId) {
        boolean enabled = false;
        mTextFileUrlEditText.setEnabled(enabled);
        mSearchFilterEditText.setEnabled(enabled);
        mSearchButton.setEnabled(enabled);
        mProgressLayout.setVisibility(View.VISIBLE);
        mProgressTextView.setText(textResId);
    }

    private void setIdleState() {
        boolean enabled = true;
        mTextFileUrlEditText.setEnabled(enabled);
        mSearchFilterEditText.setEnabled(enabled);
        mSearchButton.setEnabled(enabled);
        mProgressLayout.setVisibility(View.GONE);
    }

    private void showError(int titleResId, int messageResId) {
        UiUtils.showErrorDialog(getContext(), titleResId, messageResId);
    }

    private void setupViewToHideKeyboardOnTouchIfFocusNotInEditText(@NonNull View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View touchedView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View focusedView = UiUtils.getCurrentFocus(TextSearchFragment.this);
                    if (focusedView != null &&
                        !(touchedView instanceof EditText)) {
                        UiUtils.hideKeyboard(focusedView);
                    }
                }
                return false;
            }
        });
    }

    private void addEditTextChangeListener(@NonNull EditText editText, @NonNull OnTextChangeAction onTextChangeAction) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                onTextChangeAction.apply(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        });
    }

    private void showFileDownloadError(@NonNull FileDownloadException exception) {
        FileDownloadException.ErrorType errorType = exception.getErrorType();
        int errorMessageResId;
        switch (errorType) {
            case INVALID_URL_FORMAT:
                errorMessageResId = R.string.download_error_invalid_url_format;
                break;
            case CREATE_DOWNLOADS_FOLDER_ERROR:
                errorMessageResId = R.string.download_error_create_downloads_folder;
                break;
            case DELETE_DOWNLOADED_FILE_ERROR:
                errorMessageResId = R.string.download_error_delete_downloaded_file;
                break;
            case OPEN_URL_STREAM_ERROR:
                errorMessageResId = R.string.download_error_open_url_stream;
                break;
            case OPEN_OUTPUT_FILE_ERROR:
                errorMessageResId = R.string.download_error_open_output_file;
                break;
            case OPEN_OUTPUT_FILE_SECURITY_ERROR:
                errorMessageResId = R.string.download_error_open_output_file_security;
                break;
            case READ_URL_DATA_ERROR:
                errorMessageResId = R.string.download_error_read_url_data;
                break;
            case WRITE_URL_DATA_ERROR:
                errorMessageResId = R.string.download_error_write_url_data;
                break;
            case UNKNOWN_ERROR:
                errorMessageResId = R.string.download_error_unknown;
                break;
            default:
                throw new IllegalArgumentException("Unknown value of FileDownloadException.ErrorType: " + errorType);
        }
        showError(R.string.download_error_title, errorMessageResId);
    }

    private void showTextSearchError(@NonNull TextSearchException exception) {
        TextSearchException.ErrorType errorType = exception.getErrorType();
        int errorMessageResId;
        switch (errorType) {
            case FILE_NOT_FOUND_ERROR:
                errorMessageResId = R.string.search_error_file_not_found;
                break;
            case FILE_READ_ERROR:
                errorMessageResId = R.string.search_error_file_read;
                break;
            case EMPTY_SEARCH_FILTER_ERROR:
                errorMessageResId = R.string.search_error_empty_filter;
                break;
            case UNKNOWN_ERROR:
                errorMessageResId = R.string.search_error_unknown;
                break;
            default:
                throw new IllegalArgumentException("Unknown value of TextSearchException.ErrorType: " + errorType);
        }
        showError(R.string.search_error_title, errorMessageResId);
    }

    private void showUnknownError() {
        showError(R.string.unknown_error_title, R.string.unknown_error_message);
    }

    public interface OnTextChangeAction {
        void apply(@NonNull String newText);
    }
}
