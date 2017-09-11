package com.yizhen.shop.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.model.goods.Consult;
import com.yizhen.shop.model.goods.ConsultList;
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

public class MyConsultListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private BaseQuickAdapter<Consult, BaseViewHolder> adapter;
    private TitleBar titleBar;
    private int pagerNum = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.layut_titlebar_swl_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.setCenterText("我的提问");
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
        adapter = new BaseQuickAdapter<Consult, BaseViewHolder>(R.layout.item_consult_mine) {
            @Override
            protected void convert(BaseViewHolder helper, final Consult item) {
                try {
                    helper.setText(R.id.tv_consult_add_time, DateUtils.tenLongToString(item.consult_addtime, DateUtils.yyyyMMddHHmmss));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //-----
                helper.setVisible(R.id.ll_num, false);
                ImageLoader.load(bContext, item.pic_cover_mid, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_spec, item.introduction);
                helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
                //----
                helper.setGone(R.id.tv_consult_right, false);
                helper.setText(R.id.tv_wen, item.consult_content);
                if (!TextUtils.isEmpty(item.consult_reply)) {
                    helper.setText(R.id.tv_da, item.consult_reply);
                } else {
                    helper.setText(R.id.tv_da, "还没有回答");
                }

                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(bContext, item.goods_id);
                    }
                });
            }
        };
    }

    private void consult_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CONSULT_LIST);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    ConsultList consultList = netBaseBean.parseObject(ConsultList.class);
                    pagerNum = consultList.next_page;
                    List<Consult> list = consultList.list;
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Override
    public void onRefresh() {
        consult_list(1);
    }

    @Override
    public void onLoadMoreRequested() {
        consult_list(pagerNum);
    }
}
