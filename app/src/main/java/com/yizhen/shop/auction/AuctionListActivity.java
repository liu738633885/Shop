package com.yizhen.shop.auction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.goods.GoodsListActivity;
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

public class AuctionListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private TitleBar titleBar;
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int pagerNum = 1;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private String title;
    private int id;
    private int mode;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_auction_list;
    }

    public static void goTo(Context context, String title, int id, int mode) {
        Intent intent = new Intent(context, AuctionListActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        intent.putExtra("mode", mode);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        title = intent.getStringExtra("title");
        id = intent.getIntExtra("id", 0);
        mode = intent.getIntExtra("mode", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (!TextUtils.isEmpty(title)) {
            titleBar.setCenterText(title);
        }
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view, rv);
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_auction) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv1, item.goods_name);
                helper.setText(R.id.tv2, "¥" + item.starting_price);
                ImageLoader.loadHome(mContext, item.goods_img, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv3, item.offers > 0 ? item.offers + "次出价" : "起拍价");
                TimeTextView tv4 = helper.getView(R.id.tv4);
                //tv4.setTimes(item.e_time*1000);
                tv4.setTimes(item.s_time, item.e_time);
                // helper.setText(R.id.tv4, "距结束还有");
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goTo(mContext, Constants.AUCTION_DETAIL_WEB + item.goods_id, "拍卖详情");
                    }
                });
            }
        };
    }

    @Override
    public void onLoadMoreRequested() {
        get_category_goods(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_category_goods(1);
    }

    private void get_category_goods(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_CATEGORY_GOODS);
        request.add("pageno", num);
        if (mode == 0) {
            request.add("category_id", id);
        } else if (mode == 1) {
            request.add("theme_id", id);
        }
        CallServer.getRequestInstance().add(bContext, num, request, new HttpListenerCallback() {
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
