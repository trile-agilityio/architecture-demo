package com.architecture.component.di;

import com.architecture.component.ui.activity.repo.RepoFragment;
import com.architecture.component.ui.activity.search.SearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract RepoFragment contributeRepoFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();
}