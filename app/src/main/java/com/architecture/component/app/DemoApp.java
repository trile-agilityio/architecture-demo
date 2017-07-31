package com.architecture.component.app;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.architecture.component.db.database.AppDatabase;

import timber.log.BuildConfig;
import timber.log.Timber;

public class DemoApp extends Application {

    public static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        appDatabase = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME).build();

    }
}