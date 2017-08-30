package com.yizhen.shop.util;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/2/15.
 */

public class MyViewUtils {
    public static void bindListWithNum(int pageno, int what, BaseQuickAdapter adapter, List list) {
        boolean isnodata = pageno == -1 | pageno == 0;
        if (what == 1) {
            adapter.setNewData(list);
        } else {
            adapter.addData(list);
        }
        if (isnodata) {
            adapter.loadMoreEnd();
        } else {
            adapter.loadMoreComplete();
        }
    }

    public static void bindListWithNum(int pageno, int what, BaseQuickAdapter adapter, List list, View view) {
        boolean isnodata = pageno == -1 | pageno == 0;
        if (list != null && list.size() > 0) {
            if (what == 1) {
                adapter.setNewData(list);
            } else {
                adapter.addData(list);
            }
        } else {
            adapter.loadMoreEnd();
        }
        if (isnodata) {
            adapter.loadMoreEnd();
            view.setVisibility(what == 0 ? View.VISIBLE : View.GONE);
        } else {
            view.setVisibility(View.GONE);
            adapter.loadMoreComplete();
        }
    }

    public static void bindList(BaseQuickAdapter adapter, List list) {
        if (list != null && list.size() > 0) {
            adapter.setNewData(list);
            adapter.loadMoreComplete();
        } else {
            adapter.setNewData(new ArrayList());
            adapter.loadMoreEnd();
        }
    }
}
