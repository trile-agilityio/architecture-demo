package com.architecture.component.ui.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.architecture.component.R;
import com.architecture.component.ui.activity.search.SearchFragment;
import com.architecture.component.ui.base.NavigationController;

public class MainActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private final LifecycleRegistry lifecycle = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycle;
    }

    public static NavigationController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        navigationController = new NavigationController(this);

        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, searchFragment)
                .commitAllowingStateLoss();
        if (savedInstanceState == null) {
            navigationController.navigateToSearch();
        }
    }

}