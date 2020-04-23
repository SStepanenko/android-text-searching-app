package com.sstepanenko.textsearchingapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public final class UiUtils {

    private UiUtils() {

    }

    public static void showErrorDialog(@NonNull Context context, int titleResId, int messageResId) {
        new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(titleResId)
            .setMessage(messageResId)
            .setNeutralButton(android.R.string.ok, null)
            .show();
    }

    @Nullable
    public static View getCurrentFocus(@NonNull Fragment fragment) {
        Activity activity = fragment.getActivity();
        return activity != null ? activity.getCurrentFocus() : null;
    }

    public static void hideKeyboard(@NonNull View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
