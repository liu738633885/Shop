package com.yizhen.shop;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yizhen.shop.auction.AuctionHomeFragment;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.category.CategoryFragment;
import com.yizhen.shop.home.HomeFragment;
import com.yizhen.shop.model.cart.CartNum;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.push.ExampleUtil;
import com.yizhen.shop.shoppingcart.ShoppingCartFragment;
import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.user.MineFragment;
import com.yizhen.shop.util.StatusBarUtil;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.util.permissions.PermissionsManager;
import com.yizhen.shop.util.permissions.PermissionsResultAction;
import com.yizhen.shop.util.upadata.UpdataAppManager;

import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends BaseActivity {
    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private AuctionHomeFragment auctionHomeFragment;
    private MineFragment mineFragment;
    private int index;
    private int currentTabIndex;
    private Button[] mTabs;
    private Fragment[] fragments;
    private FrameLayout content;
    private View root;
    private TextView tv_shopping_car_num;
    public static boolean isForeground = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.darkMode(this);
        root = findViewById(R.id.root);
        content = (FrameLayout) findViewById(R.id.content);
        mTabs = new Button[5];
        mTabs[0] = (Button) findViewById(R.id.btn1);
        mTabs[1] = (Button) findViewById(R.id.btn2);
        mTabs[2] = (Button) findViewById(R.id.btn3);
        mTabs[3] = (Button) findViewById(R.id.btn4);
        mTabs[4] = (Button) findViewById(R.id.btn5);
        mTabs[0].setSelected(true);
        tv_shopping_car_num = (TextView) findViewById(R.id.tv_shopping_car_num);
        homeFragment = new HomeFragment();
        auctionHomeFragment = new AuctionHomeFragment();
        categoryFragment = new CategoryFragment();
        shoppingCartFragment = new ShoppingCartFragment();
        mineFragment = new MineFragment();
        fragments = new Fragment[]{homeFragment, auctionHomeFragment, categoryFragment, shoppingCartFragment, mineFragment};
        getSupportFragmentManager().beginTransaction().add(R.id.content, homeFragment).commit();
        //root.setFitsSystemWindows(true);
        requestPermissions();
        UpdataAppManager.updata(this, false, new UpdataAppManager.HaveNewCode() {
            @Override
            public void haveNewCode(boolean yes) {
                //findViewById(R.id.hasNewCode).setVisibility(yes ? View.VISIBLE : View.INVISIBLE);
            }
        });
       /* LoadingDialog dialog=new LoadingDialog(this);
        dialog.show();*/
        registerMessageReceiver();  // used for receive msg
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "权限 " + permission + "没被开启,请开启所有需要权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                index = 0;
                break;
            case R.id.btn2:
                index = 1;
                break;
            case R.id.btn3:
                index = 2;
                break;
            case R.id.btn4:
                index = 3;
                break;
            case R.id.btn5:
                if (!UserManager.isLogin()) {
                    startActivity(new Intent(bContext, LoginActivity.class));
                    return;
                }
                index = 4;
                break;
        }
        clickBottomBar(index);
    }

    private void clickBottomBar(int index) {
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.content, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }

        currentTabIndex = index;
    }

    public void setShoppingCartNum(int num) {
        if (num > 0) {
            tv_shopping_car_num.setVisibility(View.VISIBLE);
            tv_shopping_car_num.setText(num + "");
        } else {
            tv_shopping_car_num.setVisibility(View.INVISIBLE);
        }
    }

    public void get_cart_num() {
        if (UserManager.isLogin()) {
            NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CART_NUM);
            CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    //T.showShort(bContext, netBaseBean.getMessage());
                    CartNum cartNum = netBaseBean.parseObject(CartNum.class);
                    setShoppingCartNum(cartNum.num);
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }
            }, false, false);
        } else {
            setShoppingCartNum(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_cart_num();
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e != null && e.getAction().equals(EventRefresh.ACTION_LOGOUT)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickBottomBar(0);
                }
            }, 1000);

        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                T.showShort(getApplicationContext(), "再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                //System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    /*======push========*/
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.yizhen.shop.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";


    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    private void setCostomMsg(String msg) {
       /* if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }*/
    }
}
