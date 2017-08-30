package com.yizhen.shop.model.order;

import com.yizhen.shop.model.goods.Coupon;
import com.yizhen.shop.model.goods.Goods;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lewis on 2017/8/1.
 */

public class Order implements Serializable {
    public int order_id;
    public double total_money;
    public String order_money;
    public String refund_money;
    public String out_trade_no;
    public double discount_money;
    public List<Goods> goods_list;
    public List<Coupon> coupon_list;
    public List<Operation> member_operation;
    public String status_name;
    public int order_status; //-1为退款中，0待付款，1为待发货，2为已发货，3为已收货，4为已完成，5为已关闭
    public String receiver_address;
    public String receiver_name;
    public String receiver_mobile;
    public String payment_type_name;
    public long create_time;
    public int give_point;
    public String shipping_status_name;
    public String pay_money;
}
