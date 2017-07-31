package com.architecture.component.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
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

public class SearchViewModel extends AndroidViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final LiveData<Resource<List<Repo>>> results;
    private final NextPageHandler nextPageHandler;
    private RepoRepository repoRepository;

    public SearchViewModel(Application application) {
        super(application);
        repoRepository = new RepoRepository();

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

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SearchViewModel(mApplication);
        }
    }
}