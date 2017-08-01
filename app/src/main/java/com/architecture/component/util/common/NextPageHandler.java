package com.architecture.component.util.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.architecture.component.repository.RepoRepository;

import java.util.Objects;

public class NextPageHandler implements Observer<Resource<Boolean>> {

    @Nullable
    private LiveData<Resource<Boolean>> nextPageLiveData;

    private final MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();

    private String query;

    private final RepoRepository repository;

    @VisibleForTesting
    private boolean hasMore;

    @VisibleForTesting
    public NextPageHandler(RepoRepository repository) {
        this.repository = repository;
        reset();
    }

    /**
     * Query next page.
     *
     * @param query The {@link String}
     */
    public void queryNextPage(String query) {
        if (Objects.equals(this.query, query)) {
            return;
        }
        unregister();
        this.query = query;
        nextPageLiveData = repository.searchNextPage(query);
        loadMoreState.setValue(new LoadMoreState(true, null));
        //noinspection ConstantConditions
        nextPageLiveData.observeForever(this);
    }

    @Override
    public void onChanged(@Nullable Resource<Boolean> result) {
        if (result == null) {
            reset();
        } else {
            switch (result.status) {
                case SUCCESS:
                    hasMore = Boolean.TRUE.equals(result.data);
                    unregister();
                    loadMoreState.setValue(new LoadMoreState(false, null));
                    break;

                case ERROR:
                    hasMore = true;
                    unregister();
                    loadMoreState.setValue(new LoadMoreState(false, result.message));
                    break;
            }
        }
    }

    /**
     * Unregister
     */
    private void unregister() {
        if (nextPageLiveData != null) {
            nextPageLiveData.removeObserver(this);
            nextPageLiveData = null;
            if (hasMore) {
                query = null;
            }
        }
    }

    /**
     * Reset
     */
    public void reset() {
        unregister();
        hasMore = true;
        loadMoreState.setValue(new LoadMoreState(false, null));
    }

    /**
     * Get load more State.
     *
     * @return LoadMoreState
     */
    public MutableLiveData<LoadMoreState> getLoadMoreState() {
        return loadMoreState;
    }
}