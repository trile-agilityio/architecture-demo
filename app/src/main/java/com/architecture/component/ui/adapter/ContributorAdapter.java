package com.architecture.component.ui.adapter;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.architecture.component.R;
import com.architecture.component.databinding.ContributorItemBinding;
import com.architecture.component.db.entity.Contributor;
import com.architecture.component.ui.adapter.base.DataBoundListAdapter;

import java.util.Objects;

public class ContributorAdapter extends DataBoundListAdapter<Contributor, ContributorItemBinding> {

    private final DataBindingComponent dataBindingComponent;

    public ContributorAdapter(DataBindingComponent dataBindingComponent) {
        this.dataBindingComponent = dataBindingComponent;
    }

    @Override
    protected ContributorItemBinding createBinding(ViewGroup parent) {

        // Binding data
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.contributor_item, parent, false, dataBindingComponent);
    }

    @Override
    protected void bind(ContributorItemBinding binding, Contributor item) {
        binding.setContributor(item);
    }

    @Override
    protected boolean areItemsTheSame(Contributor oldItem, Contributor newItem) {
        return Objects.equals(oldItem.getLogin(), newItem.getLogin());
    }

    @Override
    protected boolean areContentsTheSame(Contributor oldItem, Contributor newItem) {
        return Objects.equals(oldItem.getAvatarUrl(), newItem.getAvatarUrl())
                && oldItem.getContributions() == newItem.getContributions();
    }
}