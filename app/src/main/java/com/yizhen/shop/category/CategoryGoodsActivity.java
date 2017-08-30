package com.yizhen.shop.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.widgets.TitleBar;

public class CategoryGoodsActivity extends BaseActivity {
    private int category_id;
    private String category_name;
    private TitleBar titleBar;
    private CategoryGoodsFragment fragment;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_category_goods;
    }

       public static void goTo(Context context, int category_id, String category_name) {
        Intent intent = new Intent(context, CategoryGoodsActivity.class);
        intent.putExtra("category_id", category_id);
        intent.putExtra("category_name", category_name);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        category_id = intent.getIntExtra("category_id", 0);
        category_name = intent.getStringExtra("category_name");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (!TextUtils.isEmpty(category_name)) {
            titleBar.setCenterText(category_name);
        }
        fragment = CategoryGoodsFragment.newInstance(category_id);
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commit();
    }
}
