package com.architecture.component.repository.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.architecture.component.service.base.ResponseApi;
import com.architecture.component.util.common.AppExecutors;
import com.architecture.component.util.common.Resource;

/**
 * A generic class that can provide a resource backed by both the SQLite database and the network.
 *
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);

            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource,
                        newData -> result.setValue(Resource.loading(newData)));
            }
        });
    }

    /**
     * Fetch data from server
     *
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {

        LiveData<ResponseApi<RequestType>> apiResponse = createCall();
        // re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource,
                newData -> result.setValue(Resource.loading(newData)));

        result.addSource(apiResponse, response -> {

            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            //noinspection ConstantConditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));

                    appExecutors.mainThread().execute(() ->
                            // specially request a new live data,
                            // otherwise will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(),
                                    newData -> result.setValue(Resource.success(newData)))
                    );
                });

            } else {
                onFetchFailed();
                result.addSource(dbSource, newData ->
                        result.setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    protected void onFetchFailed() {
    }

    public MediatorLiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ResponseApi<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<ResponseApi<RequestType>> createCall();

}