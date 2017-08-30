package com.yizhen.shop.model.category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/7/31.
 */

public class Category {
    public int category_id;
    public int pid;
    public int level;
    public int is_visible;
    public int attr_id;
    public int sort;
    public String category_name;
    public String short_name;
    public String attr_name;
    public String keywords;
    public String description;
    public String category_pic;
    public List<Category> childs = new ArrayList<>();
}
