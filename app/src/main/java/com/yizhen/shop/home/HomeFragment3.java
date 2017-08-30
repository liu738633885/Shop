package com.yizhen.shop.home;

import android.os.Bundle;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;

/**
 * Created by lewis on 2017/7/21.
 */

public class HomeFragment3 extends BaseFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
       // Logger.e("initView3");
    }
    @Override
    protected void loadData() {
        super.loadData();
    }
    @Override
    public int getNum(){
        return 3;
    }
}
