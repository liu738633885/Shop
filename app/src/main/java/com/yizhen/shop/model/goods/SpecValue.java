package com.yizhen.shop.model.goods;

/**
 * Created by lewis on 2017/7/27.
 */

public class SpecValue {
    public String spec_value_name;
    public String spec_name;
    public int spec_id;
    public int spec_value_id;
    public int spec_show_type;
    public String spec_value_data;

    @Override
    public String toString() {
        return "SpecValue{" +
                "spec_value_name='" + spec_value_name + '\'' +
                '}';
    }
}
