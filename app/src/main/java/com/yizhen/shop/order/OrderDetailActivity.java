package com.yizhen.shop.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.order.Operation;
import com.yizhen.shop.model.order.Order;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.imageloader.ImageLoader;

public class OrderDetailActivity extends BaseActivity {
    private int order_id;
    private TextView tv_address_name, tv_address_phone, tv_address_default, tv_address, tv_youhui, tv_yunfei, pay_money, tv_point, tv_status_name, tv_info;
    private LinearLayout ll_goods;
    private ImageView address_right;
    private Order order;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_order_detail;
    }

    public static void goTo(Context context, int order_id) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("order_id", order_id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        order_id = intent.getIntExtra("order_id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_address_name = (TextView) findViewById(R.id.tv_address_name);
        tv_address_phone = (TextView) findViewById(R.id.tv_address_phone);
        tv_address_default = (TextView) findViewById(R.id.tv_address_default);
        tv_address_default.setVisibility(View.GONE);
        tv_address = (TextView) findViewById(R.id.tv_address);
        address_right = (ImageView) findViewById(R.id.address_right);
        address_right.setVisibility(View.INVISIBLE);
        tv_youhui = (TextView) findViewById(R.id.tv_youhui);
        tv_yunfei = (TextView) findViewById(R.id.tv_yunfei);
        pay_money = (TextView) findViewById(R.id.pay_money);
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_status_name = (TextView) findViewById(R.id.tv_status_name);
        tv_info = (TextView) findViewById(R.id.tv_info);
        ll_goods = (LinearLayout) findViewById(R.id.ll_goods);
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_order_detail();
    }

    public void onRefresh() {
        get_order_detail();
    }

    private void get_order_detail() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_ORDER_DETAIL);
        request.add("order_id", order_id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    order = netBaseBean.parseObject(Order.class);
                    updateUI();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void updateUI() {
        if (order == null) {
            return;
        }
        tv_address_name.setText(order.receiver_name);
        tv_address_phone.setText(order.receiver_mobile);
        tv_address.setText(order.receiver_address);
        tv_youhui.setText(order.coupon_name);
        tv_yunfei.setText("¥ " + order.shipping_money);
        ll_goods.removeAllViews();
        for (final Goods goods : order.goods_list) {
            View layout = LayoutInflater.from(bContext).inflate(R.layout.item_order_goods, ll_goods, false);
            ((TextView) layout.findViewById(R.id.tv_title)).setText(goods.goods_name);
            ((TextView) layout.findViewById(R.id.tv_num)).setText(goods.num + "");
            ((TextView) layout.findViewById(R.id.tv_spec)).setText(goods.sku_name);
            ((TextView) layout.findViewById(R.id.tv_price)).setText("¥ " + goods.price);
            //Button button = layout.findViewById(R.id.av);
            TextView tv_refund = layout.findViewById(R.id.tv_refund);
            tv_refund.setVisibility(View.VISIBLE);//退款文本显示
            switch (goods.refund_status) {
                case 1:
                    tv_refund.setText("买家申请退款");
                    break;
                case 2:
                    tv_refund.setText("等待买家退货");
                    break;
                case 3:
                    tv_refund.setText("等待卖家确认收货");
                    break;
                case 4:
                    tv_refund.setText("等待卖家确认退款");
                    break;
                case 5:
                    tv_refund.setText("退款已成功");
                    break;
                case -1:
                    tv_refund.setText("退款已拒绝");
                    break;
                case -2:
                    tv_refund.setText("退款已关闭");
                    break;
                case -3:
                    tv_refund.setText("退款申请不通过");
                    break;
                default:
                    //如果是0 说明还未退款
                    tv_refund.setVisibility(View.GONE);//文本隐藏
            }
            LinearLayout ll_button = layout.findViewById(R.id.ll_button);
            ll_button.removeAllViews();
            for (final Operation operation : goods.refund_operation) {
                Button button = (Button) LayoutInflater.from(bContext).inflate(R.layout.item_order_button, ll_button, false);
                button.setText(operation.name);
                button.setTextColor(Color.parseColor(operation.color));
                ll_button.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (operation.no) {
                            case "refund":
                                RefundActivity.goTo(bContext, goods, order.order_id,order.shipping_status);
                                break;
                            case "detail":
                                RefundDetailActivity.goTo(bContext, order.order_id, goods.order_goods_id);
                                break;
                            case "cancel":
                                cancel_refund_apply(order.order_id, goods.order_goods_id);
                                break;
                        }
                    }
                });
            }
            ImageLoader.loadHome(bContext, goods.goods_picture, (ImageView) layout.findViewById(R.id.imv));
            ll_goods.addView(layout);
        }
        pay_money.setText("¥ " + order.pay_money);
        tv_point.setText("获得" + order.give_point + "积分");
        String info = "";
        info += "订单编号: " + order.out_trade_no + "\n";
        info += "支付方式: " + order.payment_type_name + "\n";
        try {
            info += "下单时间: " + DateUtils.tenLongToString(order.create_time, DateUtils.yyyyMMddHHmmss);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_info.setText(info);
        tv_status_name.setText(order.status_name);
        // hideAllButton();
        LinearLayout ll_operation = (LinearLayout) findViewById(R.id.ll_operation);
        ll_operation.removeAllViews();
        for (final Operation operation : order.member_operation) {
            Button button = (Button) LayoutInflater.from(bContext).inflate(R.layout.item_order_button, ll_operation, false);
            button.setText(operation.name);
            button.setTextColor(Color.parseColor(operation.color));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (operation.no) {
                        case "pay"://支付
                            pay(order.out_trade_no);
                            break;
                        case "close"://取消订单
                            close(order.order_id);
                            break;
                        case "delete"://删除
                            delete_order(order.order_id);
                            break;
                        case "getdelivery"://确认收货
                            take_delivery(order.order_id);
                            break;
                        case "remind"://提醒发货
                            T.showShort(bContext, "没有接口");
                            break;
                        case "evaluate"://去评价
                            AddEvaluateActivity.goTo(bContext, order);
                            break;
                        case "express"://查看物流
                            WebViewActivity.goTo(bContext, Constants.WEB_EXPRESS + order.order_id, "物流信息");
                            break;
                    }
                }
            });
            if (operation.no.equals("pay")) {
                updatePayButton(button);
            }
            ll_operation.addView(button);
        }
    }

    //关闭订单
    private void close(int order_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ORDER_CLOSE);
        request.add("order_id", order_id);
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

    //删除订单
    private void delete_order(int order_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.DELETE_ORDER);
        request.add("order_id", order_id);
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

    //确认收货
    private void take_delivery(final int order_id) {
        new AlertDialog.Builder(this).setMessage("确定已收到货物" +
                "？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                take_delivery_net(order_id);
            }
        }).setNegativeButton("取消", null).show();
    }

    private void take_delivery_net(int order_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ORDER_TAKE_DELIVERY);
        request.add("order_id", order_id);
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

    //模拟付款
    private void pay(String out_trade_no) {
       /* NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.UNIFY_PAY);
        request.add("out_trade_no", out_trade_no);
        CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    T.showShort(bContext, "付款成功");
                    onRefresh();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);*/
        PayActivity.goTo(this, out_trade_no);

    }

    CountDownTimer countDownTimer;

    private void updatePayButton(final Button btn) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (order.create_time != 0 && btn.getVisibility() == View.VISIBLE) {
            countDownTimer = new CountDownTimer((order.create_time * 1000 + 60 * 60 * 1000) - System.currentTimeMillis(), 1000) {
                public void onTick(long millisUntilFinished) {
                    long t = millisUntilFinished / 1000;
                    long h = t / (60 * 60);
                    long m = (t - 60 * 60 * h) / 60;
                    long s = t - 60 * 60 * h - 60 * m;
                    String hs = h == 0 ? "" : h + ":";
                    String ms = m == 0 ? "" : m + ":";
                    String ss = (s > 9 ? s + "" : "0" + s) + ((h == 0 && m == 0) ? "s" : "");
                    btn.setText("付款 " + hs + ms + ss);
                }

                public void onFinish() {
                    btn.setText("立刻付款");
                }
            };
            countDownTimer.start();
        }
    }

    private void cancel_refund_apply(final int order_id, final int order_goods_id) {
        new AlertDialog.Builder(bContext).setMessage("确认撤销退款申请?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
        }).setNegativeButton("取消", null).create().show();

    }

}
