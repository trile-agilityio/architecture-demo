package com.architecture.component.ui.adapter;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.architecture.component.R;
import com.architecture.component.databinding.RepoItemBinding;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.ui.adapter.base.DataBoundListAdapter;

import java.util.Objects;

/**
 * A RecyclerView adapter for Repositories list.
 */
public class RepoListAdapter extends DataBoundListAdapter<Repo, RepoItemBinding> {

    private final DataBindingComponent dataBindingComponent;
    private final RepoClickCallback repoClickCallback;
    private final boolean isShowFullName;

    public RepoListAdapter(DataBindingComponent dataBindingComponent, boolean showFullName,
                           RepoClickCallback repoClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.repoClickCallback = repoClickCallback;
        this.isShowFullName = showFullName;
    }

    @Override
    protected RepoItemBinding createBinding(ViewGroup parent) {
        // data binding
        RepoItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.repo_item, parent, false, dataBindingComponent);

        // show full name
        binding.setShowFullName(isShowFullName);

        // on item Repo click
        binding.getRoot().setOnClickListener(v -> {
            Repo repo = binding.getRepo();

            if (repo != null && repoClickCallback != null) {
                repoClickCallback.onClick(repo);
            }
        });

        return binding;
    }

    @Override
    protected void bind(RepoItemBinding binding, Repo item) {
        binding.setRepo(item);
    }

    @Override
    protected boolean areItemsTheSame(Repo oldItem, Repo newItem) {
        return Objects.equals(oldItem.owner, newItem.owner)
                && Objects.equals(oldItem.name, newItem.name);
    }

    @Override
    protected boolean areContentsTheSame(Repo oldItem, Repo newItem) {
        return Objects.equals(oldItem.description, newItem.description)
                && oldItem.stars == newItem.stars;
    }

    public interface RepoClickCallback {
        void onClick(Repo repo);
    }
}