package com.architecture.component.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.architecture.component.db.entity.Repo;
import com.architecture.component.repository.RepoRepository;
import com.architecture.component.util.common.AbsentLiveData;
import com.architecture.component.util.common.LoadMoreState;
import com.architecture.component.util.common.NextPageHandler;
import com.architecture.component.util.common.Resource;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final LiveData<Resource<List<Repo>>> results;
    private final NextPageHandler nextPageHandler;

    @Inject
    public SearchViewModel(RepoRepository repoRepository) {
        nextPageHandler = new NextPageHandler(repoRepository);

        results = Transformations.switchMap(query, search -> {
            if (search == null || search.trim().length() == 0) {
                return AbsentLiveData.create();
            } else {
                return repoRepository.search(search);
            }
        });
    }

    /**
     * Set string query.
     *
     * @param originalInput The query
     */
    public void setQuery(@NonNull String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if (Objects.equals(input, query.getValue())) {
            return;
        }

        nextPageHandler.reset();
        query.setValue(input);
    }

    /**
     * Get results.
     *
     * @return
     */
    public LiveData<Resource<List<Repo>>> getResults() {
        return results;
    }

    /**
     * Get load more status sate.
     *
     * @return
     */
    public LiveData<LoadMoreState> getLoadMoreState() {
        return nextPageHandler.getLoadMoreState();
    }

    /**
     * Load next page.
     */
    public void loadNextPage() {
        String value = query.getValue();
        if (value == null || value.trim().length() == 0) {
            return;
        }

        nextPageHandler.queryNextPage(value);
    }

    /**
     * Refresh data.
     */
    public void refresh() {
        if (query.getValue() != null) {
            query.setValue(query.getValue());
        }
    }
}