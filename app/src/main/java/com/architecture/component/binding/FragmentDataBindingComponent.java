package com.architecture.component.binding;

import android.app.Fragment;
import android.databinding.DataBindingComponent;

/**
 * A Data Binding Component implementation for fragments.
 */
public class FragmentDataBindingComponent implements DataBindingComponent {
    private final FragmentBindingAdapters adapter;

    public FragmentDataBindingComponent(Fragment fragment) {
        this.adapter = new FragmentBindingAdapters(fragment);
    }

    @Override
    public FragmentBindingAdapters getFragmentBindingAdapters() {
        return adapter;
    }
}