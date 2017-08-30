package com.yizhen.shop.goods;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.GoodsList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.CommonUtils;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.SearchBarUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private TextView cancel;
    private View back;
    private EditText edt_query;
    private ViewGroup rl_record;
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int pagerNum = 1;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private String q;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        cancel = (TextView) findViewById(R.id.cancel);
        back = findViewById(R.id.back);
        rl_record = (ViewGroup) findViewById(R.id.rl_record);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_query.getText().toString().length() == 0 || back.getVisibility() != View.VISIBLE) {
                    finish();
                } else {
                    cancel.setVisibility(View.GONE);
                    CommonUtils.hideSoftInput(bContext, view);
                }
            }
        });
        edt_query = SearchBarUtils.init(this, new SearchBarUtils.OnSearchListener() {
            @Override
            public void onSearch(String string) {
                cancel.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                q = string;
                onRefresh();
            }

            @Override
            public void cleanSearch() {
                cancel.setVisibility(View.VISIBLE);
                q = "";
                onRefresh();
            }
        });
        edt_query.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    cancel.setVisibility(View.VISIBLE);
                    rl_record.setVisibility(View.VISIBLE);
                } else {
                    // 此处为失去焦点时的处理内容
                }
            }
        });
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(bContext, 2));
        rv.setBackgroundColor(Color.WHITE);
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_view, rv);
        adapter.setOnLoadMoreListener(this, rv);
    }

    private void get_search_goods(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_SEARCH_GOODS);
        request.add("pageno", num);
        if (TextUtils.isEmpty(q)) {
            adapter.setNewData(new ArrayList<Goods>());
            return;
        } else {
            request.add("q", q);
        }
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

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_goods) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
                ImageLoader.loadHome(mContext, item.pic_cover_big, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv_desc, item.keywords);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(mContext, item.goods_id);
                    }
                });
            }
        };
    }

    @Override
    public void onLoadMoreRequested() {
        get_search_goods(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_search_goods(1);
    }
}
