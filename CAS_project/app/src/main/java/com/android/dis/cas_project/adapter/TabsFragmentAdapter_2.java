package com.android.dis.cas_project.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.dis.cas_project.fragment.AbstractTabFragment;
import com.android.dis.cas_project.fragment.FragmentOrdersDriver;
import com.android.dis.cas_project.fragment.FragmentWhereAreYou;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 13.12.2015.
 */
public class TabsFragmentAdapter_2 extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter_2(Context context, FragmentManager fm) {
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
        tabs.put(0, FragmentOrdersDriver.getInstance(context));
        tabs.put(1, FragmentWhereAreYou.getInstance(context));
    }
}