package com.architecture.component.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.architecture.component.db.entity.Contributor;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.repository.RepoRepository;
import com.architecture.component.util.common.AbsentLiveData;
import com.architecture.component.util.common.Resource;

import java.util.List;
import java.util.Objects;

public class RepoViewModel extends AndroidViewModel {

    @VisibleForTesting
    private MutableLiveData<RepoId> repoId;
    private LiveData<Resource<Repo>> repo;
    private LiveData<Resource<List<Contributor>>> contributors;
    private RepoRepository repoRepository;

    public RepoViewModel(Application application) {
        super(application);

        repoRepository = new RepoRepository();
        this.repoId = new MutableLiveData<>();

        // Load repositories
        repo = Transformations.switchMap(repoId, input->{
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }
            return repoRepository.loadRepository(input.owner, input.name);
        });

        // Load Contributors
        contributors = Transformations.switchMap(repoId, input->{
            if (input.isEmpty()) {
                return AbsentLiveData.create();
            }

            return repoRepository.loadContributors(input.owner, input.name);
        });
    }

    /**
     * Get Repository data.
     */
    public LiveData<Resource<Repo>> getRepo() {
        return repo;
    }

    /**
     * Get list Contributors.
     */
    public LiveData<Resource<List<Contributor>>> getContributors() {
        return contributors;
    }

    /**
     * Set current data.
     */
    public void retry() {
        RepoId current = repoId.getValue();
        if (current != null && !current.isEmpty()) {
            repoId.setValue(current);
        }
    }

    /**
     * Set Repository Id.
     *
     * @param owner The {@link String}
     * @param name  The {@link String}
     */
    public void setId(String owner, String name) {
        RepoId update = new RepoId(owner, name);
        if (Objects.equals(repoId.getValue(), update)) {
            return;
        }
        repoId.setValue(update);
    }

    @VisibleForTesting
    static class RepoId {
        public final String owner;
        public final String name;

        RepoId(String owner, String name) {
            this.owner = owner == null ? null : owner.trim();
            this.name = name == null ? null : name.trim();
        }

        boolean isEmpty() {
            return owner == null || name == null || owner.length() == 0 || name.length() == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            RepoId repoId = (RepoId) o;

            if (owner != null ? !owner.equals(repoId.owner) : repoId.owner != null) {
                return false;
            }
            return name != null ? name.equals(repoId.name) : repoId.name == null;
        }

        @Override
        public int hashCode() {
            int result = owner != null ? owner.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
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
            return (T) new RepoViewModel(mApplication);
        }
    }
}