package com.architecture.component.ui.adapter;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.architecture.component.R;
import com.architecture.component.databinding.ItemContributorBinding;
import com.architecture.component.db.entity.Contributor;

import java.util.Objects;

public class ContributorAdapter extends DataBoundListAdapter<Contributor, ItemContributorBinding> {

    private final DataBindingComponent dataBindingComponent;

    public ContributorAdapter(DataBindingComponent dataBindingComponent) {
        this.dataBindingComponent = dataBindingComponent;
    }

    @Override
    protected ItemContributorBinding createBinding(ViewGroup parent) {

        // Binding data
        ItemContributorBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_contributor, parent, false, dataBindingComponent);

        return binding;
    }

    @Override
    protected void bind(ItemContributorBinding binding, Contributor item) {
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