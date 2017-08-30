package com.yizhen.shop.util;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yizhen.shop.Constants;
import com.yizhen.shop.model.ImageBean;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.manager.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 16/6/24.
 */
public class UpLoadManager {
    final static int compressWidth = 1000;
    final static int compressHeight = 1000;

    public static void uploadImgs(Context context, List<String> paths, UpLoadListener upLoadListener, final String url) {
        uploadImgs(context, paths, upLoadListener, 1000, 1000, url);
    }

    public static void uploadImgs(final Context context, final List<String> paths, final UpLoadListener upLoadListener, final int w, final int h, final String url) {
        if (paths == null || paths.size() < 1) {
            return;
        }
        if (upLoadListener == null) {
            return;
        }
        final List<String> urls = new ArrayList<>();
        final int[] i = {0};
        SingleUpLoadListener singleUpLoadListener = new SingleUpLoadListener() {
            @Override
            public void onComplete(boolean isSuccess, String path_message) {
                if (isSuccess) {
                    urls.add(path_message);
                } else {
                    upLoadListener.Failed(i[0]);
                    Logger.d("失败信息" + path_message);
                }
                if (i[0] >= paths.size() - 1) {
                    upLoadListener.Success(urls);
                } else {
                    i[0]++;
                    uploadImg(context, paths.get(i[0]), this, w, h, url);
                    Logger.d("开始上传第" + (i[0] + 1) + "张");
                }
            }
        };
        uploadImg(context, paths.get(i[0]), singleUpLoadListener, w, h, url);
    }

    public static void uploadImg(Context context, String filePath, SingleUpLoadListener listener, String url) {
        uploadImg(context, filePath, listener, compressWidth, compressHeight, url);
    }

    public static void uploadImg(Context context, String filePath, final SingleUpLoadListener listener, int w, int h, String url) {
        if (listener == null) {
            return;
        }
        String newFilePath = BitmapUtils.compressImg(filePath, w, h);
        String string;
        if (TextUtils.isEmpty(url)) {
            string = Constants.UPLOAD_IMGES;
        } else {
            string = url;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(string);
        request.add("user_id", UserManager.getId());
        request.add("token", UserManager.getToken());
        BasicBinary binary = new FileBinary(new File(newFilePath));
        request.add("file", binary);// 添加1个文件
        CallServer.getRequestInstance().add(context, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Logger.e("请求成功" + netBaseBean.getBody().toString());
                if (netBaseBean.isSuccess()) {
                    ImageBean bean = netBaseBean.parseObject(ImageBean.class);
                    if (bean != null && !TextUtils.isEmpty(bean.getUrl())) {
                        listener.onComplete(true, bean.getUrl());
                    } else {
                        listener.onComplete(false, netBaseBean.getMessage());
                    }
                } else {
                    listener.onComplete(false, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                listener.onComplete(false, "");
            }
        }, true, true);
    }

    public interface SingleUpLoadListener {
        void onComplete(boolean isSuccess, String path_message);
    }

    public interface UpLoadListener {
        void Success(List<String> urls);

        void Failed(int error);
    }
}
