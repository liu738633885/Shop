package com.yizhen.shop.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.GoodsList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
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
 * Created by lewis on 2017/10/13.
 */

public class AuctionSuccessFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private int pagerNum = 1;
    private int type;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swl_rv;
    }

    public static AuctionSuccessFragment newInstance(int type) {
        AuctionSuccessFragment fragment = new AuctionSuccessFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        type = getArguments().getInt("type");
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        rv.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_auction_success) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv1, item.goods_name);
                helper.setText(R.id.tv2, "成交 ¥" + item.auction_price);
                helper.setText(R.id.tv3, item.offers + "次出价");
                ImageLoader.loadHome(mContext, item.goods_img, (ImageView) helper.getView(R.id.imv));
                helper.setGone(R.id.ll_button, type == 3 || type == 4);
                helper.setGone(R.id.btn_express, type == 3 || type == 4);
                helper.setGone(R.id.btn_delivery, type == 3);
                helper.getView(R.id.btn_express).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goTo(getActivity(), Constants.WEB_EXPRESS + item.order_id, "物流信息");
                    }
                });
                helper.getView(R.id.btn_delivery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auctionOrderTakeDelivery(item.order_id);
                    }
                });
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goToWithLogIn(mContext, Constants.AUCTION_DETAIL_WEB + item.goods_id, "拍卖详情");
                    }
                });
            }
        };
    }

    private void auctionOrderTakeDelivery(final int order_id) {
        new AlertDialog.Builder(getActivity()).setMessage("确认收货?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.AUCTION_ORDER_TASK_DELIVERY);
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
        }).setNegativeButton("取消", null).create().show();
    }

    @Override
    public void onRefresh() {
        get_auction_success(1);
    }

    private void get_auction_success(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_SUCCESS);
        request.add("pageno", num);
        request.add("type", type);
        //request.add("type", 1);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    GoodsList goodsList = netBaseBean.parseObject(GoodsList.class);
                    pagerNum = goodsList.next_page;
                    List<Goods> list = goodsList.list;
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Override
    protected void loadData() {
        onRefresh();
    }


    @Override
    public void onLoadMoreRequested() {
        get_auction_success(pagerNum);
    }
}
