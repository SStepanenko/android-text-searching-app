package com.sstepanenko.textsearchingapp.view;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.sstepanenko.textsearchingapp.R;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TextSearchFragment.newInstance())
                    .commit();
        }
    }
}
