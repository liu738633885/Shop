package com.yizhen.shop.auction;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.GoodsList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.TitleBar;

import java.util.List;

/**
 * Created by lewis on 2017/10/12.
 */

public class AuctionMarginListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private TitleBar titleBar;
    private int pagerNum = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.layut_titlebar_swl_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.setCenterText("我的保证金");
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(bContext));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view, rv);
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_auction_baozheng) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv1, item.goods_name);
                helper.setText(R.id.tv2, "保证金金额: " + item.margin_money + "元");
                ImageLoader.loadHome(mContext, item.goods_img, (ImageView) helper.getView(R.id.imv));
                if (item.refund_type == 1) {
                    helper.setText(R.id.tv3, "退还方式: 微信");
                } else if (item.refund_type == 2) {
                    helper.setText(R.id.tv3, "退还方式: 支付宝");
                } else {
                    helper.setText(R.id.tv3, "退还方式: ");
                }
                try {
                    helper.setText(R.id.tv_time1, "缴纳时间: " + DateUtils.tenLongToString(item.pay_time, DateUtils.DB_DATA_FORMAT));
                    helper.setGone(R.id.tv_time2, item.refund_time != 0);
                    helper.setText(R.id.tv_time2, "释放时间: " + DateUtils.tenLongToString(item.refund_time, DateUtils.DB_DATA_FORMAT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (item.pay_type == 1) {
                    helper.setText(R.id.tv_pay_state, "支付方式: 微信");
                } else if (item.pay_type == 2) {
                    helper.setText(R.id.tv_pay_state, "支付方式: 支付宝");
                } else {
                    helper.setText(R.id.tv_pay_state, "支付方式: ");
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goToWithLogIn(mContext, Constants.AUCTION_DETAIL_WEB + item.goods_id, "拍卖详情");
                    }
                });
            }
        };
    }

    @Override
    public void onLoadMoreRequested() {
        get_auction_margin_list(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_auction_margin_list(1);
    }

    private void get_auction_margin_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_MARGIN_LIST);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
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

}
