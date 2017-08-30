package com.yizhen.shop.home;

import android.os.Bundle;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.util.DataMaker;

import static com.yizhen.shop.R.id.rv;

/**
 * Created by lewis on 2017/7/21.
 */

public class HomeFragment2 extends BaseFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //Logger.e("initView2");
    }
    @Override
    protected void loadData() {
        super.loadData();
    }
    @Override
    public int getNum(){
        return 2;
    }
}
