package com.yizhen.shop.model.order;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lewis on 2017/9/4.
 */

public class PayInfo {
    //支付宝
    public String return_str;
    //微信
    public String appid;
    public String noncestr;

    @JSONField(name = "package")
    public String packages;

    public String partnerid;
    public String prepayid;
    public long timestamp;
    public String sign;
}
