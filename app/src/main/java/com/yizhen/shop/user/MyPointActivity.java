package com.yizhen.shop.user;

import android.os.Bundle;
import android.widget.TextView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.UserInfo;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.manager.UserManager;

public class MyPointActivity extends BaseActivity {
    private TextView tv_point;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_point;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_point.setText(UserManager.getPoint() + "");
        getInfo();
    }

    private void getInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_INFO);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfo userInfo = netBaseBean.parseObject(UserInfo.class);
                    UserManager.saveUserInfo(userInfo);
                    tv_point.setText(UserManager.getPoint() + "");
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
}
