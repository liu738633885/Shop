package com.yizhen.shop.goods;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.Topic;
import com.yizhen.shop.model.goods.TopicInfo;
import com.yizhen.shop.model.goods.TopicInfoList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;


/**
 * Created by lewis on 2017/8/24.
 */

public class TopicFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv, rv2;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private BaseQuickAdapter<TopicInfo, BaseViewHolder> adapter2;
    private int topic_id;
    private ImageView headView;
    private TextView recommend_topic;
    private View footerView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swl_rv;
    }

    public static TopicFragment newInstance(int topic_id) {
        TopicFragment fragment = new TopicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("topic_id", topic_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        topic_id = getArguments().getInt("topic_id");
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setBackgroundColor(Color.WHITE);
        initAdapter();
        rv.setAdapter(adapter);
        headView = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.headview_imageview, (ViewGroup) rv.getParent(), false);
        adapter.addHeaderView(headView);
        adapter.setEmptyView(R.layout.empty_view, rv);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.footerview_topic, (ViewGroup) rv.getParent(), false);
        rv2 = footerView.findViewById(R.id.rv2);
        rv2.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv2.setAdapter(adapter2);
        adapter.addFooterView(footerView);

        if (getActivity() instanceof TopicActivity) {
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
        adapter2 = new BaseQuickAdapter<TopicInfo, BaseViewHolder>(R.layout.item_topic) {
            @Override
            protected void convert(BaseViewHolder helper, final TopicInfo item) {
                helper.setText(R.id.tv1, item.title);
                helper.setText(R.id.tv2, item.describe);
                ImageLoader.loadAutoHeight(getActivity(), item.image, (ImageView) helper.getView(R.id.imv), 0);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TopicActivity.goTo(getActivity(),item.topic_id,item.title);
                    }
                });
            }
        };
    }

    @Override
    protected void loadData() {
        super.loadData();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        get_topic_detail();
    }

    private void get_topic_detail() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_TOPIC_DETAIL);
        request.add("topic_id", topic_id);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Topic topic = netBaseBean.parseObject(Topic.class);
                    ImageLoader.loadAutoHeight(getActivity(), topic.topicInfo.image, headView, 0);
                    if (topic.list == null || topic.list.size() < 1) {
                        topic.list = new ArrayList<Goods>();
                    }
                    adapter.setNewData(topic.list);
                    get_recommend_topic();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void get_recommend_topic() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_RECOMMEND_TOPIC);
        request.add("topic_id", topic_id);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    TopicInfoList data = netBaseBean.parseObject(TopicInfoList.class);
                    if (data.list != null && data.list.size() > 0) {
                        adapter2.setNewData(data.list);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responeCode, long networkMillis) {

            }
        }, swl, "");
    }
}
