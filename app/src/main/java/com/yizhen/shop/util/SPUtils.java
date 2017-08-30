package com.yizhen.shop.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.MainApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by lewis on 16/5/23.
 */

public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME_DEFAULT = "superactivities";
    public static final String FILE_NAME_USER = "user";
    public static final String FILE_NAME_SETTING = "setting";
    public static final String FILE_NAME_HUODONG = "huodong";
    public static final String FILE_NAME_HUANXINUSER = "huanxin_user";
    static SPUtils defaultInstance;
    static SPUtils userInstance;
    static SPUtils settingInstance;
    static SPUtils HuodongInstance;
    static SPUtils HuanxinUserInstance;
    static SharedPreferences mPreferences;
    static SharedPreferences.Editor mEditor;

    public static SPUtils getDefaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new SPUtils();
        }
        mPreferences = MainApplication.getInstance().getSharedPreferences(FILE_NAME_DEFAULT, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        return defaultInstance;
    }

    public static SPUtils getUserInstance() {
        if (userInstance == null) {
            userInstance = new SPUtils();
        }
        mPreferences = MainApplication.getInstance().getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        return userInstance;
    }

    public static SPUtils getSettingInstance() {
        if (settingInstance == null) {
            settingInstance = new SPUtils();
        }
        mPreferences = MainApplication.getInstance().getSharedPreferences(FILE_NAME_SETTING, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        return settingInstance;
    }
    public static SPUtils getHuodongInstance() {
        if (HuodongInstance == null) {
            HuodongInstance = new SPUtils();
        }
        mPreferences = MainApplication.getInstance().getSharedPreferences(FILE_NAME_HUODONG, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        return HuodongInstance;
    }
    public static SPUtils getHuanxinUserInstance() {
        if (HuanxinUserInstance == null) {
            HuanxinUserInstance = new SPUtils();
        }
        mPreferences = MainApplication.getInstance().getSharedPreferences(FILE_NAME_HUANXINUSER, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        return HuanxinUserInstance;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof String) {
            mEditor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            mEditor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            mEditor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            mEditor.putLong(key, (Long) object);
        } else {
            mEditor.putString(key, JSON.toJSON(object).toString());
        }
        Logger.d("存储的 key:"+key+"---"+object.toString());
        SharedPreferencesCompat.apply(mEditor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {

        if (defaultObject instanceof String) {
            return mPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return mPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return mPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return mPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return mPreferences.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public <T> T getObject(String key, Class<T> clazz) {
        if (TextUtils.isEmpty((String) get(key, ""))) {
            return null;
        }
        return JSON.parseObject((String) get(key, ""), clazz);

    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void remove(String key) {

        mEditor.remove(key);
        SharedPreferencesCompat.apply(mEditor);
    }

    /**
     * 清除所有数据
     */
    public void clear() {

        mEditor.clear();
        SharedPreferencesCompat.apply(mEditor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}
