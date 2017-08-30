package com.yizhen.shop.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.model.goods.Coupon;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.List;

import yyydjk.com.library.CouponView;

/**
 * Created by lewis on 2017/8/17.
 */

public class MyCouponListFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int pagerNum = 1;
    private int state;
    private BaseQuickAdapter<Coupon, BaseViewHolder> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_swl_rv;
    }

    public static MyCouponListFragment newInstance(int state) {
        MyCouponListFragment fragment = new MyCouponListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        state = getArguments().getInt("state");
        swl = view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_view, rv);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Coupon, BaseViewHolder>(R.layout.item_coupon) {
            @Override
            protected void convert(BaseViewHolder helper, final Coupon item) {
                helper.setText(R.id.tv1, "¥" + item.money);
                helper.setText(R.id.tv2, "满" + item.at_least + "使用");
                try {
                    helper.setText(R.id.tv3, "有效期" + DateUtils.tenLongToString(item.start_time, DateUtils.yyyyMMDD2) + " - " + DateUtils.tenLongToString(item.end_time, DateUtils.yyyyMMDD2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CouponView couponView = helper.getView(R.id.couponView);
                if (state == 1) {
                    helper.setText(R.id.tv_get, "未使用");
                } else if (state == 2) {
                    helper.setText(R.id.tv_get, "已使用");
                } else if (state == 3) {
                    helper.setText(R.id.tv_get, "已过期");
                    couponView.setBackgroundColor(0xffaaaaaa);
                }

            }
        };
    }

    private void my_coupon_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.MY_COUPON_LIST);
        request.add("state", state);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Coupon> couponList = netBaseBean.parseList(Coupon.class);
                    adapter.setNewData(couponList);
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
    public void onRefresh() {
        my_coupon_list();
    }
}
