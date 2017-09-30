package com.yizhen.shop.auction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.IconHintView;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.home.ViewPagerAdapter;
import com.yizhen.shop.model.auction.AuctionHome;
import com.yizhen.shop.model.auction.AuctionTheme;
import com.yizhen.shop.model.category.Category;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.home.Adv;
import com.yizhen.shop.model.home.Home;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.StatusBarUtil;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/9/28.
 */

public class AuctionHomeFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private AuctionHomeBeanAdapter adapter;
    private ViewPagerAdapter pagerAdapter;
    private View headView;
    private RollPagerView rollPagerView;
    private LinearLayout ll_cate;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auction_home;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        StatusBarUtil.setPaddingSmart(getActivity(), view);
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_auction_home, (ViewGroup) rv.getParent(), false);
        rollPagerView = headView.findViewById(R.id.roll_view_pager);
        ll_cate = headView.findViewById(R.id.ll_cate);
        rollPagerView.setHintView(new IconHintView(getActivity(), R.drawable.shape_viewpager_point_focus, R.drawable.shape_viewpager_point_normal, 0));
        adapter = new AuctionHomeBeanAdapter();
        adapter.addHeaderView(headView);
        pagerAdapter = new ViewPagerAdapter(getActivity(), rollPagerView);
        rollPagerView.setAdapter(pagerAdapter);
        rv.setAdapter(adapter);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        get_Auction_Adv_list();
        get_theme_list();

    }

    private List<AuctionTheme> themes;

    private void get_theme_list() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_THEME_LIST);
        CallServer.getRequestInstance().add(getActivity(), num, netBaseRequest, new HttpListenerCallback() {

            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    themes = netBaseBean.parseList(AuctionTheme.class);
                }
                get_index_list();
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                get_index_list();
            }


        }, swl, "");
    }

    private void get_index_list() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_INDEX_LIST);
        CallServer.getRequestInstance().add(getActivity(), num, netBaseRequest, new HttpListenerCallback() {

            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    AuctionHome auctionHome = netBaseBean.parseObject(AuctionHome.class);
                    List<Object> list = new ArrayList<Object>();
                    if (themes != null && themes.size() > 0) {
                        list.addAll(themes);
                    }
                    ll_cate.removeAllViews();
                    for (final Category c : auctionHome.cate_list) {
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_auction_cate, ll_cate, false);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        TextView tv = layout.findViewById(R.id.tv);
                        ImageView imv = layout.findViewById(R.id.imv);
                        tv.setText(c.category_name);
                        ImageLoader.load(getActivity(), c.category_pic, imv);
                        ll_cate.addView(layout);
                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AuctionListActivity.goTo(getActivity(), c.category_name, c.category_id, 0);
                            }
                        });
                    }
                    LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_auction_cate_more, ll_cate, false);
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    ll_cate.addView(layout);
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(), AuctionCateActivity.class));
                        }
                    });
                    list.add(new Goods(Home.TYPE_TITLE, "今日拍卖"));
                    for (Goods g : auctionHome.today_list) {
                        g.type = Home.TYPE_AUCTION_MATCH;
                        list.add(g);
                    }
                    list.add(new Goods(Home.TYPE_TITLE, "热门推荐"));
                    for (Goods g : auctionHome.hot_list) {
                        g.type = Home.TYPE_AUCTION_MATCH;
                        list.add(g);
                    }
                    list.add(new Goods(Home.TYPE_TITLE, "拍卖预告"));
                    for (Goods g : auctionHome.ready_list) {
                        g.type = Home.TYPE_AUCTION_WRAP;
                        list.add(g);
                    }
                    list.add(new Goods(Home.TYPE_TITLE, "为您精选"));
                    for (Goods g : auctionHome.push_list) {
                        g.type = Home.TYPE_AUCTION_MATCH;
                        list.add(g);
                    }
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }


        }, swl, "");
    }

    private void get_Auction_Adv_list() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_ADV_LIST);
        CallServer.getRequestInstance().add(getActivity(), num, netBaseRequest, new HttpListenerCallback() {

            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                List<Adv> advs = netBaseBean.parseList(Adv.class);
                pagerAdapter.updata(advs);
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                get_index_list();
            }


        }, swl, "");
    }


}
