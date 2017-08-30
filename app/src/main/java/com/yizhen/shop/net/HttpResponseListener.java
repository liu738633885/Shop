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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.user.LoginActivity;
import com.yizhen.shop.user.SplashActivity;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.widgets.dialog.LoadingDialog;
import com.yizhen.shop.widgets.swl.SwipeRefreshLayout;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.net.ProtocolException;

/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author Yan Zhenjie.
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private Context context;
    /**
     * Dialog.
     */
    private LoadingDialog mWaitDialog;
    private SwipeRefreshLayout swl;

    private Request<?> mRequest;
    /**
     * 结果回调.
     */
    private HttpListener<T> callback;
    private HttpListenerCallback callback2;

    /**
     * 是否显示dialog.
     */
    private boolean isLoading;
    /**
     * 弹出错误信息.
     */
    private String errorText;
    private boolean showError;

    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        this.context = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new LoadingDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback = httpCallback;
        this.isLoading = isLoading;
    }

    public HttpResponseListener(Context context, Request<?> request, HttpListenerCallback httpCallback, boolean canCancel, boolean isLoading) {
        this.context = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new LoadingDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback2 = httpCallback;
        this.isLoading = isLoading;
    }

    public HttpResponseListener(Context context, Request<?> request, HttpListenerCallback httpCallback, boolean canCancel, boolean isLoading, String errorText) {
        this.context = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new LoadingDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback2 = httpCallback;
        this.isLoading = isLoading;
        this.errorText = errorText;
        showError = true;
    }

    public HttpResponseListener(Context context, Request<?> request, HttpListenerCallback httpCallback, SwipeRefreshLayout swl) {
        this.context = context;
        this.mRequest = request;
        this.swl = swl;
        this.callback2 = httpCallback;
        showError = true;
    }

    public HttpResponseListener(Context context, Request<?> request, HttpListenerCallback httpCallback, SwipeRefreshLayout swl, String errorText) {
        this.context = context;
        this.mRequest = request;
        this.swl = swl;
        this.callback2 = httpCallback;
        this.errorText = errorText;
        showError = true;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (isLoading && mWaitDialog != null && !mWaitDialog.isShowing()) {
            mWaitDialog.show();
        }
        if (swl != null && !swl.isRefreshing()) {
            swl.setRefreshing(true);
        }
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        try {
            if (isLoading && mWaitDialog != null && mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
            if (swl != null && swl.isRefreshing()) {
                swl.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (response.get() instanceof NetBaseBean) {
            NetBaseBean request = (NetBaseBean) response.get();
            try {
                switch (request.getStatus()) {
                    case 502:
                        if (UserManager.isLogin()) {
                            Toast.makeText(context, "登录信息过期", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
                        }
                        if (context instanceof SplashActivity) {
                            Logger.e("首页判断");
                        } else {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        break;
                    case 200:
                        //成功不弹出信息
                        break;
                    default:
                        if (showError) {
                            Toast.makeText(context, TextUtils.isEmpty(errorText) ? request.getMessage() : errorText, Toast.LENGTH_SHORT).show();
                        }
                }
                if (callback2 != null) {
                    callback2.onSucceed(what, request);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "服务器数据类型错误", Toast.LENGTH_SHORT).show();
            }
        }

        if (callback != null) {
            callback.onSucceed(what, response);
        }
    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好
            HttpExceptionToast.show(R.string.error_please_check_network);
        } else if (exception instanceof TimeoutError) {// 请求超时
            HttpExceptionToast.show(R.string.error_timeout);
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            HttpExceptionToast.show(R.string.error_not_found_server);
        } else if (exception instanceof URLError) {// URL是错的
            HttpExceptionToast.show(R.string.error_url_error);
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            HttpExceptionToast.show(R.string.error_not_found_cache);
        } else if (exception instanceof ProtocolException) {
            HttpExceptionToast.show(R.string.error_system_unsupport_method);
        } else {
            HttpExceptionToast.show(R.string.error_unknow);
        }
        Logger.e("错误：" + exception.getMessage());
        //HttpExceptionToast.showException(response.getException(), response.responseCode());
        if (callback != null) {
            callback.onFailed(what, mRequest.url(), response.getTag(), exception, response.responseCode(), response.getNetworkMillis());
        }
        if (callback2 != null) {
            try {
                callback2.onFailed(what, mRequest.url(), response.getTag(), exception, response.responseCode(), response.getNetworkMillis());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "服务器数据类型错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
