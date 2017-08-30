package com.yizhen.shop.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.widgets.TitleBar;

public class TopicActivity extends BaseActivity {
    private int topic_id;
    private String topic_name;
    private TitleBar titleBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_topic;
    }

    public static void goTo(Context context, int topic_id, String topic_name) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra("topic_id", topic_id);
        intent.putExtra("topic_name", topic_name);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        topic_id = intent.getIntExtra("topic_id", 0);
        topic_name = intent.getStringExtra("topic_name");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (!TextUtils.isEmpty(topic_name)) {
            titleBar.setCenterText(topic_name);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.content, TopicFragment.newInstance(topic_id)).commit();
    }
}
