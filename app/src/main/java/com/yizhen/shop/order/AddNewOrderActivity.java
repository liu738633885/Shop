package com.yizhen.shop.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.address.Address;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.goods.Bill;
import com.yizhen.shop.model.goods.Coupon;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.order.Order;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.user.AddBillActivity;
import com.yizhen.shop.user.AddressListActivity;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.dialog.BottomDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class AddNewOrderActivity extends BaseActivity {
    public static String TAG = AddNewOrderActivity.class.getSimpleName();
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private RecyclerView rv, rv_coupon;
    private View headView;
    private BottomDialog couponDialog;
    private int mode;//1 购买   2 购物车
    private String sku;
    private TextView tv_coupon, tv_all_price, tv_freight, tv_fapiao, tv_choose_address, tv_address_name, tv_address_phone, tv_address_default, tv_address, tv_all_price_bottom;
    private RelativeLayout rl_address;
    private BaseQuickAdapter<Coupon, BaseViewHolder> couponAdapter;
    private Order order;
    private Address chooseAddress;
    private Button add_order;
    private int is_invoice;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_new_order;
    }

    public static void goTo(Context context, String sku, int mode) {
        Intent intent = new Intent(context, AddNewOrderActivity.class);
        intent.putExtra("sku", sku);
        intent.putExtra("mode", mode);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        mode = intent.getIntExtra("mode", 0);
        sku = intent.getStringExtra("sku");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        headView = LayoutInflater.from(this).inflate(R.layout.headview_add_order, (ViewGroup) rv.getParent(), false);
        adapter.addHeaderView(headView);
        rv.setAdapter(adapter);
        tv_coupon = headView.findViewById(R.id.tv_coupon);
        tv_all_price = headView.findViewById(R.id.tv_all_price);
        tv_all_price_bottom = (TextView) findViewById(R.id.tv_all_price_bottom);
        tv_freight = headView.findViewById(R.id.tv_freight);
        tv_fapiao = headView.findViewById(R.id.tv_fapiao);
        ((View) tv_fapiao.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bContext, AddBillActivity.class));
            }
        });
        tv_choose_address = headView.findViewById(R.id.tv_choose_address);
        rl_address = headView.findViewById(R.id.rl_address);
        tv_address_name = headView.findViewById(R.id.tv_address_name);
        tv_address_phone = headView.findViewById(R.id.tv_address_phone);
        tv_address_default = headView.findViewById(R.id.tv_address_default);
        tv_address = headView.findViewById(R.id.tv_address);
        add_order = (Button) findViewById(R.id.add_order);
        tv_choose_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressListActivity.goTo(bContext, TAG);
            }
        });
        rl_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressListActivity.goTo(bContext, TAG);
            }
        });
        ((View) tv_coupon.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponDialog.show();
            }
        });
        add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatOrder();
            }
        });
        initCouponDialog();
        payment_order();
    }


    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_shopping_car_goods) {
            @Override
            protected void convert(BaseViewHolder helper, Goods item) {
                helper.setVisible(R.id.cb, false);
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_num, item.num + "");
                helper.setText(R.id.tv_spec, item.sku_name);
                helper.setText(R.id.tv_price, item.price);
                ImageLoader.loadHome(bContext, item.pic_cover_mid, (ImageView) helper.getView(R.id.imv));
            }
        };
    }

    private Coupon chooseCoupon;

    private void initCouponDialog() {
        couponDialog = new BottomDialog(this);
        couponDialog.setContentView(R.layout.dialog_choose_coupon);
        rv_coupon = couponDialog.findViewById(R.id.rv);
        rv_coupon.setLayoutManager(new LinearLayoutManager(this));
        couponAdapter = new BaseQuickAdapter<Coupon, BaseViewHolder>(R.layout.item_coupon) {
            @Override
            protected void convert(BaseViewHolder helper, final Coupon item) {
                helper.setText(R.id.tv1, "¥" + item.money);
                helper.setText(R.id.tv2, "满" + item.at_least + "使用");
                try {
                    helper.setText(R.id.tv3, "有效期" + DateUtils.tenLongToString(item.start_time, DateUtils.yyyyMMDD2) + " - " + DateUtils.tenLongToString(item.end_time, DateUtils.yyyyMMDD2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                helper.setText(R.id.tv_get, "立刻使用");
                helper.getView(R.id.tv_get).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (order.total_money > Double.parseDouble(item.at_least)) {
                            chooseCoupon = item;
                            updateCouponUI();
                            updatePriceUI();
                            couponDialog.dismiss();
                        } else {
                            T.showShort(bContext, "不符合用卷条件");
                        }
                    }
                });
            }
        };
        rv_coupon.setAdapter(couponAdapter);
    }

    private void updateUI() {
        if (order == null) {
            return;
        }
        updateAddressUI();
        updateCouponUI();
        updatePriceUI();
        if (order.goods_list != null) {
            adapter.setNewData(order.goods_list);
        }
    }

    private void updatePriceUI() {
        tv_all_price.setText("¥ " + order.total_money);
        if (chooseCoupon != null) {
            tv_all_price_bottom.setText("¥ " + (order.total_money - Double.parseDouble(chooseCoupon.money)));
        } else {
            tv_all_price_bottom.setText("¥ " + order.total_money);
        }
    }

    private void updateAddressUI() {
        if (chooseAddress == null) {
            rl_address.setVisibility(View.GONE);
            tv_choose_address.setVisibility(View.VISIBLE);
        } else {
            rl_address.setVisibility(View.VISIBLE);
            tv_choose_address.setVisibility(View.GONE);
            tv_address_name.setText(chooseAddress.consigner);
            tv_address_phone.setText(chooseAddress.mobile);
            tv_address.setText(chooseAddress.province + " " + chooseAddress.city + " " + chooseAddress.district + " " + chooseAddress.address);
            tv_address_default.setVisibility(chooseAddress.is_default == 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void updateCouponUI() {
        if (order == null || order.coupon_list == null || order.coupon_list.size() < 1) {
            ((View) tv_coupon.getParent()).setVisibility(View.GONE);
        } else {
            couponAdapter.setNewData(order.coupon_list);
            ((View) tv_coupon.getParent()).setVisibility(View.VISIBLE);
        }
        if (chooseCoupon == null) {
            tv_coupon.setText("未选择优惠券");
        } else {
            tv_coupon.setText("已选择:满" + chooseCoupon.at_least + "减" + chooseCoupon.money);
        }
    }

    private void payment_order() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.PAYMENT_ORDER);
        if (mode == 1) {
            request.add("goods_sku_list", sku);
        } else if (mode == 2) {
            request.add("cart_ids", sku);
        }

        Logger.e(sku);
        request.add("order_type", mode == 1 ? "buy_now" : "cart");
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    order = netBaseBean.parseObject(Order.class);
                    updateUI();
                    get_address_list();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void get_address_list() {
        //默认显示第一个地址
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_ADDRESS_LIST);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Address> list = netBaseBean.parseList(Address.class);
                    for (Address address : list) {
                        if (address.is_default == 1) {
                            chooseAddress = address;
                            updateAddressUI();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG)) {
            if (e.getData() instanceof Address) {
                chooseAddress = (Address) e.getData();
                updateAddressUI();
            } else if (e.getData() instanceof Bill) {
                Bill bill = (Bill) e.getData();
                if (bill != null) {
                    is_invoice = 1;
                    tv_fapiao.setText((bill.type == 2 ? "纸质" : "电子") + "(" + bill.bill_content + (bill.bill_rise_type == 2 ? "-" + bill.company_name : "-个人") + ")");
                }
            }
        }
    }

    private void creatOrder() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CREATE_ORDER);
        String str = "";
        for (Goods goods : order.goods_list) {
            str += goods.sku_id + ",";
        }
        if (str.length() > 0 && str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
        }
        request.add("goods_sku_list", str);
        if (chooseCoupon != null) {
            request.add("coupon_id", chooseCoupon.coupon_id);
        }
        if (chooseAddress != null) {
            request.add("address_id", chooseAddress.id);
        }
        request.add("pay_type", 1);
        request.add("is_invoice", is_invoice);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    //Logger.e(netBaseBean.getBody());
                    OrderListActivity.goTo(bContext, 1);
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }
}
