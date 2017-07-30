package com.architecture.component.service.api;

import android.arch.lifecycle.LiveData;

import com.architecture.component.db.entity.Contributor;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.service.base.ResponseApi;
import com.architecture.component.service.response.SearchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IGithubApi {

    @GET("repos/{owner}/{name}")
    LiveData<ResponseApi<Repo>> getRepo(@Path("owner") String owner,
                                        @Path("name") String name);

    @GET("users/{login}/repos")
    LiveData<ResponseApi<List<Repo>>> getRepos(@Path("login") String login);

    @GET("search/repositories")
    LiveData<ResponseApi<SearchResponse>> searchRepos(@Query("q") String query);

    @GET("search/repositories")
    Call<SearchResponse> searchRepos(@Query("q") String query, @Query("page") int page);

    @GET("repos/{owner}/{name}/contributors")
    LiveData<ResponseApi<List<Contributor>>> getContributors(@Path("owner") String owner,
                                                             @Path("name") String name);

}