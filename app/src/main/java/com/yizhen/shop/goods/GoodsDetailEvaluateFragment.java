package com.yizhen.shop.goods;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.model.goods.Evaluate;
import com.yizhen.shop.model.goods.EvaluateList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.List;

import static com.yizhen.shop.R.id.imv_comment_headimg;


/**
 * Created by lewis on 2017/7/25.
 */

public class GoodsDetailEvaluateFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int pagerNum=1;
    private int goods_id;
    private BaseQuickAdapter<Evaluate, BaseViewHolder> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swl_rv;
    }

    public static GoodsDetailEvaluateFragment newInstance(int goods_id) {
        GoodsDetailEvaluateFragment fragment = new GoodsDetailEvaluateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("goods_id", goods_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        goods_id = getArguments().getInt("goods_id");
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view,rv);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Evaluate, BaseViewHolder>(R.layout.item_comment) {
            @Override
            protected void convert(BaseViewHolder helper, Evaluate item) {
                ImageLoader.loadHeadImage(getActivity(), item.user_headimg, (ImageView) helper.getView(imv_comment_headimg), -1);
                helper.setText(R.id.tv_comment_nickname, item.member_name);
                try {
                    helper.setText(R.id.tv_comment_time, DateUtils.tenLongToString(item.addtime, DateUtils.yyyyMMDD2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                helper.setText(R.id.tv_comment_content, item.content);
                helper.setText(R.id.tv_comment_spec, item.goods_name);
                LinearLayout ll_comment_imgs = helper.getView(R.id.ll_comment_imgs);
                ll_comment_imgs.removeAllViews();
                if (item.image != null && item.image.size() > 0) {
                    ((View) ll_comment_imgs.getParent()).setVisibility(View.VISIBLE);
                    for (String img : item.image) {
                        ImageView imageView = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.item_comment_img, ll_comment_imgs, false);
                        ImageLoader.load(getActivity(), img, imageView);
                        ll_comment_imgs.addView(imageView);
                    }
                } else {
                    ((View) ll_comment_imgs.getParent()).setVisibility(View.GONE);
                }
            }
        };
    }

    private void get_evaluate_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_EVALUATE_LIST);
        request.add("goods_id", goods_id);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    EvaluateList evaluateList = netBaseBean.parseObject(EvaluateList.class);
                    pagerNum = evaluateList.next_page;
                    List<Evaluate> list = evaluateList.list;
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
        super.loadData();
        onRefresh();
    }

    @Override
    public void onLoadMoreRequested() {
        get_evaluate_list(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_evaluate_list(1);
    }
}
