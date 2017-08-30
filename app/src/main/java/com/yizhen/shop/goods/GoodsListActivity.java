package com.yizhen.shop.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.widgets.TitleBar;

public class GoodsListActivity extends BaseActivity {
    private String title;
    private String url;
    private TitleBar titleBar;
    private GoodsListFragment fragment;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_goods_list;
    }

    public static void goTo(Context context, String title, String url) {
        Intent intent = new Intent(context, GoodsListActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (!TextUtils.isEmpty(title)) {
            titleBar.setCenterText(title);
        }
        fragment = GoodsListFragment.newInstance(url);
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commit();
    }
}
