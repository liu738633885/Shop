package com.yizhen.shop.user;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yizhen.shop.MainActivity;
import com.yizhen.shop.MainApplication;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.util.SystemUtils;

public class SplashActivity extends BaseActivity {
    private TextView tv_info;
    private Handler handler = new Handler();
    private static final int sleepTime = 1000;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_info.setText("版本:" + SystemUtils.getAppVersionName(this) + (MainApplication.getInstance().getResources().getInteger(R.integer.HTTP_CONFIG) == 2 ? "测试服务器" : ""));
        openMainActivityWait(sleepTime);
    }
    private void openMainActivityWait(int millis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, millis);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, millis + 1000);
    }
}
