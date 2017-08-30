package com.yizhen.shop.model;

import android.app.Activity;

/**
 * Created by lewis on 2017/8/4.
 */

public class MineItem {
    public String name;
    public int icon;
    public Class activity;

    public MineItem(String name, int icon, Class activity) {
        this.name = name;
        this.icon = icon;
        this.activity = activity;
    }
}
