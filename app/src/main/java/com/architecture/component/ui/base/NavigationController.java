package com.architecture.component.ui.base;

import android.support.v4.app.FragmentManager;

import com.architecture.component.R;
import com.architecture.component.ui.activity.MainActivity;

import javax.inject.Inject;

public class NavigationController {

    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToSearch() {
//        SearchFragment searchFragment = new SearchFragment();
//        fragmentManager.beginTransaction()
//                .replace(containerId, searchFragment)
//                .commitAllowingStateLoss();
    }
}