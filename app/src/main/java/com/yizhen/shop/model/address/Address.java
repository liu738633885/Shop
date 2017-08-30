package com.yizhen.shop.model.address;

/**
 * Created by lewis on 2017/8/1.
 */

public class Address {
    public int id;
    public int is_default;//是否为默认 1为默认 0为不默认
    public int province_id;
    public int city_id;
    public int district_id;
    public String consigner;
    public String mobile;
    public String address;
    public String province;
    public String city;
    public String district;
}
