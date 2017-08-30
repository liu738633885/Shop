package com.yizhen.shop.user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.util.EditTextUitls;
import com.yizhen.shop.util.MD5Util;
import com.yizhen.shop.widgets.TitleBar;

public class ForgetPasswordActivity extends BaseActivity {
    private TitleBar titleBar;
    private EditText edt_phone, edt_code, edt_password;
    private Button btn_getCode;
    private ImageView imv_look;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find_password();
            }
        });
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        btn_getCode = (Button) findViewById(R.id.btn_getCode);
        imv_look = (ImageView) findViewById(R.id.imv_look);
        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_getCode.setEnabled(s.length() == 11 && EditTextUitls.isMobile(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
        EditTextUitls.bindLookPasswordToImageView(edt_password, imv_look, R.mipmap.password_show, R.mipmap.password_hide);
    }

    private void find_password() {
        NetBaseRequest request = new NetBaseRequest(Constants.FIND_PASSWORD);
        request.add("phone", edt_phone.getText().toString());
        request.add("code", edt_code.getText().toString());
        //request.add("password", MD5Util.MD5String(edt_password.getText().toString()));
        request.add("password", edt_password.getText().toString());
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

    private void getCode() {
        NetBaseRequest getCodeRequest = new NetBaseRequest(Constants.GET_EDIT_PASSWORD_CODE);
        getCodeRequest.add("phone", edt_phone.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, getCodeRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    btn_getCode.setClickable(false);
                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            btn_getCode.setText("已发送" + millisUntilFinished / 1000 + "s");
                        }

                        public void onFinish() {
                            btn_getCode.setClickable(true);
                            btn_getCode.setText("重新发送");
                        }
                    }.start();
                } else {
                    btn_getCode.setClickable(true);
                    btn_getCode.setText("重新发送");
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, false, true);
    }
}
