package com.yizhen.shop.message;

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
import com.yizhen.shop.goods.TopicActivity;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.sms.Message;
import com.yizhen.shop.model.sms.MessageList;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.MyViewUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

import java.util.List;

public class MessageListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private BaseQuickAdapter<Message, BaseViewHolder> adapter;
    private int pagerNum = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(bContext));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view, rv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Message, BaseViewHolder>(R.layout.item_message) {
            @Override
            protected void convert(BaseViewHolder helper, final Message item) {
                helper.setText(R.id.tv_time, item.create_time);
                helper.setText(R.id.tv1, item.title);
                helper.setText(R.id.tv2, item.describe);
                ImageLoader.loadAutoHeight(bContext, item.image, (ImageView) helper.getView(R.id.imv), 0);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (item.type) {
                            case 1:
                                TopicActivity.goTo(bContext,item.id,item.title);
                                break;
                            case 2:
                                GoodsDetailActivity.goTo(bContext,item.id);
                                break;
                        }
                    }
                });
            }
        };
    }

    private void get_message_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.SMS_ACTIVITY_LIST);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    MessageList messageList = netBaseBean.parseObject(MessageList.class);
                    pagerNum = messageList.next_page;
                    List<Message> list = messageList.list;
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    @Override
    public void onLoadMoreRequested() {
        get_message_list(pagerNum);
    }

    @Override
    public void onRefresh() {
        get_message_list(1);
    }
}
