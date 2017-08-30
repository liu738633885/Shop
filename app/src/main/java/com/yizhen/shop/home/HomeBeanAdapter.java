package com.yizhen.shop.home;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.goods.GoodsListActivity;
import com.yizhen.shop.goods.TopicActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.home.Home;
import com.yizhen.shop.util.imageloader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by lewis on 2017/7/24.
 */

public class HomeBeanAdapter extends BaseQuickAdapter<Goods, BaseViewHolder> {

    public HomeBeanAdapter() {
        super(new ArrayList<Goods>());
    }

    @Override
    protected void convert(BaseViewHolder helper, final Goods item) {
        if (item.type == Home.TYPE_TITLE) {
            helper.setVisible(R.id.next, !item.title.equals("猜你喜欢"));
            helper.setText(R.id.tv, item.title);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (item.title) {
                        case "新品首发":
                            GoodsListActivity.goTo(mContext, "新品首发", Constants.GET_NEW_GOODS);
                            break;
                        case "开光直供":
                            GoodsListActivity.goTo(mContext, "开光直供", Constants.GET_OPENING_GOODS);
                            break;
                    }
                }
            });
        } else if (item.type == Home.TYPE_TITLE_NEW) {
            ImageLoader.loadAutoHeight(mContext, item.title_img, (ImageView) helper.getView(R.id.imv), 0);
        } else if (item.type == Home.TYPE_TITLE_SPECIAL) {
            helper.setText(R.id.tv, item.title);
            helper.setText(R.id.tv1, item.tv1);
            helper.setText(R.id.tv2, item.tv2);
            ImageLoader.loadAutoHeight(mContext, item.title_img, (ImageView) helper.getView(R.id.imv), 0);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转到专题页面
                    TopicActivity.goTo(mContext, item.title_id, item.title);
                }
            });
        } else if (item.type == Home.TYPE_GOODS_KAIGUANG) {
            helper.setText(R.id.tv_title, item.goods_name);
            helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
            ImageLoader.loadHome(mContext, item.pic_cover_big, (ImageView) helper.getView(R.id.imv));
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoodsDetailActivity.goTo(mContext, item.goods_id);
                }
            });
        } else if (item.type == Home.TYPE_GOODS_NEW) {
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
    }

    @Override
    protected int getDefItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Home.TYPE_TITLE) {
            return new FullViewHolder(this.getItemView(R.layout.item_home_title, parent));
        } else if (viewType == Home.TYPE_GOODS_KAIGUANG) {
            return new GridViewHolder(this.getItemView(R.layout.item_home_kaiguang, parent));
        } else if (viewType == Home.TYPE_TITLE_NEW) {
            return new FullViewHolder(this.getItemView(R.layout.item_home_title_new, parent));
        } else if (viewType == Home.TYPE_TITLE_SPECIAL) {
            return new FullViewHolder(this.getItemView(R.layout.item_home_title_special, parent));
        } else {
            return new GridViewHolder(this.getItemView(R.layout.item_goods, parent));
        }
    }

    private class GridViewHolder extends BaseViewHolder {
        protected GridViewHolder(View view) {
            super(view);
        }
    }

    private class FullViewHolder extends BaseViewHolder {
        protected FullViewHolder(View view) {
            super(view);
            setFullSpan(this);
        }
    }
}
