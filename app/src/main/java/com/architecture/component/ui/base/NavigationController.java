package com.architecture.component.ui.base;

import android.support.v4.app.FragmentManager;

import com.architecture.component.R;
import com.architecture.component.ui.activity.repo.RepoFragment;
import com.architecture.component.ui.activity.search.SearchFragment;

public class NavigationController {

    private final int containerId;
    private final FragmentManager fragmentManager;

    public NavigationController(SearchFragment searchFragment) {
        this.containerId = R.id.container;
        this.fragmentManager = searchFragment.getChildFragmentManager();
    }

    public void navigateToRepo(String owner, String name) {
        RepoFragment fragment = RepoFragment.create(owner, name);
        String tag = "repo" + "/" + owner + "/" + name;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}