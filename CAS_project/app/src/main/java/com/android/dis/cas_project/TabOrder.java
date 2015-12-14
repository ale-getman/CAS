package com.android.dis.cas_project;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.dis.cas_project.adapter.TabsFragmentAdapter_order;

/**
 * Created by User on 14.12.2015.
 */
public class TabOrder extends AppCompatActivity{

    private static final int LAYOUT = R.layout.tab_order;
    private Toolbar toolbar;
    private ViewPager viewPager;

    public static double dol,shi;
    public static String loc_x, loc_y;
    public static String log,id,who,pas,type, address, image_url;
    public static String text_status,text_order,text_technic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        loc_x  = getIntent().getStringExtra("loc_x");
        loc_y  = getIntent().getStringExtra("loc_y");
        dol = getIntent().getDoubleExtra("dolgota", dol);
        shi = getIntent().getDoubleExtra("shirota", shi);
        log = getIntent().getStringExtra("login");
        id = getIntent().getStringExtra("id");
        text_status = getIntent().getStringExtra("status");
        text_order = getIntent().getStringExtra("name");
        text_technic = getIntent().getStringExtra("technic");
        who = getIntent().getStringExtra("who");
        pas = getIntent().getStringExtra("password");
        type = getIntent().getStringExtra("type");
        address = getIntent().getStringExtra("address");
        image_url = getIntent().getStringExtra("image_url");

        initToolbar();
        initTabs();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsFragmentAdapter_order adapter = new TabsFragmentAdapter_order(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

}
