package com.architecture.component.ui.activity.search;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingComponent;

import com.architecture.component.binding.FragmentDataBindingComponent;
import com.architecture.component.ui.base.NavigationController;

import javax.inject.Inject;

public class SearchFragment extends LifecycleFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);


}
