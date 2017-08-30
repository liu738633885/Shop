/**
 * Copyright © YOLANDA. All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yizhen.shop.net;


import android.widget.Toast;

import com.yizhen.shop.MainApplication;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;

import java.net.ProtocolException;

/**
 * Created in Jan 31, 2016 4:15:36 PM.
 *
 * @author YOLANDA;
 */
public class HttpExceptionToast {

    public static void show(CharSequence msg) {
        Toast.makeText(MainApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
    }

    public static void show(int stringId) {
        Toast.makeText(MainApplication.getInstance(), stringId, Toast.LENGTH_LONG).show();
    }

    public static void showException(Exception exception, int responseCode) {
        if (exception instanceof ClientError) {// 客户端错误
            if (responseCode == 400) {//服务器未能理解请求。
                show("错误的请求，服务器表示不能理解。");
            } else if (responseCode == 403) {// 请求的页面被禁止
                show("错误的请求，服务器表示不愿意。");
            } else if (responseCode == 404) {// 服务器无法找到请求的页面
                show("错误的请求，服务器表示找不到。");
            } else {// 400-417都是客户端错误，开发者可以自己去查询噢
                show("错误的请求，服务器表示伤不起。");
            }
        } else if (exception instanceof ServerError) {// 服务器错误
            if (500 == responseCode) {
                show("服务器遇到不可预知的情况。");
            } else if (501 == responseCode) {
                show("服务器不支持的请求。");
            } else if (502 == responseCode) {
                show("服务器从上游服务器收到一个无效的响应。");
            } else if (503 == responseCode) {
                show("服务器临时过载或当机。");
            } else if (504 == responseCode) {
                show("网关超时。");
            } else if (505 == responseCode) {
                show("服务器不支持请求中指明的HTTP协议版本。");
            } else {
                show("服务器脑子有问题。");
            }
        } else if (exception instanceof NetworkError) {// 网络不好
            show("请检查网络。");
        } else if (exception instanceof TimeoutError) {// 请求超时
            show("请求超时，网络不好或者服务器不稳定。");
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            show("未发现指定服务器。");
        } else if (exception instanceof URLError) {// URL是错的
            show("URL错误。");
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            show("没有发现缓存。");
        } else if (exception instanceof ProtocolException) {
            show("系统不支持的请求方式。");
        } else {
            show("未知错误。");
        }
    }
    public class ClientError extends Exception {

        private static final long serialVersionUID = 11561L;

        public ClientError() {
        }

        public ClientError(String detailMessage) {
            super(detailMessage);
        }

        public ClientError(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ClientError(Throwable throwable) {
            super(throwable);
        }
    }


}
