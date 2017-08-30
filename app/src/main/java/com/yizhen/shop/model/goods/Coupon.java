package com.yizhen.shop.model.goods;

import java.io.Serializable;

/**
 * Created by lewis on 2017/7/31.
 */

public class Coupon implements Serializable {
    public int coupon_id;
    public int coupon_type_id;
    public int range_type;
    public String coupon_name;
    public String money;
    public String at_least;
    public long start_time;
    public long end_time;
}
