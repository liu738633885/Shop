/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yizhen.shop.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.widgets.swl.SwipeRefreshLayout;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * Created in Oct 23, 2015 1:00:56 PM.
 *
 * @author Yan Zhenjie.
 */
public class CallServer {

    private static CallServer callServer;

    /**
     * 请求队列.
     */
    private RequestQueue requestQueue;

    /**
     * 下载队列.
     */
    private static DownloadQueue downloadQueue;

    private CallServer() {
        requestQueue = NoHttp.newRequestQueue();
    }

    /**
     * 请求队列.
     */
    public synchronized static CallServer getRequestInstance() {
        if (callServer == null)
            callServer = new CallServer();
        return callServer;
    }

    /**
     * 下载队列.
     */
    public static DownloadQueue getDownloadInstance() {
        if (downloadQueue == null)
            downloadQueue = NoHttp.newDownloadQueue();
        return downloadQueue;
    }

    /**
     * 添加一个请求到请求队列.
     *
     * @param context   context用来实例化dialog.
     * @param what      用来标志请求, 当多个请求使用同一个{@link HttpListener}时, 在回调方法中会返回这个what.
     * @param request   请求对象.
     * @param callback  结果回调对象.
     * @param canCancel 是否允许用户取消请求.
     * @param isLoading 是否显示dialog.
     */
    public <T> void add(Context context, int what, Request<T> request, HttpListener<T> callback, boolean canCancel, boolean isLoading) {
        request.setCancelSign(context);
        requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, canCancel, isLoading));
    }


    public <T> void add(Context context, int what, Request<T> request, HttpListenerCallback callback, boolean canCancel, boolean isLoading) {
        request.setCancelSign(context);
        requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, canCancel, isLoading));
    }

    public <T> void add(Context context, int what, Request<T> request, HttpListenerCallback callback, boolean canCancel, boolean isLoading, String errorText) {
        request.setCancelSign(context);
        requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, canCancel, isLoading, errorText));
    }

    public <T> void add(Context context, int what, Request<T> request, HttpListenerCallback callback, SwipeRefreshLayout swl) {
        request.setCancelSign(context);
        requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, swl));
    }

    public <T> void add(Context context, int what, Request<T> request, HttpListenerCallback callback, SwipeRefreshLayout swl, String errorText) {
        request.setCancelSign(context);
        requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, swl, errorText));
    }

    public <T> void addWithLogin(Context context, int what, Request<T> request, HttpListenerCallback callback, boolean canCancel, boolean isLoading) {
        try {
            final Activity activity = (Activity) context;
            if (UserManager.isLogin(context)) {
                //request.add("uid", UserManager.getUid());
                requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, canCancel, isLoading));
            } else {
                //activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //说明是ApplicationContext
        }

    }

    public <T> void addWithLoginToLogin(Context context, int what, Request<T> request, HttpListenerCallback callback, boolean canCancel, boolean isLoading) {
        try {
            final Activity activity = (Activity) context;
            if (UserManager.isLogin(context)) {
                requestQueue.add(what, request, new HttpResponseListener<T>(context, request, callback, canCancel, isLoading));
            } else {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //说明是ApplicationContext
        }

    }

    /**
     * 取消这个sign标记的所有请求.
     */
    public void cancelBySign(Object sign) {
        requestQueue.cancelBySign(sign);
    }

    /**
     * 取消队列中所有请求.
     */
    public void cancelAll() {
        requestQueue.cancelAll();
    }

    /**
     * 退出app时停止所有请求.
     */
    public void stopAll() {
        requestQueue.stop();
    }

}
