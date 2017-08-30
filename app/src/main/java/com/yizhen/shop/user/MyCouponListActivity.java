package com.yizhen.shop.user;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MyCouponListActivity extends BaseActivity {
    private TabLayout tabLayout;
    private List<BaseFragment> list_fragment = new ArrayList<>();
    private String[] tabStrs = {"未使用", "已使用", "已过期"};
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_coupon_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String s : tabStrs) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        list_fragment.add(MyCouponListFragment.newInstance(1));
        list_fragment.add(MyCouponListFragment.newInstance(2));
        list_fragment.add(MyCouponListFragment.newInstance(3));
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
    }
}
