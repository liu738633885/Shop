package com.yizhen.shop.model.auction;

import com.yizhen.shop.model.category.Category;
import com.yizhen.shop.model.goods.Goods;

import java.util.List;

/**
 * Created by lewis on 2017/9/28.
 */

public class AuctionHome {
    public List<Category> cate_list;//三个分类列表
    public List<Goods> today_list;// 今日拍卖
    public List<Goods> hot_list; // 热门推荐
    public List<Goods> ready_list;// 拍卖预告
    public List<Goods> push_list;// 为你推荐
}
