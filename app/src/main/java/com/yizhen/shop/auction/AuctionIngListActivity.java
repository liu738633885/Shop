package com.yizhen.shop.auction;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.TimeTextView;
import com.yizhen.shop.widgets.TitleBar;

import java.util.List;

public class AuctionIngListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
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
        titleBar.setCenterText("我的参拍");
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
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_auction_match) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv1, item.goods_name);
                helper.setText(R.id.tv2, "当前 ¥ " + item.auction_price);
                ImageLoader.loadHome(mContext, item.goods_img, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv3, item.offers + "次出价");
                TimeTextView tv4 = helper.getView(R.id.tv4);
                //tv4.setTimes(item.e_time*1000);
                tv4.setTimes(item.s_time, item.e_time);
                // helper.setText(R.id.tv4, "距结束还有");
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goToWithLogIn(mContext, Constants.AUCTION_DETAIL_WEB + item.goods_id, "拍卖详情");
                    }
                });
                TextView tv_state = helper.getView(R.id.tv_state);
                switch (item.status) {
                    case 1:
                        tv_state.setVisibility(View.VISIBLE);
                        tv_state.setText("进行中");
                        tv_state.setTextColor(ContextCompat.getColor(bContext, R.color.super_red));
                        break;
                    case 2:
                        tv_state.setVisibility(View.VISIBLE);
                        tv_state.setText("已出局");
                        tv_state.setTextColor(ContextCompat.getColor(bContext, R.color.gray03));
                        break;
                    case 3:
                        tv_state.setVisibility(View.VISIBLE);
                        tv_state.setText("已获拍");
                        tv_state.setTextColor(ContextCompat.getColor(bContext, R.color.super_red));
                        break;
                }
            }
        };
    }

    @Override
    public void onLoadMoreRequested() {
        get_auction_ing(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_auction_ing(1);
    }

    private void get_auction_ing(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_ING);
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
