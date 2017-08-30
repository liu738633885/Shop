package com.yizhen.shop.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.goods.Consult;
import com.yizhen.shop.model.goods.ConsultList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.TitleBar;
import com.yizhen.shop.widgets.dialog.CenterDialog;

import java.util.List;

public class ConsultListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int goods_id;
    private BaseQuickAdapter<Consult, BaseViewHolder> adapter;
    private TitleBar titleBar;
    private CenterDialog dialog;
    private EditText edt;
    private int pagerNum = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_consult_list;
    }

    public static void goTo(Context context, int goods_id) {
        Intent intent = new Intent(context, ConsultListActivity.class);
        intent.putExtra("goods_id", goods_id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        goods_id = intent.getIntExtra("goods_id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(bContext));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view, rv);
        initDialog();
    }

    private void initDialog() {
        dialog = new CenterDialog(this);
        dialog.setContentView(R.layout.dialog_edit_text);
        ((TextView) dialog.findViewById(R.id.tv_title)).setText("发起提问");
        ((Button) dialog.findViewById(R.id.btn_ok)).setText("确定");
        edt = dialog.findViewById(R.id.edt);
        edt.setHint("请输入您想要发起的提问");
        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_Consult(edt.getText().toString());
            }
        });
    }

    private void add_Consult(String text) {
        if (TextUtils.isEmpty(text)) {
            T.showShort(bContext, "请输入提问内容");
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_CONSULT);
        request.add("goods_id", goods_id);
        request.add("consult_content", text);
        request.add("isanonymous", 0);
        CallServer.getRequestInstance().addWithLoginToLogin(bContext, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(bContext, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    edt.setText("");
                    dialog.dismiss();
                    onRefresh();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Consult, BaseViewHolder>(R.layout.item_consult) {
            @Override
            protected void convert(BaseViewHolder helper, Consult item) {
                helper.setVisible(R.id.tv_consult_right, false);
                helper.setText(R.id.tv_wen, item.consult_content);

                if (!TextUtils.isEmpty(item.consult_reply)) {
                    helper.setText(R.id.tv_da, item.consult_reply);
                } else {
                    helper.setText(R.id.tv_da, "还没有回答");
                }
            }
        };
    }

    private void get_consult_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CONSULT_LIST);
        request.add("goods_id", goods_id);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    ConsultList consultList = netBaseBean.parseObject(ConsultList.class);
                    pagerNum = consultList.next_page;
                    List<Consult> list = consultList.list;
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Override
    public void onRefresh() {
        get_consult_list(1);
    }

    @Override
    public void onLoadMoreRequested() {
        get_consult_list(pagerNum);
    }
}