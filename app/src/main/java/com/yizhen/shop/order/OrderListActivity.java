package com.yizhen.shop.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends BaseActivity {
    private String[] tabStrs = {"全部", "待付款", "待发货", "待收货", "待评价"};
    private TabLayout tabLayout;
    private List<BaseFragment> list_fragment = new ArrayList<>();
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private int position;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_order_list;
    }

    public static void goTo(Context context, int position) {
        Intent intent = new Intent(context, OrderListActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        position = intent.getIntExtra("position", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (String s : tabStrs) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        list_fragment.add(OrderListFragment.newInstance(0));
        list_fragment.add(OrderListFragment.newInstance(1));
        list_fragment.add(OrderListFragment.newInstance(2));
        list_fragment.add(OrderListFragment.newInstance(3));
        list_fragment.add(OrderListFragment.newInstance(6));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(list_fragment.size());
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return list_fragment.get(position);
            }

            @Override
            public int getCount() {
                return tabStrs.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabStrs[position];
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        Logger.e(position+"");
        viewPager.setCurrentItem(position);
    }
}
