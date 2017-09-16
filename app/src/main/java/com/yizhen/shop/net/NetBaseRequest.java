/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yizhen.shop.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.RestRequest;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.util.List;


public class NetBaseRequest extends RestRequest<NetBaseBean> {

    public NetBaseRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    public NetBaseRequest(String url) {
        super(url, RequestMethod.POST);
    }

  /*  @Override
    public NetBaseBean parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        String result = StringRequest.parseResponseString(url, responseHeaders, responseBody);
        Logger.d("后台返回的数据:"+result);
        NetBaseBean baseBean;
        try {
            baseBean = JSON.parseObject(result, NetBaseBean.class);
            Logger.d(baseBean.toString());
            return baseBean;
        } catch (Exception e) {
            return new NetBaseBean();
        }
    }
*/
   /* @Override
    public String getAccept() {
        return JsonObjectRequest.ACCEPT;
    }*/

    public void addJsonArray(String key, List list) {
        if(list==null){
            return;
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(list);
        add(key, jsonArray.toJSONString());
    }

    @Override
    public NetBaseBean parseResponse(Headers responseHeaders, byte[] responseBody) {
        String result = StringRequest.parseResponseString(responseHeaders, responseBody);
        //Logger.d("后台返回的数据:" + result);
        // 这里如果数据格式错误，或者解析失败，会在失败的回调方法中返回 ParseError 异常。
        NetBaseBean baseBean = JSON.parseObject(result, NetBaseBean.class);
        Logger.d("json解析的数据" + baseBean.toString());
        return baseBean;
    }
}
