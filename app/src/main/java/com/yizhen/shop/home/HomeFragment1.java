package com.yizhen.shop.home;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.IconHintView;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.GoodsList;
import com.yizhen.shop.model.home.Adv;
import com.yizhen.shop.model.home.Home;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/7/21.
 */

public class HomeFragment1 extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv, rv0;
    private View headView;
    private RollPagerView rollPagerView;
    private LewisSwipeRefreshLayout swl;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private HomeBeanAdapter headAdapter;
    private ViewPagerAdapter pagerAdapter;
    private int pagerNum;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home1;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv0 = new RecyclerView(getActivity());
        rv0.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_home1, (ViewGroup) rv0.getParent(), false);

        rollPagerView = headView.findViewById(R.id.roll_view_pager);
        rollPagerView.setHintView(new IconHintView(getActivity(), R.drawable.shape_viewpager_point_focus, R.drawable.shape_viewpager_point_normal, 0));

        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_goods) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
                ImageLoader.loadHome(mContext, item.pic_cover_mid, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv_desc, item.keywords);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(getActivity(), item.goods_id);
                    }
                });
            }
        };
        headAdapter = new HomeBeanAdapter();
        pagerAdapter = new ViewPagerAdapter(getActivity(), rollPagerView);
        rv.setAdapter(adapter);
        rv0.setAdapter(headAdapter);
        headAdapter.addHeaderView(headView);
        adapter.addHeaderView(rv0);
        rollPagerView.setAdapter(pagerAdapter);
        adapter.setOnLoadMoreListener(this, rv);
    }

    @Override
    protected void loadData() {
        super.loadData();
        onRefresh();
    }

    private void get_index_list() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_INDEX_LIST);
        CallServer.getRequestInstance().add(getActivity(), 0x02, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Home home = netBaseBean.parseObject(Home.class);
                    List<Goods> goodsList = new ArrayList<Goods>();
                    goodsList.add(new Goods(Home.TYPE_TITLE, "精品推荐"));
                    for (Goods goods : home.direct_goods_list) {
                        goods.type = Home.TYPE_GOODS_KAIGUANG;
                        goodsList.add(goods);
                    }
                    //goodsList.add(new Goods(Home.TYPE_TITLE, "限时抢购"));
                    if(home.discount.goods_list.size()>0){
                        Goods discountGoods = home.discount.goods_list.get(0);
                        discountGoods.type = Home.TYPE_DISCOUNT;
                        discountGoods.end_time = home.discount.end_time;
                        discountGoods.next_time = home.discount.next_time;
                        goodsList.add(discountGoods);
                    }
                    goodsList.add(new Goods(Home.TYPE_TITLE, "新品首发"));
                    //goodsList.add(new Goods(Home.TYPE_TITLE_NEW, "新品首发", "http://yanxuan.nosdn.127.net/1677b1e2ffa92161b46f1a59816b560d.jpg?imageView&quality=95&thumbnail=1090x310"));
                    for (Goods goods : home.new_goods_list) {
                        goods.type = Home.TYPE_GOODS_NEW;
                        goodsList.add(goods);
                    }
                    //goodsList.add(new Goods(Home.TYPE_TITLE_SPECIAL, "佛堂必备", "https://img.alicdn.com/imgextra/i2/408148187/TB2V_qMcXXXXXaRXXXXXXXXXXXX_!!408148187.jpg"));
                    if (home.special_list != null && home.special_list.goods_list != null && home.special_list.goods_list.size() > 0) {
                        Goods goodsFo = new Goods(Home.TYPE_TITLE_SPECIAL, "佛堂必备", home.special_list.image, home.special_list.topic_id);
                        goodsFo.tv1 = home.special_list.title;
                        goodsFo.tv2 = home.special_list.describe;
                        goodsList.add(goodsFo);
                        for (Goods goods : home.special_list.goods_list) {
                            goods.type = Home.TYPE_GOODS_NEW;
                            goodsList.add(goods);
                        }
                    }
                    goodsList.add(new Goods(Home.TYPE_TITLE, "猜你喜欢"));
                    //-----
                    headAdapter.setNewData(goodsList);
                    //get_recommend_list(0);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl);
    }

    private void get_recommend_list(final int num) {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_RECOMMEND_LIST);
        CallServer.getRequestInstance().add(getActivity(), num, netBaseRequest, new HttpListenerCallback() {

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

    private void get_Adv_list() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_ADV_LIST);
        CallServer.getRequestInstance().add(getActivity(), 0x01, netBaseRequest, new HttpListenerCallback() {

            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Adv> advs = netBaseBean.parseList(Adv.class);
                    pagerAdapter.updata(advs);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }


        }, swl, "");
    }

    @Override
    public int getNum() {
        return 1;
    }

    @Override
    public void onRefresh() {
        get_Adv_list();
        get_index_list();
        get_recommend_list(1);
    }

    @Override
    public void onLoadMoreRequested() {
        get_recommend_list(pagerNum);
    }
}
