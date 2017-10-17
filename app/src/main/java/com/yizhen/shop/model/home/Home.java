package com.yizhen.shop.model.home;

import com.yizhen.shop.model.goods.Goods;

import java.util.List;

/**
 * Created by lewis on 2017/7/24.
 */

public class Home {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_GOODS_KAIGUANG = 1;
    public static final int TYPE_TITLE_NEW = 2;
    public static final int TYPE_GOODS_NEW = 3;
    public static final int TYPE_TITLE_SPECIAL = 4;
    public static final int TYPE_AUCTION_THEME = 5;
    public static final int TYPE_AUCTION_CATE = 6;
    public static final int TYPE_AUCTION_MATCH = 7;
    public static final int TYPE_AUCTION_WRAP = 8;
    public static final int TYPE_DISCOUNT = 9;
    //新品首发
    public List<Goods> new_goods_list;
    //开光直供
    public List<Goods> direct_goods_list;
    public SpecialList special_list;
    public Discount discount;

    public class SpecialList {
        public int topic_id;
        public String image;
        public String title;
        public String describe;
        public List<Goods> goods_list;
    }
    public class Discount{
        public long end_time;
        public long next_time;
        public List<Goods> goods_list;
    }
}
