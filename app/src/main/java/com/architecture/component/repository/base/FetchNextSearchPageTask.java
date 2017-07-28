package com.architecture.component.repository.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.architecture.component.db.database.GithubDb;
import com.architecture.component.db.entity.SearchResult;
import com.architecture.component.service.api.IGithubApi;
import com.architecture.component.service.base.ResponseApi;
import com.architecture.component.service.response.SearchResponse;
import com.architecture.component.util.common.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
public class FetchNextSearchPageTask implements Runnable {

    private final MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    private final String query;
    private final IGithubApi githubApi;
    private final GithubDb db;

    FetchNextSearchPageTask(String query, IGithubApi githubApi, GithubDb db) {
        this.query = query;
        this.githubApi = githubApi;
        this.db = db;
    }

    @Override
    public void run() {
        SearchResult current = db.repoDao().findSearchResult(query);

        if(current == null) {
            liveData.postValue(null);
            return;
        }

        final Integer nextPage = current.next;
        if (nextPage == null) {
            liveData.postValue(Resource.success(false));
            return;
        }

        try {
            Response<SearchResponse> response = githubApi
                    .searchRepos(query, nextPage).execute();
            ResponseApi<SearchResponse> apiResponse = new ResponseApi<>(response);

            if (apiResponse.isSuccessful()) {

                List<Integer> ids = new ArrayList<>();
                ids.addAll(current.repoIds);
                //noinspection ConstantConditions
                ids.addAll(apiResponse.body.getRepoIds());
                SearchResult merged = new SearchResult(query, ids,
                        apiResponse.body.getTotal(), apiResponse.getNextPage());

                try {
                    db.beginTransaction();
                    db.repoDao().insert(merged);
                    db.repoDao().insertRepos(apiResponse.body.getItems());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                liveData.postValue(Resource.success(apiResponse.getNextPage() != null));

            } else {
                liveData.postValue(Resource.error(apiResponse.errorMessage, true));
            }
        } catch (IOException e) {
            liveData.postValue(Resource.error(e.getMessage(), true));
        }
    }

    LiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }
}