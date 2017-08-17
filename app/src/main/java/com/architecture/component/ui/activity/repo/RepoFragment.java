package com.architecture.component.ui.activity.repo;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.architecture.component.R;
import com.architecture.component.binding.FragmentDataBindingComponent;
import com.architecture.component.databinding.RepoFragmentBinding;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.ui.activity.MainActivity;
import com.architecture.component.ui.adapter.ContributorAdapter;
import com.architecture.component.util.common.AutoClearedValue;
import com.architecture.component.util.common.Resource;
import com.architecture.component.viewmodel.RepoViewModel;

import java.util.Collections;

public class RepoFragment extends Fragment implements LifecycleRegistryOwner {

    private static final String REPO_OWNER_KEY = "repo_owner";
    private static final String REPO_NAME_KEY = "repo_name";

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private RepoViewModel repoViewModel;

    public DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private AutoClearedValue<RepoFragmentBinding> binding;
    private AutoClearedValue<ContributorAdapter> adapter;

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RepoFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.repo_fragment, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataBinding.setRetryCallback(() -> repoViewModel.retry());
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Init RepoViewModel
        repoViewModel = ViewModelProviders.of(this)
                .get(RepoViewModel.class);

        // get Arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey(REPO_OWNER_KEY)
                && args.containsKey(REPO_NAME_KEY)) {

            repoViewModel.setId(args.getString(REPO_OWNER_KEY),
                    args.getString(REPO_NAME_KEY));
        } else {
            repoViewModel.setId(null, null);
        }

        // Load list Repositories
        LiveData<Resource<Repo>> repo = repoViewModel.getRepo();
        repo.observe(this, resource -> {
            binding.get().setRepo(resource == null ? null : resource.data);
            binding.get().setRepoResource(resource);
            binding.get().executePendingBindings();
        });

        // Contributor adapter
        ContributorAdapter adapter = new ContributorAdapter(dataBindingComponent);
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().contributorList.setAdapter(adapter);

        // Contributors list
        initContributorList(repoViewModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize Contributors list.
     *
     * @param viewModel The {@link RepoViewModel}
     */
    private void initContributorList(RepoViewModel viewModel) {
        viewModel.getContributors().observe(this, contributors -> {
            if (contributors != null && contributors.data != null) {
                adapter.get().replace(contributors.data);
            } else {
                //noinspection ConstantConditions
                adapter.get().replace(Collections.emptyList());
            }
        });
    }

    /**
     * Create fragment.
     *
     * @param owner The {@link String}
     * @param name The {@link String}
     * @return RepoFragment
     */
    public static RepoFragment create(String owner, String name) {
        RepoFragment repoFragment = new RepoFragment();
        Bundle args = new Bundle();
        args.putString(REPO_OWNER_KEY, owner);
        args.putString(REPO_NAME_KEY, name);
        repoFragment.setArguments(args);
        return repoFragment;
    }
}