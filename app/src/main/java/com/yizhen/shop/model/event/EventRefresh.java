package com.yizhen.shop.model.event;

import static android.R.attr.data;

/**
 * Created by lewis on 16/7/26.
 */
public class EventRefresh {
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_LOGOUT = "logout";
    public static final String ACTION_REGISTER = "register";
    public static final String ACTION_REFRESH = "refresh";
    public static final String ACTION_CHAT = "chat";
    public static final String ACTION_USER_CHANGE = "user_change";
    public static final String ACTION_CITY = "city";
    public static final String ACTION_WECHAT_PAY = "wechat_pay";
    public static final String ACTION_CLUB_USER = "club_user";
    public static final String ACTION_CLUB_USER_CHANGE = "club_user_change";
    public static final String PAY_OK = "pay";
    public static final String PAY_BACK = "pay_back";
    private String action;
    private Object data;
    private String where;

    public EventRefresh(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }



    public EventRefresh(String action, Object data, String where) {
        this.action = action;
        this.data = data;
        this.where = where;
    }

    public EventRefresh(Object data, String where) {
        this.data = data;
        this.where = where;
    }

    public EventRefresh(String action, String where) {
        this.action = action;
        this.where = where;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
