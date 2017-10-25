package com.yizhen.shop.util.imageloader;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.yizhen.shop.BuildConfig;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.util.DensityUtil;


/**
 * Created by lewis on 16/7/6.
 */
public class ImageLoader {
    private static DrawableRequestBuilder getBuilder(Context context, String url, boolean isHeadIcon, int type, int denWith, int denHeight, int round, int placeHoder) {

        if (isHeadIcon) {
            placeHoder = R.mipmap.default_user_icon;
        } else {
            if (placeHoder == 0) {
                placeHoder = R.color.loading;
            }
        }
        DrawableRequestBuilder builder = Glide.with(context).load(getUrl(url)).placeholder(placeHoder);
        if (denWith > 0 && denHeight > 0) {
            builder.override(DensityUtil.getWidth(context) / denWith, DensityUtil.getWidth(context) / denHeight);
        }
        if (type == 1) {
            builder.centerCrop();
        } else if (type == 2) {
            builder.fitCenter();
        }
        if (round == 0) {
            builder.transform(new GlideCircleTransform(context));
        } else if (round != -1) {
            builder.transform(new GlideRoundTransform(context, round));
        }
        return builder;
    }

    public static String getUrl(String url) {
        if (url.startsWith("http")) {
            return url;

        } else if (url.startsWith("/storage")) {
            return url;

        } else if (url.startsWith("android.resource:")) {
            return url;
        } else {
            return Constants.IMG_HEAD + url;
           /* if (TextUtils.isEmpty(url) && isHeadIcon) {
                url = "android.resource://" + BuildConfig.APPLICATION_ID + "/mipmap/" + R.mipmap.default_user_icon;
            } else {
                url = Constants.IMG_HEAD + url;
            }*/
        }

    }

    public static void load(Context context, String url, ImageView imv) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            getBuilder(context, url, false, 1, 0, 0, -1, 0).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void load(Context context, String url, ImageView imv, int round) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            getBuilder(context, url, false, 1, 0, 0, round, 0).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadAutoHeight(Context context, String url, ImageView imv, int round) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            //Glide.with(context).load(url).bitmapTransform(new RoundedCornersTransformation(context, round, 0)).into(imv);
            //Glide.with(context).load(url).bitmapTransform(new RoundedCornersTransformation(context, DensityUtil.dip2px(context,round), 0)).into(imv);
            Glide.with(context).load(getUrl(url)).transform(new FitCenter(context), new GlideRoundTransform(context, round)).into(imv);
            //Glide.with(context).load(url).transform(new GlideRoundTransform(context, DensityUtil.dip2px(context,round))).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            getBuilder(context, url, false, 2, 0, 0, round, -1).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public static void loadHome(Context context, String url, ImageView imv) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            //getBuilder(context, url, false, 1, 0, 0, -1, 0).transform(new GlideRoundTransform2(context, 30, 30, 0, 0)).into(imv);
            RoundCornersTransformation2 transformation=new RoundCornersTransformation2(context,DensityUtil.dip2px(context,2),RoundCornersTransformation2.CornerType.TOP);
            Glide.with(context).load(getUrl(url)).bitmapTransform(transformation).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadFromMipmap(Context context, int urlId, ImageView imv) {
        try {
            String url = "android.resource://" + BuildConfig.APPLICATION_ID + "/mipmap/" + urlId;
            getBuilder(context, url, false, 1, 0, 0, -1, 0).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadHeadImage(Context context, String url, ImageView imv, int round) {
        try {
            if (TextUtils.isEmpty(url)) {
                url = "android.resource://" + BuildConfig.APPLICATION_ID + "/mipmap/" + R.mipmap.default_user_icon;
            }
            getBuilder(context, url, true, 1, 0, 0, round, 0).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
