package com.architecture.component.ui.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.architecture.component.R;
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

        if (savedInstanceState == null) {
            navigationController.navigateToSearch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigationController = new NavigationController(this);
    }
}