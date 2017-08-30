package com.yizhen.shop.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.order.Refund;
import com.yizhen.shop.model.order.RefundList;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.dialog.CenterDialog;

import java.util.List;

public class RefundDetailActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int order_id;
    private int order_goods_id;
    private BaseQuickAdapter<Refund, BaseViewHolder> adapter;
    private Button btn1, btn2;
    private CenterDialog dialog;
    private EditText edt1, edt2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_refund_detail;
    }

    public static void goTo(Context context, int order_id, int order_goods_id) {
        Intent intent = new Intent(context, RefundDetailActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("order_goods_id", order_goods_id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        order_id = intent.getIntExtra("order_id", 0);
        order_goods_id = intent.getIntExtra("order_goods_id", 0);
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
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_refund_apply();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_refund_detail();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Refund, BaseViewHolder>(R.layout.item_refund) {
            @Override
            protected void convert(BaseViewHolder helper, Refund item) {
                try {
                    helper.setText(R.id.tv_refund_time, DateUtils.tenLongToString(item.action_time, DateUtils.yyyyMMddHHmmss));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                helper.setText(R.id.tv_refund_action, item.action);
                helper.setVisible(R.id.refund_line, !TextUtils.isEmpty(item.refund_ext));
                helper.setVisible(R.id.tv_refund_ext, !TextUtils.isEmpty(item.refund_ext));
                helper.setText(R.id.tv_refund_ext, item.refund_ext);
                //背景
                LinearLayout ll_refund_content = helper.getView(R.id.ll_refund_content);
                TextView tv_refund_action = helper.getView(R.id.tv_refund_action);
                TextView tv_refund_ext = helper.getView(R.id.tv_refund_ext);
                View refund_line = helper.getView(R.id.refund_line);
                if (helper.getAdapterPosition() == getData().size() - 1) {
                    ll_refund_content.setBackgroundResource(R.drawable.shape_item_bg_colorprimary_light);
                    tv_refund_action.setTextColor(Color.WHITE);
                    tv_refund_ext.setTextColor(Color.WHITE);
                    refund_line.setBackgroundColor(Color.WHITE);
                } else {
                    ll_refund_content.setBackgroundResource(R.drawable.shape_item_bg);
                    tv_refund_action.setTextColor(0xff666666);
                    tv_refund_ext.setTextColor(0xff666666);
                    refund_line.setBackgroundColor(0xff666666);
                }
            }
        };
    }

    private void get_refund_detail() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_REFUND_DETAIL);
        request.add("order_id", order_id);
        request.add("order_goods_id", order_goods_id);
        CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                RefundList refundList = netBaseBean.parseObject(RefundList.class);
                List<Refund> list = refundList.list;
                adapter.setNewData(list);
                rv.smoothScrollToPosition(adapter.getData().size() - 1);
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void cancel_refund_apply() {
        new AlertDialog.Builder(this).setMessage("确认撤销退款申请?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancel_refund_apply_net();
            }
        }).setNegativeButton("取消", null).create().show();
    }

    private void cancel_refund_apply_net() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CANCEL_REFUND_APPLY);
        request.add("order_id", order_id);
        request.add("order_goods_id", order_goods_id);
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

    private void initDialog() {
        dialog = new CenterDialog(this);
        dialog.setContentView(R.layout.dialog_refund);
        ((TextView) dialog.findViewById(R.id.tv_title)).setText("上传快递单号");
        ((Button) dialog.findViewById(R.id.btn_ok)).setText("确定");
        edt1 = dialog.findViewById(R.id.edt1);
        edt2 = dialog.findViewById(R.id.edt2);
        edt1.setHint("快递名称");
        edt2.setHint("快递编号");
        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_refund_express_net(edt1.getText().toString(), edt2.getText().toString());
            }
        });

        SharedPreferences.Editor editor= getSharedPreferences("打算的", Context.MODE_PRIVATE).edit();
        editor.commit();
    }

    private void add_refund_express_net(String text1, String text2) {
        if (TextUtils.isEmpty(text1)) {
            T.showShort(bContext, "请输入快递名称");
            return;
        }
        if (TextUtils.isEmpty(text2)) {
            T.showShort(bContext, "请输入快递编号");
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_REFUND_EXPRESS);
        request.add("order_id", order_id);
        request.add("order_goods_id", order_goods_id);
        request.add("refund_express_company", text1);
        request.add("refund_shipping_no", text2);
        CallServer.getRequestInstance().add(bContext, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(bContext, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    edt1.setText("");
                    edt2.setText("");
                    dialog.dismiss();
                    onRefresh();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }


    @Override
    public void onRefresh() {
        get_refund_detail();
    }
}
