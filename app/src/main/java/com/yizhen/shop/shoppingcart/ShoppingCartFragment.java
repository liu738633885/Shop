package com.yizhen.shop.shoppingcart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.Constants;
import com.yizhen.shop.MainActivity;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.model.cart.Cart;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.order.AddNewOrderActivity;
import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.util.StatusBarUtil;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.widgets.AmountView;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.yizhen.shop.R.id.tv;

/**
 * Created by lewis on 2017/7/14.
 */

public class ShoppingCartFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv, rv2;
    private TextView tv_edit;
    private boolean isEdit;
    private Button btn_all;
    private TextView tv_all_num, tv_all_price;
    private CheckBox cb_all;
    private LinearLayout ll_bottom_bar;
    private View footerView;
    private ImageView back;
    private BaseQuickAdapter<Cart, BaseViewHolder> adapter;
    private LewisSwipeRefreshLayout swl;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter2;
    private View loginHeadView;
    private DecimalFormat df;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shopping_car;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        StatusBarUtil.setPaddingSmart(getActivity(), view);
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        back = view.findViewById(R.id.back);
        if (getActivity() instanceof MainActivity) {
            back.setVisibility(View.INVISIBLE);
        }
        df = new DecimalFormat("######0.00");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        btn_all = view.findViewById(R.id.btn_all);
        tv_all_num = view.findViewById(R.id.tv_all_num);
        tv_all_price = view.findViewById(R.id.tv_all_price);
        cb_all = view.findViewById(R.id.cb_all);
        ll_bottom_bar = view.findViewById(R.id.ll_bottom_bar);
        tv_edit = view.findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    update_cart();
                } else {
                    isEdit = !isEdit;
                    adapter.notifyDataSetChanged();
                    updateBottomBar();
                }
            }
        });
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_view_wrap, rv);
        //测试头部
        //TextView textView=new TextView(getActivity());
        //textView.setText("发都发放大范德萨发的发的说法的说法的发送到发送到发送到发顺丰撒发顺丰撒旦法师法师打发沙发斯蒂芬第三方三地方水电费水电费大 发的发到付阿斯蒂芬 asas 发生的 ");
        //adapter.addHeaderView(textView);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.footerview_shopping_car, (ViewGroup) rv.getParent(), false);
        adapter.addFooterView(footerView);
        loginHeadView = view.findViewById(R.id.ll_login);
        loginHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        adapter.setHeaderFooterEmpty(true, true);
        rv2 = footerView.findViewById(R.id.rv2);
        rv2.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv2.setAdapter(adapter2);
        updateBottomBar();
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    Iterator<Cart> iter = adapter.getData().iterator();
                    while (iter.hasNext()) {
                        Cart next = iter.next();
                        if (next.checkedEdit) {
                            iter.remove();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateBottomBar();
                } else {
                    //T.showShort(getActivity(), "下单");
                    List<Integer> cart_ids = new ArrayList<Integer>();
                    for (Cart cart : adapter.getData()) {
                        if (cart.checked) {
                            cart_ids.add(cart.cart_id);
                        }
                    }
                    AddNewOrderActivity.goTo(getActivity(), cart_ids.toString(), 2);
                }
            }
        });
        //onRefresh();
    }
    public void onResume(){
        super.onResume();
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Cart, BaseViewHolder>(R.layout.item_shopping_car_goods) {

            @Override
            protected void convert(final BaseViewHolder helper, final Cart item) {
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(getActivity(), item.goods_id);
                    }
                });
                helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(getActivity()).setMessage("确定删除此商品?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                del_cart(item.cart_id);
                            }
                        }).setNegativeButton("否", null).create().show();
                        return true;
                    }
                });
                TextView tv_title = helper.getView(R.id.tv_title);
                TextView tv_spec = helper.getView(R.id.tv_spec);
                TextView tv_spec2 = helper.getView(R.id.tv_spec2);
                LinearLayout ll_num = helper.getView(R.id.ll_num);
                AmountView av = helper.getView(R.id.av);
                CheckBox cb = helper.getView(R.id.cb);
                cb.setOnCheckedChangeListener(null);
                if (isEdit) {
                    cb.setChecked(item.checkedEdit);
                } else {
                    cb.setChecked(item.checked);
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isEdit) {
                            item.checkedEdit = b;
                        } else {
                            item.checked = b;
                        }
                        updateBottomBar();
                    }
                });
                //渲染 ui
                tv_title.setText(item.goods_name);
                ImageLoader.loadHome(getActivity(), item.goods_img, (ImageView) helper.getView(R.id.imv));
                tv_spec.setText(item.sku_name);
                tv_spec2.setText("已选择: " + item.sku_name);
                helper.setText(R.id.tv_price, "¥ " + item.price);
                helper.setText(R.id.tv_num, "" + item.num);
                //edit
                tv_title.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
                tv_spec.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
                ll_num.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
                tv_spec2.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
                av.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
                av.setGoods_storage(item.stock);
                av.setAmount(item.num);
                av.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
                    @Override
                    public void onAmountChange(View view, int amount) {
                        //Toast.makeText(getActivity(), "Amount=>  " + amount, Toast.LENGTH_SHORT).show();
                        item.num = amount;
                    }
                });
            }
        };
        adapter2 = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_goods) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
                ImageLoader.loadHome(mContext, item.pic_cover_mid, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv_desc, item.introduction);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(mContext, item.goods_id);
                    }
                });
            }
        };
    }

    private void del_cart(int cart_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.DEL_CART);
        request.add("cart_id", cart_id);
        CallServer.getRequestInstance().add(getActivity(), 0x05, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    onRefresh();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }


    boolean ischooseAll;
    int chooseNum;
    double allPrice;

    private void updateBottomBar() {
        if (isEdit) {
            tv_edit.setText("完成");
        } else {
            tv_edit.setText("编辑");
        }
        //显示
        if (adapter.getData() != null && adapter.getData().size() > 0) {
            ll_bottom_bar.setVisibility(View.VISIBLE);
            tv_edit.setVisibility(View.VISIBLE);
        } else {
            ll_bottom_bar.setVisibility(View.GONE);
            //当购物车没有数据时,隐藏按钮
            if(isEdit){
                tv_edit.setVisibility(View.VISIBLE);
            }else {
                tv_edit.setVisibility(View.INVISIBLE);
            }
        }
        //处理细节
        //初始化数据
        ischooseAll = true;
        chooseNum = 0;

        allPrice = 0;
        //遍历数据
        for (Cart cart : adapter.getData()) {
            if (isEdit) {
                if (cart.checkedEdit) {
                    chooseNum += cart.num;
                } else {
                    ischooseAll = false;
                }
            } else {
                if (cart.checked) {
                    chooseNum += cart.num;
                    allPrice += (cart.num * Double.parseDouble(cart.price));
                } else {
                    ischooseAll = false;
                }
            }

        }
        //渲染视图
        tv_all_price.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
        btn_all.setText(isEdit ? "删除" : "下单");
        cb_all.setOnCheckedChangeListener(null);
        cb_all.setChecked(ischooseAll);
        if (chooseNum != 0) {
            tv_all_num.setText("已选(" + chooseNum + ")");
        } else {
            tv_all_num.setText("全选");
        }
        tv_all_price.setText("合计:¥ " + df.format(allPrice));
        btn_all.setEnabled(chooseNum > 0);
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (Cart cart : adapter.getData()) {
                    if (isEdit) {
                        cart.checkedEdit = b;
                    } else {
                        cart.checked = b;
                    }
                }
                adapter.notifyDataSetChanged();
                updateBottomBar();
            }
        });
    }

   /* @Override
    public void onResume() {
        super.onResume();
        if (!UserManager.isLogin()) {
            adapter.setNewData(new ArrayList<Cart>());
        }
    }*/

    private void getCart() {
        if (!UserManager.isLogin()) {
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CART);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Cart> cartList = netBaseBean.parseList(Cart.class);
                    adapter.setNewData(cartList);
                    isEdit = false;
                    updateBottomBar();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).get_cart_num();
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void update_cart() {
        if (!UserManager.isLogin()) {
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.UPDATE_CART);
        List<String> list = new ArrayList<>();
        for (Cart cart : adapter.getData()) {
            String x = "{\"cart_id\":" + cart.cart_id + ",\"num\":" + cart.num + "}";
            list.add(x);
        }
        Logger.e(list.toString());
        request.add("cart_array", list.toString());
        CallServer.getRequestInstance().add(getActivity(), 0x03, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getCart();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void getTuijian() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GOODS_CART_RECOMMEND);
        CallServer.getRequestInstance().add(getActivity(), 0x04, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Goods> goodsList = netBaseBean.parseList(Goods.class);
                    adapter2.setNewData(goodsList);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void updateLoginUI() {
        loginHeadView.setVisibility(UserManager.isLogin() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        updateLoginUI();
        getCart();
        getTuijian();
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e != null && !TextUtils.isEmpty(e.getAction())) {
            if (e.getAction().equals(EventRefresh.ACTION_LOGIN)) {
                onRefresh();
            } else if (e.getAction().equals(EventRefresh.ADD_ORDER_OK)) {
                onRefresh();
            } else if (e.getAction().equals(EventRefresh.ACTION_LOGOUT)) {
                adapter.setNewData(new ArrayList<Cart>());
                updateLoginUI();
            }
        }
    }
}
