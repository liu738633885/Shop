package com.yizhen.shop.model.goods;

import java.util.List;

/**
 * Created by lewis on 2017/7/27.
 */

public class GoodsDetail {
    public int goods_id;
    public String goods_name;
    public String introduction;
    public String market_price;
    public String price;
    public String promotion_price;
    public String keywords;
    public String pic_cover_micro;
    public String pic_cover_mid;
    public String pic_cover_small;
    public List<Spec> spec_list;
}
