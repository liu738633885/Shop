package com.yizhen.shop.home;

import android.os.Bundle;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;

/**
 * Created by lewis on 2017/7/21.
 */

public class HomeFragment5 extends BaseFragment{
    protected  int num=5;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //Logger.e("initView5");
    }
    @Override
    protected void loadData() {
        super.loadData();
    }
    @Override
    public int getNum(){
        return 5;
    }
}
