package com.android.dis.cas_project.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.dis.cas_project.fragment.AbstractTabFragment;
import com.android.dis.cas_project.fragment.FragmentDriversManager;
import com.android.dis.cas_project.fragment.FragmentMapsDrivers;
import com.android.dis.cas_project.fragment.FragmentOrdersManager;


import java.util.HashMap;
import java.util.Map;

public class TabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        tabs.put(0, FragmentOrdersManager.getInstance(context));
        tabs.put(1, FragmentDriversManager.getInstance(context));
        tabs.put(2, FragmentMapsDrivers.getInstance(context));
    }
}
