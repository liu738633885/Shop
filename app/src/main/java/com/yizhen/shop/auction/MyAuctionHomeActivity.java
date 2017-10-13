package com.yizhen.shop.auction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;

public class MyAuctionHomeActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_auction_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    //我的参拍
    public void get_auction_ing(View v) {
        startActivity(new Intent(bContext, AuctionIngListActivity.class));
    }

    //我的获拍
    public void get_auction_success(View v) {
        startActivity(new Intent(bContext, AuctionSuccessActivity.class));
    }

    //我的保证金
    public void get_auction_margin_list(View v) {
        startActivity(new Intent(bContext, AuctionMarginListActivity.class));
    }


}
