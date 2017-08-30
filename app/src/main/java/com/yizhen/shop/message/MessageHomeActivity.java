package com.yizhen.shop.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;

public class MessageHomeActivity extends BaseActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_message_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.ll_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bContext, MessageListActivity.class));
            }
        });
    }
}
