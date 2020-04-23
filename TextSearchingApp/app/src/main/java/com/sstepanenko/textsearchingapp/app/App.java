package com.sstepanenko.textsearchingapp.app;

import android.app.Application;

import com.sstepanenko.textsearchingapp.di.ApplicationComponent;
import com.sstepanenko.textsearchingapp.di.DaggerApplicationComponent;
import com.sstepanenko.textsearchingapp.di.TextSearchModule;

public class App extends Application {

    private static ApplicationComponent sApplicationComponent;

    static {
        loadNativeLibrary();
    }

    public App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sApplicationComponent = DaggerApplicationComponent.builder()
                .textSearchModule(new TextSearchModule(getApplicationContext())).build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return sApplicationComponent;
    }

    private static void loadNativeLibrary() {
        System.loadLibrary("log_reader");
    }
}
