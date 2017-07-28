package com.architecture.component.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.architecture.component.db.dao.RepoDao;
import com.architecture.component.db.database.GithubDb;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.db.entity.SearchResult;
import com.architecture.component.repository.base.FetchNextSearchPageTask;
import com.architecture.component.repository.base.NetworkBoundResource;
import com.architecture.component.service.api.IGithubApi;
import com.architecture.component.service.base.ResponseApi;
import com.architecture.component.service.response.SearchResponse;
import com.architecture.component.util.common.AbsentLiveData;
import com.architecture.component.util.common.AppExecutors;
import com.architecture.component.util.common.RateLimiter;
import com.architecture.component.util.common.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class RepoRepository {

    private final GithubDb db;
    private final RepoDao repoDao;
    private final IGithubApi githubApi;
    private final AppExecutors appExecutors;
    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public RepoRepository(AppExecutors appExecutors, GithubDb db, RepoDao repoDao,
                          IGithubApi githubService) {
        this.db = db;
        this.repoDao = repoDao;
        this.githubApi = githubService;
        this.appExecutors = appExecutors;
    }

    /**
     * Load list Repositories.
     *
     * @param owner {@link String}
     * @return {@link List<Repo>}
     */
    public LiveData<Resource<List<Repo>>> loadRepositories(String owner) {

        return new NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Repo> item) {
                Timber.d("save list Repos");
                repoDao.insertRepos(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Repo> data) {
                final boolean result = data == null || data.isEmpty()
                        || repoListRateLimit.shouldFetch(owner);

                Timber.d("should fetch Repos data : " + result);
                return result;
            }

            @NonNull
            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                Timber.d("load list repos from DB");
                return repoDao.loadRepos(owner);
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<List<Repo>>> createCall() {
                Timber.d("load list Repos from api");
                return githubApi.getRepos(owner);
            }

            @Override
            protected void onFetchFailed() {
                repoListRateLimit.reset(owner);
            }
        }.asLiveData();
    }

    /**
     * Load Repository.
     *
     * @param owner The {@link String}
     * @param name The {@link String}
     * @return {@link Repo}
     */
    public LiveData<Resource<Repo>> loadRepository(String owner, String name) {

        return new NetworkBoundResource<Repo, Repo>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull Repo item) {
                Timber.d("save Repo item");
                repoDao.insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Repo data) {
                final boolean result = data == null;

                Timber.d("should fetch Repo data : " + result);
                return result;
            }

            @NonNull
            @Override
            protected LiveData<Repo> loadFromDb() {
                Timber.d("load Repo from local");
                return repoDao.loadRepo(owner, name);
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<Repo>> createCall() {
                Timber.d("load Repo data from api");
                return githubApi.getRepo(owner, name);
            }
        }.asLiveData();
    }

    /**
     * Search next page.
     *
     * @param query The String to search.
     * @return {@link Resource<Boolean>}
     */
    public LiveData<Resource<Boolean>> searchNextPage(String query) {

        FetchNextSearchPageTask fetchNextSearchPageTask = new FetchNextSearchPageTask(
                query, githubApi, db);
        appExecutors.networkIO().execute(fetchNextSearchPageTask);
        return fetchNextSearchPageTask.getLiveData();
    }

    /**
     * Search list Repositories.
     *
     * @param query The String to search.
     * @return List Repo result
     */
    public LiveData<Resource<List<Repo>>> search(String query) {
        return new NetworkBoundResource<List<Repo>, SearchResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull SearchResponse item) {
                Timber.d("saveCallResult");

                List<Integer> repoIds = item.getRepoIds();
                SearchResult SearchResult = new SearchResult(
                        query, repoIds, item.getTotal(), item.getNextPage());
                db.beginTransaction();
                try {
                    repoDao.insertRepos(item.getItems());
                    repoDao.insert(SearchResult);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Repo> data) {
                Timber.d("shouldFetch :", String.valueOf(data == null));
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                Timber.d("loadFromDb");

                return Transformations.switchMap(repoDao.search(query), searchData->{
                    if (searchData == null) {
                        return AbsentLiveData.create();
                    } else {
                        return repoDao.loadOrdered(searchData.repoIds);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<SearchResponse>> createCall() {
                Timber.d("createCall");

                return githubApi.searchRepos(query);
            }

            @Override
            protected SearchResponse processResponse(ResponseApi<SearchResponse> response) {
                Timber.d("processResponse");

                SearchResponse body = response.body;
                if (body != null) {
                    body.setNextPage(response.getNextPage());
                }
                return body;
            }
        }.asLiveData();
    }
}