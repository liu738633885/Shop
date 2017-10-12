package com.yizhen.shop.auction;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.goods.GoodsListActivity;
import com.yizhen.shop.model.auction.AuctionTheme;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.home.Home;
import com.yizhen.shop.util.DensityUtil;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.TimeTextView;

import java.util.ArrayList;


/**
 * Created by lewis on 2017/7/24.
 */

public class AuctionHomeBeanAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

    public AuctionHomeBeanAdapter() {
        super(new ArrayList<Object>());
    }

    @Override
    protected void convert(BaseViewHolder helper, final Object cell) {
        if (cell instanceof Goods) {
            final Goods item = (Goods) cell;
            if (item.type == Home.TYPE_TITLE) {
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
            } else if (item.type == Home.TYPE_AUCTION_MATCH) {
                helper.setText(R.id.tv1, item.goods_name);
                helper.setText(R.id.tv2, "当前 ¥ " + item.auction_price);
                ImageLoader.loadHome(mContext, item.goods_img, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv3, item.offers + "次出价");
                TimeTextView tv4 = helper.getView(R.id.tv4);
                //tv4.setTimes(item.e_time*1000);
                tv4.setTimes(item.s_time, item.e_time);
                // helper.setText(R.id.tv4, "距结束还有");
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goToWithLogIn(mContext, Constants.AUCTION_DETAIL_WEB + item.goods_id, "拍卖详情");
                    }
                });
            } else if (item.type == Home.TYPE_AUCTION_WRAP) {
                helper.setText(R.id.tv1, item.goods_name);
                helper.setText(R.id.tv2, "¥" + item.starting_price);
                ImageLoader.loadHome(mContext, item.goods_img, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv3, "起拍价");
                TimeTextView tv4 = helper.getView(R.id.tv4);
                //tv4.setTimes(item.e_time*1000);
                tv4.setTimes(item.s_time, item.e_time);
                // helper.setText(R.id.tv4, "距结束还有");
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebViewActivity.goToWithLogIn(mContext, Constants.AUCTION_DETAIL_WEB + item.goods_id, "拍卖详情");
                    }
                });
            }
        } else if (cell instanceof AuctionTheme) {
            final AuctionTheme item = (AuctionTheme) cell;
            helper.setText(R.id.tv1, item.theme_name);
            LinearLayout ll = helper.getView(R.id.ll);
            ll.removeAllViews();
            int with = (DensityUtil.getWidth(mContext) - DensityUtil.dip2px(mContext, 64)) / 4;
            //int with = ll.getWidth()/2;
            if (item.theme_goods_list != null && item.theme_goods_list.size() > 0) {
                for (int i = 0; i < item.theme_goods_list.size(); i++) {
                    ImageView imv = new ImageView(mContext);
                    //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(with, with);
                    if (i == 0) {
                        params.setMargins(0, 0, DensityUtil.dip2px(mContext, 10), 0);
                    }
                    imv.setLayoutParams(params);
                    ImageLoader.load(mContext, item.theme_goods_list.get(i).goods_img, imv);
                    if (i <= 1) {
                        ll.addView(imv);
                    }
                }
            }
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AuctionListActivity.goTo(mContext, item.theme_name, item.theme_id, 1);
                }
            });

        }
    }

    @Override
    protected int getDefItemViewType(int position) {
        if (getItem(position) instanceof Goods) {
            return ((Goods) getItem(position)).type;
        } else if (getItem(position) instanceof AuctionTheme) {
            return Home.TYPE_AUCTION_THEME;
        } else {
            return Home.TYPE_AUCTION_CATE;
        }
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Home.TYPE_TITLE) {
            return new FullViewHolder(this.getItemView(R.layout.item_home_title, parent));
        } else if (viewType == Home.TYPE_AUCTION_THEME) {
            return new GridViewHolder(this.getItemView(R.layout.item_auction_theme, parent));
        } else if (viewType == Home.TYPE_AUCTION_MATCH) {
            return new FullViewHolder(this.getItemView(R.layout.item_auction_match, parent));
        } else if (viewType == Home.TYPE_AUCTION_WRAP) {
            return new GridViewHolder(this.getItemView(R.layout.item_auction, parent));
        } else {
            return new GridViewHolder(this.getItemView(R.layout.item_auction_match, parent));
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
