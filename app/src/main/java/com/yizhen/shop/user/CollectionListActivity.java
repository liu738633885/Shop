package com.yizhen.shop.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.goods.Collection;
import com.yizhen.shop.model.goods.CollectionList;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.TitleBar;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class CollectionListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private TitleBar titleBar;
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private int pagerNum = 1;
    private BaseQuickAdapter<Collection, BaseViewHolder> adapter;
    public static String TAG = CollectionListActivity.class.getSimpleName();

    @Override
    protected int getContentViewId() {
        return R.layout.layut_titlebar_swl_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.setCenterText("我的收藏");
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view, rv);
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Collection, BaseViewHolder>(R.layout.item_order_goods) {
            @Override
            protected void convert(BaseViewHolder helper, final Collection item) {
                helper.setVisible(R.id.ll_num, false);
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_spec, item.introduction);
                helper.setText(R.id.tv_price, "¥ " + item.log_price);
                ImageLoader.load(bContext, item.goods_image, (ImageView) helper.getView(R.id.imv));
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(bContext, item.fav_id);
                    }
                });
                helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(bContext).setMessage("确定取消收藏?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.CANCEL_COLLECTION_GOODS);
                                request.add("goods_id", item.fav_id);
                                CallServer.getRequestInstance().addWithLogin(bContext, 0x01, request, new HttpListenerCallback() {
                                    @Override
                                    public void onSucceed(int what, NetBaseBean netBaseBean) {
                                        T.showShort(bContext, netBaseBean.getMessage());
                                        if (netBaseBean.isSuccess()) {
                                            onRefresh();
                                        }
                                    }

                                    @Override
                                    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                                    }
                                }, true, true);
                            }
                        }).setNegativeButton("否", null).create().show();
                        return true;
                    }
                });
            }
        };
    }

    @Override
    public void onLoadMoreRequested() {
        collection_list(pagerNum);
    }

    @Override
    public void onRefresh() {
        collection_list(1);
    }


    private void collection_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.COLLECTION_LIST);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    CollectionList collectionList = netBaseBean.parseObject(CollectionList.class);
                    pagerNum = collectionList.next_page;
                    List<Collection> list = collectionList.list;
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e != null && e.getWhere().equals(TAG)) {
            onRefresh();
        }
    }
}
