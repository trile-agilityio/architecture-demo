package com.architecture.component.service.base;

import com.architecture.component.service.api.IGithubApi;
import com.architecture.component.ui.adapter.base.LiveDataCallAdapterFactory;
import com.architecture.component.util.constant.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseApi {

    /**
     * Initialize IGithubApi.
     *
     * @return The {@link IGithubApi}
     */
    public static IGithubApi getGithubApi() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(IGithubApi.class);
    }
}