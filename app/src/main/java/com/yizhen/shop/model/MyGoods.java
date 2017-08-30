package com.yizhen.shop.model;

import java.util.List;

/**
 * Created by lewis on 2017/7/19.
 */

public class MyGoods {
    public String title;
    public String img;
    public List<Spec> specs;
    public String spec_title;
    public String desc = "简单的描述";
    public double price;

    public MyGoods(String title, String img, String spec_title, double price, int storage) {
        this.title = title;
        this.img = img;
        this.spec_title = spec_title;
        this.price = price;
        this.storage = storage;
    }
    public MyGoods(String title, String img, String spec_title, double price, int storage, String desc) {
        this.title = title;
        this.img = img;
        this.spec_title = spec_title;
        this.price = price;
        this.storage = storage;
        this.desc = desc;
    }

    class Spec {
        public String spec_title;
        public double price;
    }

    public int num = 1;
    public int storage;
    public boolean checked;
    public boolean checkedEdit;
}
