package com.architecture.component.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.architecture.component.db.dao.RepoDao;
import com.architecture.component.db.database.GithubDb;
import com.architecture.component.service.api.IGithubApi;
import com.architecture.component.ui.adapter.LiveDataCallAdapterFactory;
import com.architecture.component.util.constant.Config;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    IGithubApi provideGithubService() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(IGithubApi.class);
    }

    @Singleton
    @Provides
    GithubDb provideDb(Application app) {
        return Room.databaseBuilder(app, GithubDb.class, Config.GITHUB_DB_NAME).build();
    }

    @Singleton
    @Provides
    RepoDao provideRepoDao(GithubDb db) {
        return db.repoDao();
    }
}