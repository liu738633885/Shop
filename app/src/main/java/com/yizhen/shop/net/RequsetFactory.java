package com.yizhen.shop.net;


import android.content.Context;
import android.content.Intent;

import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.util.manager.UserManager;

/**
 * Created by lewis on 16/6/23.
 */
public class RequsetFactory {
    //目前所有的 url 都带 token
    //都带 city

    /**
     * @param url 默认的创建 NetBaseRequest
     *            验证登录
     * @return
     */
    public static NetBaseRequest creatBaseRequest(String url) {
        NetBaseRequest netBaseRequest = new NetBaseRequest(url);
        if (UserManager.isLogin()) {
            netBaseRequest.add("user_id", UserManager.getId());
            netBaseRequest.add("token", UserManager.getToken());
        }
        return netBaseRequest;
    }

    /*public static NetBaseRequest creatBaseRequest(String url, Context context) {
        NetBaseRequest netBaseRequest = new NetBaseRequest(url);
        if (UserManager.isLogin()) {
            netBaseRequest.add("user_id", UserManager.getId());
            netBaseRequest.add("token", UserManager.getToken());
            return netBaseRequest;
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
            return null;
        }
    }*/

    public static NetBaseRequest creatNoUidRequest(String url) {
        NetBaseRequest netBaseRequest = new NetBaseRequest(url);
        return netBaseRequest;
    }

}
