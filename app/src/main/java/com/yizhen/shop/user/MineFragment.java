package com.yizhen.shop.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.model.MineItem;
import com.yizhen.shop.order.MyRefundListActivity;
import com.yizhen.shop.order.OrderListActivity;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.util.manager.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/7/14.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView rv;
    private View headView;
    private ImageView imv, imv_level_icon, imv_level_background;
    private TextView tv_name, tv_level;
    private LinearLayout ll_my_order, ll_my_order_1, ll_my_order_2, ll_my_order_3, ll_my_order_4, ll_my_order_5;
    private BaseQuickAdapter<MineItem, BaseViewHolder> adapter;
    //private int backmode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        initAdapter();
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_mine, (ViewGroup) rv.getParent(), false);
        ll_my_order = headView.findViewById(R.id.ll_my_order);
        ll_my_order_1 = headView.findViewById(R.id.ll_my_order_1);
        ll_my_order_2 = headView.findViewById(R.id.ll_my_order_2);
        ll_my_order_3 = headView.findViewById(R.id.ll_my_order_3);
        ll_my_order_4 = headView.findViewById(R.id.ll_my_order_4);
        ll_my_order_5 = headView.findViewById(R.id.ll_my_order_5);
        ll_my_order.setOnClickListener(this);
        ll_my_order_1.setOnClickListener(this);
        ll_my_order_2.setOnClickListener(this);
        ll_my_order_3.setOnClickListener(this);
        ll_my_order_4.setOnClickListener(this);
        ll_my_order_5.setOnClickListener(this);
        imv = headView.findViewById(R.id.imv);
        imv_level_icon = headView.findViewById(R.id.imv_level_icon);
        imv_level_background = view.findViewById(R.id.imv_level_background);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //backmode = 1;
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
            }
        });
        tv_name = headView.findViewById(R.id.tv_name);
        tv_level = headView.findViewById(R.id.tv_level);
        adapter.addHeaderView(headView);
        updateUI();
        initData();
    }

    private void initData() {
        List<MineItem> list = new ArrayList<>();
        //橙红色 #FFfd8568
        //橙色 FFf6b357
        //黄色 f8b851
        //蓝色 #5d99e0
        list.add(new MineItem("我的拍卖", R.drawable.ic_paimai, null));
        list.add(new MineItem("优惠券", R.drawable.ic_youhuijuan, MyCouponListActivity.class));
        list.add(new MineItem("积分", R.drawable.ic_jifen, MyPointActivity.class));
        //list.add(new MineItem("邀请好友", R.drawable.ic_yaoqing, null));
        list.add(new MineItem("地址管理", R.drawable.ic_dizhi, AddressListActivity.class));
        list.add(new MineItem("足迹", R.drawable.ic_zuji, null));
        list.add(new MineItem("我的收藏", R.drawable.ic_shoucang, CollectionListActivity.class));
        list.add(new MineItem("我的提问", R.drawable.ic_tiwen, MyConsultListActivity.class));
        list.add(new MineItem("客服", R.drawable.ic_kefu, null));
        list.add(new MineItem("意见反馈", R.drawable.ic_yijian, FeedbackActivity.class));
        list.add(new MineItem("设置", R.drawable.ic_shezhi, SettingActivity.class));
        list.add(new MineItem());
        list.add(new MineItem());
        adapter.setNewData(list);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<MineItem, BaseViewHolder>(R.layout.item_mine) {
            @Override
            protected void convert(BaseViewHolder helper, final MineItem item) {
                helper.setText(R.id.tv, item.name);
                final ImageView imv = helper.getView(R.id.imv);
                imv.setImageResource(item.icon);
                if (item.activity != null) {
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(), item.activity));
                        }
                    });
                }

            }
        };
    }

    private void updateUI() {
        tv_name.setText(UserManager.getNickname());
        tv_level.setText(UserManager.getLevelName());
        ImageLoader.loadHeadImage(getActivity(), UserManager.getHeadImg(), imv, 0);
        ImageLoader.load(getActivity(), UserManager.getLevelIcon(), imv_level_icon);
        ImageLoader.load(getActivity(), UserManager.getKeyLevelBackground(), imv_level_background);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
       /* switch (backmode) {
            case 1:

                break;
            case 2:
                break;
        }
        backmode = 0;*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_my_order:
                OrderListActivity.goTo(getActivity(), 0);
                break;
            case R.id.ll_my_order_1:
                OrderListActivity.goTo(getActivity(), 1);
                break;
            case R.id.ll_my_order_2:
                OrderListActivity.goTo(getActivity(), 2);
                break;
            case R.id.ll_my_order_3:
                OrderListActivity.goTo(getActivity(), 3);
                break;
            case R.id.ll_my_order_4:
                OrderListActivity.goTo(getActivity(), 4);
                break;
            case R.id.ll_my_order_5:
                startActivity(new Intent(getActivity(), MyRefundListActivity.class));
                break;
        }
    }
}
