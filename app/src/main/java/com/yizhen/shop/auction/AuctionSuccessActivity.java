package com.yizhen.shop.auction;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.order.OrderListFragment;

import java.util.ArrayList;
import java.util.List;

public class AuctionSuccessActivity extends BaseActivity {
    private String[] tabStrs = {"待付款", "待发货", "待收货", "已完成"};
    private TabLayout tabLayout;
    private List<AuctionSuccessFragment> list_fragment = new ArrayList<>();
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private int position;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_auction_success;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String s : tabStrs) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        list_fragment.add(AuctionSuccessFragment.newInstance(1));
        list_fragment.add(AuctionSuccessFragment.newInstance(2));
        list_fragment.add(AuctionSuccessFragment.newInstance(3));
        list_fragment.add(AuctionSuccessFragment.newInstance(4));
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
