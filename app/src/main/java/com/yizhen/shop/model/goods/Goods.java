package com.yizhen.shop.model.goods;

import com.yizhen.shop.model.order.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lewis on 2017/7/24.
 */

public class Goods implements Serializable {

    //home
    public int type;
    public String title;
    public String title_img;
    public int title_id;
    public String tv1;
    public String tv2;
    //goods
    public int goods_id;
    public String goods_name;
    public String introduction;
    public String market_price;
    public String price;
    public String promotion_price;
    public String keywords;
    public String sku_name;
    public String sku_id;
    public String pic_cover_micro;
    public String pic_cover_mid;
    public String pic_cover_big;
    public String pic_cover_small;
    public String goods_picture;
    public String goods_img;
    public List<Spec> spec_list;
    public List<Sku> sku_list;
    public List<Image> img_list;
    public int promotion_type;//活动类型，0为未参加活动，1为团购，2为限时抢购
    public int comment_num;//评论数
    public int sales;
    public int is_collection;//0为未收藏，1为已收藏
    public int coupon_count;//优惠券个数
    public PromotionInfo promotion_info;//活动信息
    public Consult consult;
    public int give_point;
    public int state;//1为正常状态
    public int category_id;//分类 id
    public Evaluate evaluate_info;
    public int num;
    public int refund_status;//0 未申请退款 1、买家申请退款，2、等待买家退货，3、等待卖家确认收货，4、等待卖家确认退款，5、退款已成功，-1、退款已拒绝，-2、退款已关闭，-3、退款申请不通过
    //order
    public int order_goods_id;
    public List<Operation> refund_operation;//退款按钮
    //order_evaluate
    public int evaluate_scores;
    public String evaluate_content;
    public List<String> evaluate_image_list;
    //拍卖
    public String auction_price;
    public String starting_price;
    public long s_time;
    public long e_time;
    public int offers;
    public int status;//1进行中  2已出局 3已获拍
    public int margin_id;
    public String margin_money;
    public int pay_type;// 支付类型 1微信 2支付宝
    public long pay_time;
    public int refund_type;//退款方式 1微信 2支付宝
    public long refund_time;
    public int order_id;
    public int margin_is_refund;//释放状态 保证金是否释放 0否 1是
    public String margin_refund_msg;//释放状态文字描述
    //限时购
    public long end_time;
    public long next_time;

    public Goods() {
    }

    public Goods(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public Goods(int type, String title, String title_img, int title_id) {
        this.type = type;
        this.title = title;
        this.title_img = title_img;
        this.title_id = title_id;
    }

}
