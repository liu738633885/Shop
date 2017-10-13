package com.yizhen.shop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yizhen.shop.MainApplication;
import com.yizhen.shop.R;
import com.yizhen.shop.wxPay.Util;

import java.io.File;


/**
 * Created by AINANA-RD-X on 2016/8/11.
 */
public class ShareUtils {
    private static final int THUMB_SIZE = 150;
    //发送到聊天界面
    public static final int SHARE_TO_SESSION = SendMessageToWX.Req.WXSceneSession;
    //发送到朋友圈
    public static final int SHARE_TO_TIMELINE = SendMessageToWX.Req.WXSceneTimeline;
    //添加到微信收藏
    public static final int SHARE_TO_FAVORITE = SendMessageToWX.Req.WXSceneFavorite;

    public static void ShareWX(Context context, String shareUrl, String title, String description, Bitmap bitmap, int shareStyle) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, "wx366addddaff415be");
        //初始化一个WXWebpageObject对象，填写Url
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareUrl;
        //
        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = description;
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        msg.thumbData = Util.bmpToByteArrayMy(bitmap, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webPage" + System.currentTimeMillis();
        req.message = msg;
        req.scene = shareStyle;
        msgApi.sendReq(req);
    }

    public static void ShareWX(final Context context, final String shareUrl, final String title, final String description, String imgurl, final int shareStyle) {
        if (TextUtils.isEmpty(imgurl)) {
            ShareWX(context, shareUrl, title, description, BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon), shareStyle);
            return;
        }
        Glide.with(context).load(imgurl).asBitmap().into(new SimpleTarget<Bitmap>(200, 200) {
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                ShareWX(context, shareUrl, title, description, BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon), shareStyle);
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ShareWX(context, shareUrl, title, description, resource, shareStyle);
            }
        });
    }

    public static void ShareWXIMG(final Context context, final String imgpath) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, "wx2be5d3f05af61ca3");
        File file = new File(imgpath);
        Logger.e(imgpath);
        if (!file.exists()) {
            //String tip = "文件不存在";
            //Toast.makeText(MainApplication.getInstance(), tip + " path = " + imgpath, Toast.LENGTH_LONG).show();
            T.showShort(MainApplication.getInstance(), "请先浏览图片,才能分享");
            return;
        }

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(imgpath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bmp = BitmapFactory.decodeFile(imgpath);
        //Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        Bitmap thumbBmp = createBitmapThumbnail(bmp);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        int mTargetScene = SendMessageToWX.Req.WXSceneSession;
        req.scene = mTargetScene;
        msgApi.sendReq(req);
    }

    //private int mTargetScene = SendMessageToWX.Req.WXSceneSession;
    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 99;
        int newHeight = 99;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
                matrix, true);
        return newBitMap;
    }
}
