package com.yizhen.shop.model.address;

import java.util.List;

/**
 * Created by lewis on 2017/8/1.
 */

public class City extends AddressBean{
    public int cityCode;
    public String city;
    public int superCode;
    public List<County> counties;
}
