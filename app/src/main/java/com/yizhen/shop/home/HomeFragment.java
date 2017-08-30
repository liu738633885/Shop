package com.yizhen.shop.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.category.CategoryGoodsFragment;
import com.yizhen.shop.goods.SearchActivity;
import com.yizhen.shop.message.MessageHomeActivity;
import com.yizhen.shop.message.MessageListActivity;
import com.yizhen.shop.model.category.Category;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/7/13.
 */

public class HomeFragment extends BaseFragment {
    private TabLayout tabLayout;
    private List<BaseFragment> list_fragment = new ArrayList<>();
    private List<String> tabStrs;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private View query, message_point;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //view.setFitsSystemWindows(true);
        StatusBarUtil.setPaddingSmart(getActivity(), view);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabStrs = new ArrayList<>();
        tabStrs.add("推荐");
        for (String s : tabStrs) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        list_fragment.add(new HomeFragment1());

        //list_fragment.add(CategoryGoodsFragment.newInstance(319));
        viewPager = view.findViewById(R.id.viewPager);
        //viewPager.setOffscreenPageLimit(list_fragment.size());
        viewPager.setOffscreenPageLimit(100);
        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return list_fragment.get(position);
            }

            @Override
            public int getCount() {
                return tabStrs.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabStrs.get(position);
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        query = view.findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        message_point = view.findViewById(R.id.message_point);
        ((View) message_point.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MessageHomeActivity.class));
            }
        });
        get_goods_category_list();
    }


    /*   @Override
       public void setPrimaryItem(ViewGroup container, int position, Object object) {
           currentFragment = (MemoListFragment) object;
           super.setPrimaryItem(container, position, object);
       }*/
    private void get_goods_category_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_GOODS_CATEGORY_LIST);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Category> list = netBaseBean.parseList(Category.class);
                    if (list != null) {
                        //showList = new ArrayList<Category>();
                        for (Category category : list) {
                            if (category.level == 1) {
                                tabStrs.add(category.category_name);
                                tabLayout.addTab(tabLayout.newTab().setText(category.category_name));
                                list_fragment.add(CategoryGoodsFragment.newInstance(category.category_id));
                            }
                        }
                    }
                    //viewPager.setOffscreenPageLimit(list_fragment.size());
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

}
