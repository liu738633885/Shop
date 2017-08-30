package com.yizhen.shop.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.util.BitmapUtils;
import com.yizhen.shop.util.FileUtil;
import com.yizhen.shop.util.manager.SettingsManager;
import com.yizhen.shop.util.manager.UserManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_edit_password, ll_clean, ll_reset, ll_about;
    private TextView tvCacheSize;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showCacheSize();
                    Toast("清除缓存成功");

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ll_edit_password = (LinearLayout) findViewById(R.id.ll_edit_password);
        ll_clean = (LinearLayout) findViewById(R.id.ll_clean);
        ll_reset = (LinearLayout) findViewById(R.id.ll_reset);
        ll_about = (LinearLayout) findViewById(R.id.ll_about);
        tvCacheSize = (TextView) findViewById(R.id.tvCacheSize);
        ll_edit_password.setOnClickListener(this);
        ll_clean.setOnClickListener(this);
        ll_reset.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        showCacheSize();
    }

    private void showCacheSize() {
        double size = FileUtil.getFolderSize(new File(getCacheDir(), BitmapUtils.SAVE_IMG));
        size += FileUtil.getFolderSize(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
        tvCacheSize.setText(FileUtil.getFormatSize(size));
    }


    public void logout(View view) {
        UserManager.logout();
        EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGOUT));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_password:
                startActivity(new Intent(this, EditPasswordActivity.class));
                break;
            case R.id.ll_clean:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getApplicationContext()).clearDiskCache();
                        FileUtil.deleteFolderFile(getCacheDir() + BitmapUtils.SAVE_IMG, false);
                        handler.sendEmptyMessage(1);
                    }
                }).start();

                break;
            case R.id.ll_reset:
                SettingsManager.clean();
                Toast("重置设置成功");
                break;
            case R.id.ll_about:
                WebViewActivity.goTo(this, Constants.WEB_ABOUT, "关于");
                break;
        }
    }
}
