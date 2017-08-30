package com.yizhen.shop.user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.util.EditTextUitls;

import org.greenrobot.eventbus.EventBus;

import static com.yizhen.shop.R.id.edt_username;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText edt_phone, edt_code, edt_password;
    //private EditText edt_username;
    private Button btn_getCode, btn_register;
    private ImageView imv_look;
    private TextView tv;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
        //edt_username = (EditText) findViewById(edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        btn_getCode = (Button) findViewById(R.id.btn_getCode);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        imv_look = (ImageView) findViewById(R.id.imv_look);
        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_getCode.setEnabled(s.length() == 11 && EditTextUitls.isMobile(s.toString()));
                setButtonRegister();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_getCode.setOnClickListener(this);
        edt_code.addTextChangedListener(textWatcher);
        //edt_username.addTextChangedListener(textWatcher);
        edt_password.addTextChangedListener(textWatcher);
        EditTextUitls.bindLookPasswordToImageView(edt_password, imv_look, R.mipmap.password_show, R.mipmap.password_hide);
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(this);
        SpannableStringBuilder builder = new SpannableStringBuilder(tv.getText().toString());
        ForegroundColorSpan blue = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        builder.setSpan(blue, tv.getText().length() - 6, tv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);
    }

    private void setButtonRegister() {
        //&& edt_username.length() != 0
        if (edt_phone.getText().length() == 11 && EditTextUitls.isMobile(edt_phone.getText().toString()) && edt_code.getText().length() == 6  && edt_password.length() >= 8 && edt_password.length() <= 20) {
            btn_register.setEnabled(true);
        } else {
            btn_register.setEnabled(false);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setButtonRegister();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                register();
                break;
            case R.id.tv:
                //WebViewActivity.goTo(this, Constants.WEB_AGREEMENT, "用户协议");
                break;
            case R.id.btn_getCode:
                getCode();
                break;
            default:
        }
    }

    private void register() {
        NetBaseRequest request = new NetBaseRequest(Constants.REGISTER);
        request.add("phone", edt_phone.getText().toString());
        //request.add("nickname", edt_username.getText().toString());
        //request.add("platform", "1");
        request.add("code", edt_code.getText().toString());
        //request.add("password", MD5Util.MD5String(edt_password.getText().toString()));
        request.add("password", edt_password.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REGISTER, new String[]{edt_phone.getText().toString(), edt_password.getText().toString()}, LoginActivity.class.getSimpleName()));
                    finish();
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, true, true, "");
    }

    private void getCode() {
        NetBaseRequest getCodeRequest = new NetBaseRequest(Constants.GET_REGISTER_CODE);
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
