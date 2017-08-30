package com.yizhen.shop.model.address;

import java.util.List;

/**
 * Created by lewis on 2017/8/1.
 */

public class Province extends AddressBean {
    public int provinceCode;
    public String province;
    public List<City> cities;
}
