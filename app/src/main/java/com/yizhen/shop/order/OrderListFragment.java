package com.yizhen.shop.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.order.Operation;
import com.yizhen.shop.model.order.Order;
import com.yizhen.shop.model.order.OrderList;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.List;

/**
 * Created by lewis on 2017/8/3.
 */

public class OrderListFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private BaseQuickAdapter<Order, BaseViewHolder> adapter;
    private int pagerNum = 1;
    private int state;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swl_rv;
    }

    public static OrderListFragment newInstance(int state) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        state = getArguments().getInt("state");
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        rv.setAdapter(adapter);
        if (getActivity() instanceof MyRefundListActivity) {
            loadData();
        }
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Order, BaseViewHolder>(R.layout.item_order) {
            @Override
            protected void convert(BaseViewHolder helper, final Order item) {
                helper.setText(R.id.tv_status, item.status_name);
                if (state == 5) {
                    helper.setText(R.id.tv_price, "退款金额 ¥ " + item.refund_money);
                } else {
                    helper.setText(R.id.tv_price, "¥ " + item.order_money);
                }
                helper.setText(R.id.tv_order_num, "订单编号: " + item.out_trade_no);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (state == 5) {

                        } else {
                            OrderDetailActivity.goTo(getActivity(), item.order_id);
                        }
                    }
                });
                LinearLayout ll_goods = helper.getView(R.id.ll_goods);
                ll_goods.removeAllViews();
                for (final Goods goods : item.goods_list) {
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_goods, ll_goods, false);
                    layout.setBackgroundColor(Color.WHITE);
                    ((TextView) layout.findViewById(R.id.tv_title)).setText(goods.goods_name);
                    ((TextView) layout.findViewById(R.id.tv_num)).setText(goods.num + "");
                    ((TextView) layout.findViewById(R.id.tv_spec)).setText(goods.sku_name);
                    ((TextView) layout.findViewById(R.id.tv_price)).setText("¥ "+goods.price);
                    ImageLoader.loadHome(getActivity(), goods.goods_picture, (ImageView) layout.findViewById(R.id.imv));
                    //退款文本
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
                    //退款按钮
                    LinearLayout ll_button = layout.findViewById(R.id.ll_button);
                    ll_button.removeAllViews();
                    if (state != 5) {
                       /* for (final Operation operation : goods.refund_operation) {
                            Button button = (Button) LayoutInflater.from(getActivity()).inflate(R.layout.item_order_button, ll_button, false);
                            button.setText(operation.name);
                            button.setTextColor(Color.parseColor(operation.color));
                            ll_button.addView(button);
                            button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                switch (operation.no) {
                                    case "refund":
                                        RefundActivity.goTo(getActivity(), goods, item.order_id, item.shipping_status);
                                        break;
                                    case "detail":
                                        RefundDetailActivity.goTo(getActivity(), item.order_id, goods.order_goods_id);
                                        break;
                                    case "cancel":
                                        cancel_refund_apply(item.order_id, goods.order_goods_id);
                                        break;
                                }
                            }
                            });
                        }*/
                    } else {
                        Button button = (Button) LayoutInflater.from(getActivity()).inflate(R.layout.item_order_button, ll_button, false);
                        button.setText("查看详情");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RefundDetailActivity.goTo(getActivity(), item.order_id, goods.order_goods_id);
                            }
                        });
                        ll_button.addView(button);
                    }
                    //添加视图
                    ll_goods.addView(layout);
                }
                LinearLayout ll_operation = helper.getView(R.id.ll_operation);
                ll_operation.removeAllViews();
                if (state == 5) {

                } else {
                    for (final Operation operation : item.member_operation) {
                    Button button = (Button) LayoutInflater.from(getActivity()).inflate(R.layout.item_order_button, ll_operation, false);
                    button.setText(operation.name);
                    button.setTextColor(Color.parseColor(operation.color));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (operation.no) {
                                case "pay"://支付
                                    pay(item.out_trade_no);
                                    break;
                                case "close"://取消订单
                                    close(item.order_id);
                                    break;
                                case "delete"://删除
                                    delete_order(item.order_id);
                                    break;
                                case "getdelivery"://确认收货
                                    take_delivery(item.order_id);
                                    break;
                                case "remind"://提醒发货
                                    //T.showShort(getActivity(), "没有接口");
                                    remind_delivery(item.order_id);
                                    break;
                                case "evaluate"://去评价
                                    AddEvaluateActivity.goTo(getActivity(), item);
                                    break;
                                case "express"://查看物流
                                    WebViewActivity.goTo(getActivity(), Constants.WEB_EXPRESS + item.order_id, "物流信息");
                                    break;
                            }
                        }
                    });
                        ll_operation.addView(button);
                    }
                }
            }
        };
        adapter.setEmptyView(R.layout.empty_view, rv);
        adapter.setOnLoadMoreListener(this, rv);
    }

    private void remind_delivery(int order_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.REMIND_DELIVERY);
        request.add("order_id", order_id);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(getActivity(), netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
    //关闭订单
    private void close(int order_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ORDER_CLOSE);
        request.add("order_id", order_id);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(getActivity(), netBaseBean.getMessage());
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
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(getActivity(), netBaseBean.getMessage());
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
        new AlertDialog.Builder(getActivity()).setMessage("确定已收到货物？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                take_delivery_net(order_id);
            }
        }).setNegativeButton("取消", null).show();
    }

    private void take_delivery_net(int order_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ORDER_TAKE_DELIVERY);
        request.add("order_id", order_id);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(getActivity(), netBaseBean.getMessage());
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
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    T.showShort(getActivity(), "付款成功");
                    onRefresh();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);*/
        PayActivity.goTo(getActivity(), out_trade_no);

    }

    private void cancel_refund_apply(final int order_id, final int order_goods_id) {
        new AlertDialog.Builder(getActivity()).setMessage("确认撤销退款申请?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CANCEL_REFUND_APPLY);
                request.add("order_id", order_id);
                request.add("order_goods_id", order_goods_id);
                CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
                    @Override
                    public void onSucceed(int what, NetBaseBean netBaseBean) {
                        T.showShort(getActivity(), netBaseBean.getMessage());
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

    @Override
    protected void loadData() {
        onRefresh();
    }

    private void getOrderList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ORDER_LIST);
        request.add("pageno", num);
        request.add("state", state);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    OrderList orderList = netBaseBean.parseObject(OrderList.class);
                    pagerNum = orderList.next_page;
                    List<Order> list = orderList.list;
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Override
    public void onLoadMoreRequested() {
        getOrderList(pagerNum);
    }

    @Override
    public void onRefresh() {
        getOrderList(1);
    }
}
