package com.yizhen.shop.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;

import static android.R.attr.fragment;

public class MyRefundListActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_refund_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getSupportFragmentManager().beginTransaction().add(R.id.content, OrderListFragment.newInstance(5)).commit();
    }

}
