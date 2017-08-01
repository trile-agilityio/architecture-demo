package com.architecture.component.app;

import android.app.Application;

import timber.log.BuildConfig;
import timber.log.Timber;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}