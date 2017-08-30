package com.yizhen.shop.shoppingcart;

import android.os.Bundle;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.util.StatusBarUtil;

public class ShoppingCartActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_shopping_cart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.darkMode(this);
        getSupportFragmentManager().beginTransaction().add(R.id.content, new ShoppingCartFragment()).commit();
    }
}
