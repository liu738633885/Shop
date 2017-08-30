package com.yizhen.shop.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.address.Address;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.T;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AddressListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private BaseQuickAdapter<Address, BaseViewHolder> adapter;
    private TitleBar titleBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_address_list;
    }

    public static void goTo(Context context, String where) {
        Intent intent = new Intent(context, AddressListActivity.class);
        intent.putExtra(WHERE, where);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        where = intent.getStringExtra(WHERE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_view, rv);
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAddressActivity.goTo(bContext, 0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Address, BaseViewHolder>(R.layout.item_address) {
            @Override
            protected void convert(BaseViewHolder helper, final Address item) {
                helper.setText(R.id.tv_address_name, item.consigner);
                helper.setText(R.id.tv_address_phone, item.mobile);
                helper.setText(R.id.tv_address, item.province + " " + item.city + " " + item.district + " " + item.address);
                helper.setVisible(R.id.tv_address_default, item.is_default == 1);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(where)) {
                            EventBus.getDefault().post(new EventRefresh(item, where));
                            finish();
                        }
                    }
                });
                helper.getView(R.id.address_right).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddAddressActivity.goTo(bContext, item.id);
                    }
                });
                helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(bContext).setMessage("确定删除此地址么?").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.DEL_ADDRESS);
                                request.add("id", item.id);
                                CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
                                    @Override
                                    public void onSucceed(int what, NetBaseBean netBaseBean) {
                                        T.showShort(bContext, netBaseBean.getMessage());
                                        if (netBaseBean.isSuccess()) {
                                            onRefresh();
                                        }
                                    }

                                    @Override
                                    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                                    }
                                }, true, true);
                            }
                        }).setNegativeButton("取消", null).create().show();
                        return false;
                    }
                });
            }
        };
    }

    private void get_address_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_ADDRESS_LIST);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Address> list = netBaseBean.parseList(Address.class);
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Override
    public void onRefresh() {
        get_address_list();
    }


}
