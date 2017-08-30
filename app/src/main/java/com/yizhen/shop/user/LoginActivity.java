package com.yizhen.shop.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.Login;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.util.EditTextUitls;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.manager.UserManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpListenerCallback {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    private EditText edt_username, edt_password;
    private Button btn_login, btn_register, btn_forget_password;
    private ImageButton btn_delete;
    private static final String TAG = "LoginActivity";

    @Override
    protected void initView(Bundle savedInstanceState) {
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_forget_password = (Button) findViewById(R.id.btn_forget_password);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_delete = (ImageButton) findViewById(R.id.btn_delete);
        EditTextUitls.bindCleanToView(edt_username, btn_delete);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_forget_password.setOnClickListener(this);
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    login();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_username.addTextChangedListener(textWatcher);
        edt_password.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setBtnLogin();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void setBtnLogin() {
        if (edt_username.getText().length() > 0 && edt_password.getText().length() >= 8 && edt_password.getText().length() <= 20) {
            btn_login.setEnabled(true);
        } else {
            btn_login.setEnabled(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_forget_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            default:
        }
    }

    @Override
    public void onSucceed(int what, NetBaseBean netBaseBean) {
        if (netBaseBean.isSuccess()) {
            final Login login = netBaseBean.parseObject(Login.class);
            if (UserManager.saveLoginInfo(login)) {
                EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGIN));
                finish();
            }
        } else {
            T.showShort(LoginActivity.this, netBaseBean.getMessage());
        }
    }


    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
    }

    private void login() {
        if (TextUtils.isEmpty(edt_username.getText().toString())) {
            T.showShort(this, "用户名或手机号不能为空!");
            return;
        }
        if (TextUtils.isEmpty(edt_username.getText().toString())) {
            T.showShort(this, "密码不能为空!");
            return;
        }
        if (edt_username.getText().length() < 8 || edt_username.getText().length() > 20) {
            T.showShort(this, "密码长度必须为8~20之间!");
            return;
        }
        NetBaseRequest loginRequest = new NetBaseRequest(Constants.LOGIN);
        loginRequest.add("phone", edt_username.getText().toString());
        //loginRequest.add("password", MD5Util.MD5String(edt_password.getText().toString()));
        loginRequest.add("password", edt_password.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, loginRequest, this, true, true);
    }


    @Subscribe
    public void onEventMainThread(EventRefresh b) {
        if (b.getAction().equals(EventRefresh.ACTION_REGISTER) && b.getData() != null) {
            String[] strs = (String[]) b.getData();
            edt_username.setText(strs[0]);
            edt_password.setText(strs[1]);
            btn_login.performClick();
        }
    }
}
