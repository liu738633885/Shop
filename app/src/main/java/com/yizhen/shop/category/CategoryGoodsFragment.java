package com.yizhen.shop.category;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.IconHintView;
import com.yizhen.shop.Constants;
import com.yizhen.shop.MainActivity;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.home.ViewPagerAdapter;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.GoodsList;
import com.yizhen.shop.model.home.Adv;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.List;

/**
 * Created by lewis on 2017/8/18.
 */

public class CategoryGoodsFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int pagerNum = 1;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private int category_id;
    private View headView;
    private RollPagerView rollPagerView;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swl_rv;
    }

    public static CategoryGoodsFragment newInstance(int category_id) {
        CategoryGoodsFragment fragment = new CategoryGoodsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category_id", category_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        category_id = getArguments().getInt("category_id");
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setBackgroundColor(Color.WHITE);
        initAdapter();
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_home1, (ViewGroup) rv.getParent(), false);
        rollPagerView = headView.findViewById(R.id.roll_view_pager);
        rollPagerView.setHintView(new IconHintView(getActivity(), R.drawable.shape_viewpager_point_focus, R.drawable.shape_viewpager_point_normal, 0));
        pagerAdapter = new ViewPagerAdapter(getActivity(), rollPagerView);
        rollPagerView.setAdapter(pagerAdapter);
        adapter.addHeaderView(headView);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view, rv);
        if(getActivity() instanceof CategoryGoodsActivity){
            loadData();
        }
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_goods) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
                ImageLoader.loadHome(mContext, item.pic_cover_big, (ImageView) helper.getView(R.id.imv));
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

    private void get_category_goods(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CATEGORY_GOODS);
        request.add("category_id", category_id);
        request.add("pageno", num);
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

    private void get_Adv_list() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(Constants.GET_CATEGORY_ADV_LIST);
        netBaseRequest.add("category_id",category_id);
        CallServer.getRequestInstance().add(getActivity(), 0x01, netBaseRequest, new HttpListenerCallback() {

            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Adv> advs = netBaseBean.parseList(Adv.class);
                    if (advs == null || advs.size() < 1) {
                        headView.setVisibility(View.GONE);
                    } else {
                        headView.setVisibility(View.VISIBLE);
                    }
                    if (advs.size() < 2) {
                        rollPagerView.setPlayDelay(0);
                    } else {
                        rollPagerView.setPlayDelay(3000);
                    }
                    pagerAdapter.updata(advs);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }


        }, swl, "");
    }

    @Override
    protected void loadData() {
        super.loadData();
        onRefresh();
    }

    @Override
    public void onLoadMoreRequested() {
        get_category_goods(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_category_goods(1);
        get_Adv_list();

    }
}
