package com.yizhen.shop.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.util.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by lewis on 16/6/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final String WHERE = "where";
    public Context bContext;
    protected String where;
    //private View emptyView;

    //布局文件ID
    protected abstract int getContentViewId();

    //获取Intent
    protected void handleIntent(Intent intent) {

    }

    /*//检验是否登录
    protected void checkLogin() {
        if (!UserManager.isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
        }
    }*/

    protected abstract void initView(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bContext = this;
        EventBus.getDefault().register(this);
        /*if (!BuildConfig.DEBUG) {
            PgyCrashManager.register(this);
        }*/
        setContentView(getContentViewId());

        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        initView(savedInstanceState);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        //PgyFeedbackShakeManager.setShakingThreshold(1000);

        // 以对话框的形式弹出
        //PgyFeedbackShakeManager.register(MainActivity.this);

        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        //FeedbackActivity.setBarImmersive(true);
        //PgyFeedbackShakeManager.register(this, false);
    }

    @Override
    protected void onPause() {
        //PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //DemoHelper.getInstance().setAppBadge();
    }

    @Override
    protected void onDestroy() {
        //取消这个界面的网络请求
        CallServer.getRequestInstance().cancelBySign(this);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        /*if (!BuildConfig.DEBUG) {
            PgyCrashManager.unregister();
        }*/
    }

    public void Toast(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        T.showShort(this, text);
    }

   /* private View getEmptyView(RecyclerView rv) {
        if (emptyView == null) {
            return LayoutInflater.from(this).inflate(R.layout.empty_view, (ViewGroup) rv.getParent(), false);
        }
        return emptyView;
    }*/

    @Subscribe
    public void onEventMainThread(Boolean b) {
    }
/*
    public void showInfoDialog(String text) {
        showInfoDialog("", text, null, true);
    }

    public void showInfoDialog(String title, String msg, PromptDialog.OnPositiveListener onPositiveListener, boolean canCancel) {
        showPromptDialog(PromptDialog.DIALOG_TYPE_INFO, title, msg, onPositiveListener, canCancel);
    }

    public void showWrongDialog(String title, String msg, PromptDialog.OnPositiveListener onPositiveListener, boolean canCancel) {
        showPromptDialog(PromptDialog.DIALOG_TYPE_WRONG, title, msg, onPositiveListener, canCancel);
    }

    public void showWrongDialog(String text) {
        showWrongDialog("", text, null, true);
    }

    public void showSuccessDialog(String text) {
        showSuccessDialog(text, null, true);
    }

    public void showSuccessDialog(String text, PromptDialog.OnPositiveListener onPositiveListener, boolean canCancel) {
        showPromptDialog(PromptDialog.DIALOG_TYPE_SUCCESS, "", text, onPositiveListener, canCancel);
    }

    public void showPromptDialog(int type, String title, String msg, PromptDialog.OnPositiveListener onPositiveListener, boolean canCancel) {
        if (onPositiveListener == null) {
            onPositiveListener = new PromptDialog.OnPositiveListener() {
                @Override
                public void onClick(PromptDialog dialog) {
                    dialog.dismiss();
                }
            };
        }
        PromptDialog promptDialog = new PromptDialog(this);
        promptDialog.setDialogType(type);
        promptDialog.setAnimationEnable(true);
        promptDialog.setTitleText(title);
        promptDialog.setContentText(msg);
        promptDialog.setPositiveListener("OK", onPositiveListener);
        promptDialog.setCancelable(canCancel);
        promptDialog.setCanceledOnTouchOutside(canCancel);
        promptDialog.show();
    }*/
}
