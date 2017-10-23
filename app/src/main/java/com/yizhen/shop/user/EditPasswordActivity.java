package com.yizhen.shop.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MD5Util;
import com.yizhen.shop.widgets.TitleBar;

public class EditPasswordActivity extends BaseActivity {
    private TitleBar titleBar;
    private EditText edt_old_password, edt_new_password, edt_new_password2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPassword();
            }
        });
        edt_old_password = (EditText) findViewById(R.id.edt_old_password);
        edt_new_password = (EditText) findViewById(R.id.edt_new_password);
        edt_new_password2 = (EditText) findViewById(R.id.edt_new_password2);
    }

    private void editPassword() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.UPDATE_USER_PASSWORD);
        if (TextUtils.isEmpty(edt_old_password.getText().toString())) {
            Toast(" 请先输入旧密码");
            return;
        }
        if (edt_new_password.getText().toString().length() < 6) {
            Toast("新密码最少6位");
            return;
        }

        if (!edt_new_password2.getText().toString().equals(edt_new_password.getText().toString())) {
            Toast("密码不一致");
            return;
        }
        /*request.add("password", MD5Util.MD5String(edt_old_password.getText().toString()));
        request.add("new_password", MD5Util.MD5String(edt_new_password.getText().toString()));*/
        request.add("password", edt_old_password.getText().toString());
        request.add("new_password", edt_new_password.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);

    }
}
