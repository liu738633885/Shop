package com.yizhen.shop.auction;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.category.CategoryGoodsActivity;
import com.yizhen.shop.model.category.Category;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.TabView;

public class AuctionCateActivity extends BaseActivity {
    private VerticalTabLayout tabLayout;
    private RecyclerView rv;
    private View headView;
    private TextView tv_name;
    private ImageView imv_banner;
    private BaseQuickAdapter<Category, BaseViewHolder> adapter;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_auction_cate;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabLayout = (VerticalTabLayout) findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                updateRightUI(showList.get(position));
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(bContext, 3));
        initAdapter();
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(bContext).inflate(R.layout.headview_category, (ViewGroup) rv.getParent(), false);
        tv_name = headView.findViewById(R.id.tv_name);
        imv_banner = headView.findViewById(R.id.imv_banner);
        adapter.addHeaderView(headView);
        get_goods_category_list();
    }
    private void initAdapter() {
        adapter = new BaseQuickAdapter<Category, BaseViewHolder>(R.layout.item_category) {
            @Override
            protected void convert(BaseViewHolder helper, final Category item) {
                ImageLoader.load(bContext, item.category_pic, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv, item.category_name);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CategoryGoodsActivity.goTo(bContext, item.category_id, item.category_name);
                    }
                });
            }
        };
    }

    private void updateRightUI(final Category category) {
        adapter.setNewData(category.childs);
        tv_name.setText(category.category_name);
        ImageLoader.loadAutoHeight(bContext, category.category_pic, imv_banner, 3);
        imv_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryGoodsActivity.goTo(bContext, category.category_id, category.category_name);
            }
        });

    }

    private void initTabLayout() {
        tabLayout.setTabAdapter(new TabAdapter() {
            @Override
            public int getCount() {
                return showList.size();
            }

            @Override
            public TabView.TabBadge getBadge(int position) {
                return null;
            }

            @Override
            public TabView.TabIcon getIcon(int position) {
                return null;
            }

            @Override
            public TabView.TabTitle getTitle(int position) {
                return new TabView.TabTitle.Builder()
                        .setContent(showList.get(position).category_name)
                        .setTextSize(14)
                        .setTextColor(ContextCompat.getColor(bContext, R.color.colorPrimary), ContextCompat.getColor(bContext, R.color.gray01))
                        .build();
            }

            @Override
            public int getBackground(int position) {
                return 0;
            }
        });
    }


    List<Category> showList;
    Map<Integer, Category> map;

    private void get_goods_category_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_AUCTION_GOODS_CATEGORY_LIST);
        CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Category> list = netBaseBean.parseList(Category.class);
                    if (list != null) {
                        //showList = new ArrayList<Category>();
                        map = new LinkedHashMap<Integer, Category>();
                        for (Category category : list) {
                            if (category.level == 1) {
                                if (map.get(category.category_id) != null && map.get(category.category_id).childs != null && map.get(category.category_id).childs.size() > 0) {
                                    category.childs.addAll(map.get(category.category_id).childs);
                                }
                                map.put(category.category_id, category);
                            } else {
                                try {
                                    if (map.get(category.pid) == null) {
                                        map.put(category.pid, new Category());
                                    }
                                    map.get(category.pid).childs.add(category);
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                            Logger.e(map.toString());
                        }

                        showList = new ArrayList<Category>(map.values());
                        initTabLayout();
                        updateRightUI(showList.get(0));
                    }

                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }
}
