package com.yizhen.shop.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.model.cart.CartNum;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.goods.Coupon;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.Sku;
import com.yizhen.shop.model.goods.Spec;
import com.yizhen.shop.model.goods.SpecValue;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.order.AddNewOrderActivity;
import com.yizhen.shop.shoppingcart.ShoppingCartActivity;
import com.yizhen.shop.user.CollectionListActivity;
import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.ShareUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.widgets.AmountView;
import com.yizhen.shop.widgets.dialog.BottomDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GoodsDetailActivity extends BaseActivity {
    private String[] tabStrs = {"商品", "评价", "详情"};
    private List<BaseFragment> list_fragment = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentStatePagerAdapter adapter;
    private int goods_id;
    private TextView tv_state, shopping_car_num;
    private Button btn_add_shopping_car, btn_buy, btn_dialog_add_shopping_cart, btn_dialog_buy;
    private View collection;
    private ImageView back;
    private BottomDialog bottomDialog, couponDialog;
    private RecyclerView rv_coupon;
    private BaseQuickAdapter<Coupon, BaseViewHolder> couponAdapter;
    private int collectionMode;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_goods_detail;
    }


    public static void goTo(Context context, int goods_id) {
        Intent intent = new Intent(context, GoodsDetailActivity.class);
        intent.putExtra("goods_id", goods_id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        goods_id = intent.getIntExtra("goods_id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.ShareWX(bContext, Constants.GOODS_DETAIL_WEB + goods_id, "菩提伽耶", "菩提伽耶商品", "", ShareUtils.SHARE_TO_SESSION);
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.GRAVITY_CENTER);
        for (String s : tabStrs) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        list_fragment.add(new GoodsDetailFragment());
        list_fragment.add(GoodsDetailEvaluateFragment.newInstance(goods_id));
        list_fragment.add(GoodsDetailWebFragment.newInstance(goods_id));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(list_fragment.size());
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return list_fragment.get(position);
            }

            @Override
            public int getCount() {
                return tabStrs.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabStrs[position];
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tv_state = (TextView) findViewById(R.id.tv_state);
        btn_add_shopping_car = (Button) findViewById(R.id.btn_add_shopping_car);
        btn_buy = (Button) findViewById(R.id.btn_buy);
        back = (ImageView) findViewById(R.id.back);
        collection = findViewById(R.id.collection);
        shopping_car_num = (TextView) findViewById(R.id.shopping_car_num);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
        ((View) shopping_car_num.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bContext, ShoppingCartActivity.class));
            }
        });
        ((View) collection.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCollection();
            }
        });
        btn_add_shopping_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseSpec(1);
            }
        });
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseSpec(2);
            }
        });
        initCouponDialog();
        get_goods_detail();
        get_cart_num();
    }


    private void get_goods_detail() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_GOODS_DETAIL);
        netBaseRequest.add("goods_id", goods_id);
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {

            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    goods = netBaseBean.parseObject(Goods.class);
                    EventBus.getDefault().post(new EventRefresh(goods, GoodsDetailFragment.TAG));
                    tv_state.setVisibility(goods.state == 1 ? View.GONE : View.VISIBLE);
                    ((View) btn_buy.getParent()).setVisibility(goods.state == 1 ? View.VISIBLE : View.GONE);
                    collection.setBackgroundResource(goods.is_collection == 0 ? R.drawable.ic_collection : R.drawable.ic_collection_check);
                    collectionMode = goods.is_collection;
                    initDialog();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }


        }, true, true, "");
    }

    private LinearLayout ll_tag;
    private TextView tv_spec, tv_price;
    private Map<Integer, SpecValue> map;
    private Goods goods;
    private Sku chooseSku;
    private AmountView av;
    private ImageView imv;
    private int chooseNum = 1;

    private void initDialog() {
        bottomDialog = new BottomDialog(this);
        bottomDialog.setContentView(R.layout.dialog_choose_spec);
        tv_spec = bottomDialog.findViewById(R.id.tv_spec);
        tv_price = bottomDialog.findViewById(R.id.tv_price);
        btn_dialog_add_shopping_cart = bottomDialog.findViewById(R.id.btn_dialog_add_shopping_cart);
        btn_dialog_buy = bottomDialog.findViewById(R.id.btn_dialog_buy);
        btn_dialog_add_shopping_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShoppingCar();
            }
        });
        btn_dialog_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy_now();
                bottomDialog.dismiss();
            }
        });
        av = bottomDialog.findViewById(R.id.av);
        av.getEtAmount().setEnabled(false);
        imv = bottomDialog.findViewById(R.id.imv);
        ImageLoader.load(bContext, goods.img_list.get(0).pic_cover_big, imv, 3);
        ll_tag = bottomDialog.findViewById(R.id.ll_tag);
        ll_tag.removeAllViews();
        map = new LinkedHashMap<>();
        if (goods.spec_list != null && goods.spec_list.size() > 0 && goods.sku_list != null && goods.sku_list.size() > 1) {

            for (final Spec spec : goods.spec_list) {
                TextView tv_title = (TextView) LayoutInflater.from(bContext).inflate(R.layout.item_spec_text, ll_tag, false);
                tv_title.setText(spec.spec_name);
                ll_tag.addView(tv_title);
                final TagFlowLayout tagFlowLayout = new TagFlowLayout(this);
                tagFlowLayout.setMaxSelectCount(1);
                List<SpecValue> list = spec.value;
                TagAdapter<SpecValue> adapter = new TagAdapter<SpecValue>(list) {
                    @Override
                    public View getView(FlowLayout parent, int position, SpecValue o) {
                        TextView tv = (TextView) LayoutInflater.from(bContext).inflate(R.layout.item_spec, tagFlowLayout, false);
                        tv.setText(o.spec_value_name);
                        return tv;
                    }
                };
                tagFlowLayout.setAdapter(adapter);
                ll_tag.addView(tagFlowLayout);
                //准备数据
                map.put(spec.spec_id, spec.value.get(0));
                adapter.setSelectedList(0);
                tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent) {
                        //T.showShort(bContext, spec.value.get(position).spec_value_name);
                        map.put(spec.spec_id, spec.value.get(position));
                        chooseSpec();
                        Logger.e(map.toString());
                        return true;
                    }
                });
            }
        } else {
        }
        chooseSku = goods.sku_list.get(0);
        updateDialogView();

    }


    private void chooseSpec() {
        if (map == null) {
            return;
        }
        String str = "";
        for (int key : map.keySet()) {
            if (map.get(key) == null) {
                return;
            } else {
                str += (key + ":" + map.get(key).spec_value_id + ";");
            }
        }
        if (str.endsWith(";") && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        for (Sku sku : goods.sku_list) {
            if (sku.attr_value_items.equals(str)) {
                chooseSku = sku;
                updateDialogView();
                return;
            }
        }
    }

    private void updateDialogView() {
        av.setOnAmountChangeListener(null);
        tv_spec.setText(chooseSku.sku_name);
        av.setGoods_storage(chooseSku.stock);
        if (chooseNum > chooseSku.stock) {
            chooseNum = chooseSku.stock;
        }
        av.setAmount(chooseNum);
        av.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, int amount) {
                //Toast.makeText(getActivity(), "Amount=>  " + amount, Toast.LENGTH_SHORT).show();
                chooseNum = amount;
                updateDialogView();
            }
        });
        tv_price.setText(Double.parseDouble(chooseSku.promote_price) * chooseNum + "");
    }

    public void showChooseSpec(int mode) {
        //0都显示  1显示购物车  2显示直接购买
        if (mode == 0) {
            btn_dialog_add_shopping_cart.setVisibility(View.VISIBLE);
            btn_dialog_buy.setVisibility(View.VISIBLE);
        } else if (mode == 1) {
            btn_dialog_add_shopping_cart.setVisibility(View.VISIBLE);
            btn_dialog_buy.setVisibility(View.GONE);
        } else if (mode == 2) {
            btn_dialog_add_shopping_cart.setVisibility(View.GONE);
            btn_dialog_buy.setVisibility(View.VISIBLE);
        }
        bottomDialog.show();
    }

    private void addShoppingCar() {
        if (chooseSku != null && chooseNum > 0) {
            bottomDialog.dismiss();
            NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_CART);
            request.add("goods_id", goods_id);
            request.add("sku_id", chooseSku.sku_id);
            request.add("count", chooseNum);
            CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    //T.showShort(bContext, netBaseBean.getMessage());

                    get_cart_num();
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }
            }, true, true);
        } else {
            T.showShort(bContext, "还没选择规格");
        }
    }

    private void buy_now() {
        if (!UserManager.isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if (chooseSku != null && chooseNum > 0) {
            String str = "";
            str += "[{\"" + chooseSku.sku_id + "\":" + chooseNum + "}]";
            AddNewOrderActivity.goTo(this, str, 1);
        } else {
            T.showShort(bContext, "还没选择规格");
        }
    }

    private void get_cart_num() {
        if (UserManager.isLogin()) {
            NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CART_NUM);
            CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    if (netBaseBean.isSuccess()) {
                        CartNum cartNum = netBaseBean.parseObject(CartNum.class);
                        if (cartNum.num > 0) {
                            shopping_car_num.setText(cartNum.num + "");
                            shopping_car_num.setVisibility(View.VISIBLE);
                        } else {
                            shopping_car_num.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        shopping_car_num.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }
            }, true, true);
        } else {
            shopping_car_num.setVisibility(View.INVISIBLE);
        }
    }

    public void coupon_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.COUPON_LIST);
        request.add("goods_id", goods_id);
        CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Coupon> couponList = netBaseBean.parseList(Coupon.class);
                    showCouponDialog(couponList);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

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
                helper.getView(R.id.tv_get).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        get_coupon(item.coupon_type_id);
                    }
                });
            }
        };
        rv_coupon.setAdapter(couponAdapter);
    }

    private void showCouponDialog(List<Coupon> list) {
        if (list != null && list.size() > 0) {
            couponAdapter.setNewData(list);
            couponDialog.show();
        } else {
            T.showShort(this, "没有可用优惠券");
        }
    }

    private void get_coupon(int coupon_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_COUPON);
        request.add("coupon_type_id", coupon_id);
        CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(bContext, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    couponDialog.dismiss();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);

    }

    private void setCollection() {
        NetBaseRequest request;
        if (collectionMode == 0) {
            request = RequsetFactory.creatBaseRequest(Constants.ADD_COLLECTION_GOODS);
        } else {
            request = RequsetFactory.creatBaseRequest(Constants.CANCEL_COLLECTION_GOODS);
        }
        request.add("goods_id", goods_id);
        CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                //T.showShort(bContext, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    if (collectionMode == 0) {
                        collectionMode = 1;
                    } else {
                        collectionMode = 0;
                    }
                    collection.setBackgroundResource(collectionMode == 0 ? R.drawable.ic_collection : R.drawable.ic_collection_check);
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, CollectionListActivity.TAG));
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    public void move2() {
        viewPager.setCurrentItem(1, true);
    }

    private void close() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
            return;
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
