package com.architecture.component.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.architecture.component.db.dao.RepoDao;
import com.architecture.component.db.database.GithubDb;
import com.architecture.component.db.entity.Contributor;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.db.entity.RepoSearchResult;
import com.architecture.component.service.api.IGithubApi;
import com.architecture.component.service.base.BaseApi;
import com.architecture.component.service.base.ResponseApi;
import com.architecture.component.service.response.SearchResponse;
import com.architecture.component.util.common.AbsentLiveData;
import com.architecture.component.util.common.AppExecutors;
import com.architecture.component.util.common.FetchNextSearchPageTask;
import com.architecture.component.util.common.NetworkBoundResource;
import com.architecture.component.util.common.Resource;
import com.architecture.component.util.constant.Config;

import java.util.List;

import timber.log.Timber;

public class RepoRepository {

    private GithubDb db;
    private RepoDao repoDao;
    private IGithubApi githubApi;
    private AppExecutors appExecutors;

    public RepoRepository(Context context) {
        this.db = Room.databaseBuilder(context, GithubDb.class, Config.GITHUB_DB_NAME).build();
        this.repoDao = db.repoDao();
        this.githubApi = BaseApi.getGithubApi();
        this.appExecutors = new AppExecutors();
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
                return repoDao.load(owner, name);
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
     * Load list Contributors.
     *
     * @param owner
     * @param name
     * @return
     */
    public LiveData<Resource<List<Contributor>>> loadContributors(String owner, String name) {
        return new NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Contributor> contributors) {
                for (Contributor contributor : contributors) {
                    contributor.setRepoName(name);
                    contributor.setRepoOwner(owner);
                }

                db.beginTransaction();
                try {

                    repoDao.createRepoIfNotExists(new Repo(Repo.UNKNOWN_ID,
                            name, owner + "/" + name, "",
                            new Repo.Owner(owner, null), 0));
                    repoDao.insertContributors(contributors);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                Timber.d("received saved contributors to db");
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Contributor> data) {
                Timber.d("received contributor list from db: %s", data);
                final boolean result = data == null || data.isEmpty();

                Timber.d("should fetch Repo data : " + result);
                return result;
            }

            @NonNull
            @Override
            protected LiveData<List<Contributor>> loadFromDb() {
                Timber.d("load contributors from local");
                return repoDao.loadContributors(owner, name);
            }

            @NonNull
            @Override
            protected LiveData<ResponseApi<List<Contributor>>> createCall() {
                Timber.d("load contributors from server");
                return githubApi.getContributors(owner, name);
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
                RepoSearchResult SearchResult = new RepoSearchResult(
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