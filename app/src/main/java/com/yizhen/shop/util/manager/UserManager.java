package com.yizhen.shop.util.manager;

import android.content.Context;
import android.content.Intent;

import com.yizhen.shop.model.Login;
import com.yizhen.shop.model.UserInfo;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.util.SPUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lewis on 16/6/22.
 */
public class UserManager {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ISLOGIN = "isLogin";
    public static final String KEY_ID = "id";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_SEX = "sex";
    public static final String KEY_USER_HEADIMG = "user_headimg";
    public static final String KEY_MEMBER_LEVEL = "member_level";
    public static final String KEY_LEVEL_NAME = "level_name";
    public static final String KEY_POINT = "point";
    public static final String KEY_LEVEL_ICON = "level_icon";
    public static final String KEY_LEVEL_BACKGROUND = "level_background";
    public static final String KEY_IS_ADMIN = "is_admin";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_COMPANY_ID = "company_id";


    public static boolean isLogin() {
        return (boolean) SPUtils.getUserInstance().get(KEY_ISLOGIN, false);
    }

    public static boolean isLogin(Context context) {
        if (!isLogin()) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
        return isLogin();
    }

    public static boolean saveLoginInfo(Login login) {
        if (login == null) {
            return false;
        } else {
            SPUtils.getUserInstance().put(KEY_TOKEN, login.token);
            SPUtils.getUserInstance().put(KEY_ISLOGIN, true);
            SPUtils.getUserInstance().put(KEY_ID, login.user_id);
            SPUtils.getUserInstance().put(KEY_USER_HEADIMG, login.user_headimg);
            SPUtils.getUserInstance().put(KEY_NICKNAME, login.nick_name);
            SPUtils.getUserInstance().put(KEY_POINT, login.point);
            SPUtils.getUserInstance().put(KEY_LEVEL_NAME, login.level_name);
            SPUtils.getUserInstance().put(KEY_MEMBER_LEVEL, login.member_level);
            SPUtils.getUserInstance().put(KEY_LEVEL_ICON, login.level_icon);
            SPUtils.getUserInstance().put(KEY_LEVEL_BACKGROUND, login.level_background);
            return true;
        }
    }

    public static boolean saveUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        } else {
            SPUtils.getUserInstance().put(KEY_NICKNAME, userInfo.nick_name);
            SPUtils.getUserInstance().put(KEY_SEX, userInfo.sex);
            SPUtils.getUserInstance().put(KEY_USER_HEADIMG, userInfo.user_headimg);
            SPUtils.getUserInstance().put(KEY_MEMBER_LEVEL, userInfo.member_level);
            SPUtils.getUserInstance().put(KEY_LEVEL_NAME, userInfo.level_name);
            SPUtils.getUserInstance().put(KEY_POINT, userInfo.point);
            SPUtils.getUserInstance().put(KEY_LEVEL_ICON, userInfo.level_icon);
            SPUtils.getUserInstance().put(KEY_LEVEL_BACKGROUND, userInfo.level_background);
            return true;
        }
    }


    public static String getToken() {
        return (String) SPUtils.getUserInstance().get(KEY_TOKEN, "");
    }

    public static String getToken(Context context) {
        if (isLogin(context)) {
            return getToken();
        } else {
            return "";
        }

    }

    public static String getId() {
        return (int) SPUtils.getUserInstance().get(KEY_ID, 0) + "";
    }

    public static String getHeadImg() {
        return (String) SPUtils.getUserInstance().get(KEY_USER_HEADIMG, "");
    }

    public static int getPoint() {
        return (int) SPUtils.getUserInstance().get(KEY_POINT, 0);
    }

    public static String getNickname() {
        return (String) SPUtils.getUserInstance().get(KEY_NICKNAME, "");
    }

    public static String getLevelName() {
        return (String) SPUtils.getUserInstance().get(KEY_LEVEL_NAME, "");
    }

    public static String getLevelIcon() {
        return (String) SPUtils.getUserInstance().get(KEY_LEVEL_ICON, "");
    }

    public static String getKeyLevelBackground() {
        return (String) SPUtils.getUserInstance().get(KEY_LEVEL_BACKGROUND, "");
    }

    public static int getMemberLevel() {
        return (int) SPUtils.getUserInstance().get(KEY_MEMBER_LEVEL, 0);
    }

    public static void logout() {
        logoutYizhen();
        //EMClient.getInstance().logout(true);
    }

    public static boolean logoutYizhen() {
        if (isLogin()) {
            SPUtils.getUserInstance().clear();
            EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGIN));
            return true;
        } else {
            return false;
        }
    }


}
