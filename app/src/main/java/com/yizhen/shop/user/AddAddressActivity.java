package com.yizhen.shop.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.address.Address;
import com.yizhen.shop.model.address.City;
import com.yizhen.shop.model.address.County;
import com.yizhen.shop.model.address.Province;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.T;
import com.yizhen.shop.widgets.TitleBar;
import com.yizhen.shop.widgets.dialog.AddressDialog;

public class AddAddressActivity extends BaseActivity {

    private AddressDialog dialog;
    private TextView tv_address_pcd;
    private EditText edt_address_name, edt_address_phone, edt_address;
    private TitleBar titleBar;
    private int address_id;
    private Address address;
    private CheckBox cb_address;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_address;
    }

    public static void goTo(Context context, int address_id) {
        Intent intent = new Intent(context, AddAddressActivity.class);
        intent.putExtra("address_id", address_id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        address_id = intent.getIntExtra("address_id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAddress();
            }
        });
        if (address_id == 0) {
            titleBar.setCenterText("添加地址");
        } else {
            titleBar.setCenterText("编辑地址");
        }

        tv_address_pcd = (TextView) findViewById(R.id.tv_address_pcd);
        edt_address_name = (EditText) findViewById(R.id.edt_address_name);
        edt_address_phone = (EditText) findViewById(R.id.edt_address_phone);
        edt_address = (EditText) findViewById(R.id.edt_address);
        cb_address = (CheckBox) findViewById(R.id.cb_address);
        tv_address_pcd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog == null) {
                    T.showShort(bContext, "正在加载数据,请稍后");
                } else {
                    dialog.show();
                }
            }
        });
        dialog = new AddressDialog(bContext);
        dialog.setOnOkListener(new AddressDialog.OnOkListener() {
            @Override
            public void ok(Province province, City city, County county) {
                if (address == null) {
                    address = new Address();
                }
                address.province = province.province;
                address.province_id = province.provinceCode;
                address.city = city.city;
                address.city_id = city.cityCode;
                address.district = county.county;
                address.district_id = county.countyCode;
                updateAddressUI();
            }
        });
        getAddressDetail();
    }

    private void addAddress() {
        NetBaseRequest request;
        if (address_id == 0) {
            request = RequsetFactory.creatBaseRequest(Constants.ADD_ADDRESS);
        } else {
            request = RequsetFactory.creatBaseRequest(Constants.UPDATE_ADDRESS);
            request.add("id", address_id);
        }
        request.add("consigner", edt_address_name.getText().toString());
        if (edt_address_phone.getText().toString().length() != 11) {
            T.showShort(bContext, "手机号码需为11位!");
            return;
        }
        request.add("mobile", edt_address_phone.getText().toString());
        request.add("province", address.province_id);
        request.add("city", address.city_id);
        request.add("district", address.district_id);
        request.add("address", edt_address.getText().toString());
        request.add("is_default", cb_address.isChecked() ? 1 : 0);
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void getAddressDetail() {
        if (address_id == 0) {
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_ADDRESS_DETAIL);
        request.add("id", address_id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    address = netBaseBean.parseObject(Address.class);
                    updateUI();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void updateUI() {
        if (address == null) {
            return;
        }
        edt_address_name.setText(address.consigner);
        updateAddressUI();
        edt_address_phone.setText(address.mobile);
        edt_address.setText(address.address);
        cb_address.setChecked(address.is_default == 1);
    }


    private void updateAddressUI() {
        tv_address_pcd.setText(address.province + " " + address.city + " " + address.district);
    }
}
