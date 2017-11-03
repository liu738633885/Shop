package com.yizhen.shop.home;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.goods.GoodsDetailActivity;
import com.yizhen.shop.goods.GoodsListActivity;
import com.yizhen.shop.goods.TopicActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.home.Home;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.DiscountTextView;

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
                        case "精品推荐":
                            GoodsListActivity.goTo(mContext, "精品推荐", Constants.GET_OPENING_GOODS);
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
            helper.setText(R.id.tv_desc, item.introduction);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoodsDetailActivity.goTo(mContext, item.goods_id);
                }
            });
        } else if (item.type == Home.TYPE_DISCOUNT) {
            DiscountTextView tv2 = helper.getView(R.id.tv2);
            tv2.setTimes(item.end_time);
            if (item.next_time != 0) {
                try {
                    helper.setText(R.id.tv3, "下一场 " + DateUtils.tenLongToString(item.next_time, DateUtils.hhmm) + " 开始");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ImageLoader.loadHome(mContext, item.pic_cover_mid, (ImageView) helper.getView(R.id.imv));
            TextView tv_price_yuan = helper.getView(R.id.tv_price_yuan);
            tv_price_yuan.setText(item.price);
            tv_price_yuan.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置中划线并加清晰
            helper.setText(R.id.tv_price, item.promotion_price);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebViewActivity.goTo(mContext, Constants.XIAN_SHI_GOU, "限时购");
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
        } else if (viewType == Home.TYPE_DISCOUNT) {
            return new FullViewHolder(this.getItemView(R.layout.item_home_discount, parent));
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
