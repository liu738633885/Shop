package com.yizhen.shop.user;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.goods.Bill;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.order.AddNewOrderActivity;
import com.yizhen.shop.util.T;
import com.yizhen.shop.widgets.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AddBillActivity extends BaseActivity {
    private Spinner spinner1, spinner2;
    private RadioButton rb1, rb2;
    private EditText edt_company_name, edt_company_code, edt_phone, edt_email;
    private List<String> bill_types;
    private TitleBar titleBar;
    private int mode = 0;//当保存后 mode=1  关闭页面

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_bill;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_bill();
            }
        });
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        edt_company_name = (EditText) findViewById(R.id.edt_company_name);
        edt_company_code = (EditText) findViewById(R.id.edt_company_code);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_email = (EditText) findViewById(R.id.edt_email);
        ((RadioGroup) rb1.getParent()).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb1) {
                    edt_company_name.setVisibility(View.GONE);
                    edt_company_code.setVisibility(View.GONE);
                } else if (i == R.id.rb2) {
                    edt_company_name.setVisibility(View.VISIBLE);
                    edt_company_code.setVisibility(View.VISIBLE);
                }
            }
        });
        get_bill_type();
    }

    private void save_bill() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.SAVE_BILL);
        request.add("type", spinner1.getSelectedItemPosition() + 1);
        if (rb1.isChecked()) {
            request.add("bill_rise_type", 1);
        } else if (rb2.isChecked()) {
            request.add("bill_rise_type", 2);
            request.add("company_name", edt_company_name.getText().toString());
            request.add("company_code", edt_company_code.getText().toString());
        } else {
            T.showShort(bContext, "请选择发票抬头");
            return;
        }
        request.add("bill_content", spinner2.getSelectedItem().toString());
        request.add("phone", edt_phone.getText().toString());
        request.add("email", edt_email.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    mode = 1;
                    get_bill_info();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void get_bill_info() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_BILL_INFO);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Bill bill = netBaseBean.parseObject(Bill.class);
                    updateUI(bill);
                    if (mode == 1) {
                        EventBus.getDefault().post(new EventRefresh(bill, AddNewOrderActivity.TAG));
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void get_bill_type() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_BILL_TYPE);
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    bill_types = netBaseBean.parseList(String.class);
                    spinner2.setAdapter(new ArrayAdapter<String>(bContext, android.R.layout.simple_spinner_item, bill_types));
                    get_bill_info();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void updateUI(Bill bill) {
        if (bill == null) {
            return;
        }
        if (bill.type == 2) {
            spinner1.setSelection(1);
        } else {
            spinner1.setSelection(0);
        }
        if (bill.bill_rise_type == 2) {
            rb2.setChecked(true);
        } else {
            rb1.setChecked(true);
        }
        if (!TextUtils.isEmpty(bill.company_name)) {
            edt_company_name.setText(bill.company_name);
        }
        if (!TextUtils.isEmpty(bill.company_code)) {
            edt_company_code.setText(bill.company_code);
        }
        if (!TextUtils.isEmpty(bill.bill_content) && bill_types != null && bill_types.size() > 0) {
            for (int i = 0; i < bill_types.size(); i++) {
                if (bill_types.get(i).equals(bill.bill_content)) {
                    spinner2.setSelection(i);
                }
            }
        }
        if (!TextUtils.isEmpty(bill.phone)) {
            edt_phone.setText(bill.phone);
        }
        if (!TextUtils.isEmpty(bill.email)) {
            edt_email.setText(bill.email);
        }
    }
}
