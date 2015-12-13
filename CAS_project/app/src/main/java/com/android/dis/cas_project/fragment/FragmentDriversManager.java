package com.android.dis.cas_project.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.dis.cas_project.R;

/**
 * Created by User on 13.12.2015.
 */
public class FragmentDriversManager extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.fragment_example;

    public static FragmentDriversManager getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentDriversManager fragment = new FragmentDriversManager();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_drivers_manager));

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
