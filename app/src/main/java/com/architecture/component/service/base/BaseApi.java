package com.architecture.component.service.base;

import com.architecture.component.service.api.GithubService;
import com.architecture.component.ui.adapter.base.LiveDataCallAdapterFactory;
import com.architecture.component.util.constant.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseApi {

    /**
     * Initialize GithubService.
     *
     * @return The {@link GithubService}
     */
    public static GithubService getGithubService() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(GithubService.class);
    }
}