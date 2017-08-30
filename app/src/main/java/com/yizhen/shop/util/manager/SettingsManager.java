package com.yizhen.shop.util.manager;


import com.yizhen.shop.MainApplication;
import com.yizhen.shop.util.SPUtils;
import com.yizhen.shop.util.SystemUtils;

/**
 * Created by lewis on 16/6/22.
 */
public class SettingsManager {
    public static final String KEY_CITY_ID = "member_city_id";
    public static final String KEY_CITY_NAME = "member_city_name";
    public static final String KEY_APP_CODE = "now_app_code";
    public static final String KEY_NEW_APP_CODE = "new_app_code";
    public static final String KEY_SPLASH_URL = "splash_url";
    public static final String KEY_SPLASH_ID = "splash_id";

    public static String getKeyCityId() {
        return (String) SPUtils.getSettingInstance().get(KEY_CITY_ID, "313");
    }

    public static void setKEY_CITY_ID(String cityId) {
        SPUtils.getSettingInstance().put(KEY_CITY_ID, cityId);
    }


    public static String getKeyCityName() {
        return (String) SPUtils.getSettingInstance().get(KEY_CITY_NAME, "海口");
    }

    public static void setKEY_CITY_NAME(String cityName) {
        SPUtils.getSettingInstance().put(KEY_CITY_NAME, cityName);
    }

    public static void saveAppCode() {
        SPUtils.getSettingInstance().put(KEY_APP_CODE, SystemUtils.getAppVersionCode(MainApplication.getInstance()));
    }

    public static int getAppCode() {
        return (int) SPUtils.getSettingInstance().get(KEY_APP_CODE, 0);
    }

    public static void saveSplashId(int id) {
        SPUtils.getSettingInstance().put(KEY_SPLASH_ID, id);
    }

    public static boolean haveNewSpalsh(int id) {
        return (int)SPUtils.getSettingInstance().get(KEY_SPLASH_ID, 0) != id;
    }

    public static void saveSplashUrl(String url) {
        SPUtils.getSettingInstance().put(KEY_SPLASH_URL, url);
    }

    public static String getSplashUrl() {
        return (String) SPUtils.getSettingInstance().get(KEY_SPLASH_URL, "");
    }

    public static void saveNewAppCode(int code) {
        SPUtils.getSettingInstance().put(KEY_NEW_APP_CODE, code);
    }

    public static int getNewAppCode() {
        return (int) SPUtils.getSettingInstance().get(KEY_NEW_APP_CODE, 0);
    }

    /**
     * 是否为新版本第一次启动
     *
     * @return
     */
    public static boolean isNewCodeFirst() {
        if (getAppCode() == SystemUtils.getAppVersionCode(MainApplication.getInstance())) {
            return false;
        }
        saveAppCode();
        return true;
    }

    public static boolean isHaveNewCode() {
        return SystemUtils.getAppVersionCode(MainApplication.getInstance()) < getNewAppCode();
    }

    public static void clean() {
        SPUtils.getSettingInstance().clear();
    }
}
