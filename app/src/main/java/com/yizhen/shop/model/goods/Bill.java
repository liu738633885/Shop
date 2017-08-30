package com.yizhen.shop.model.goods;

/**
 * Created by lewis on 2017/8/23.
 */

public class Bill {
    public int  bill_id;
    public int  uid;
    public int  type;//发票类型，1为电子普通发票，2为纸质普通发票
    public int  bill_rise_type;//发票抬头类型，1为个人、2为单位
    public String username;
    public String company_name;
    public String company_code;
    public String bill_content;
    public String phone;
    public String email;
}
