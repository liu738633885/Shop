package com.yizhen.shop.model.netmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 16/6/22.
 */
public class NetBaseBean {
    public NetBaseBean() {
        super();
    }


    @JSONField(name = "body")
    private String body;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "status")
    private int status;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 业务是否成功。
     *
     * @return 成功返回true，失败返回false。
     */
    public boolean isSuccess() {
        return getStatus() == 200;
    }

    /**
     * 用一个JavaBean解析带有复杂的data Entity的请求
     *
     * @param clazz
     * @return
     */
    public <E> E parseObject(Class<E> clazz) {
        try {
            return JSON.parseObject(getBody(), clazz);
        } catch (Exception e) {
            try {
                return clazz.newInstance();
            } catch (Exception e1) {
            }
        }
        return null;
    }

    /**
     * 用一个JavaBean解析带有复杂的data List的请求
     *
     * @param clazz
     * @return
     */
    public <E> List<E> parseList(Class<E> clazz) {
        List<E> es = new ArrayList<E>();
        try {
            es = JSON.parseArray(getBody(), clazz);
        } catch (Exception e) {
        }
        return es;
    }

    @Override
    public String toString() {
        return "NetBaseBean{" +
                "body='" + body + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
