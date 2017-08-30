package com.yizhen.shop.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
* @ClassName: DensityUtil 
* @Description: TODO(屏幕分辨率处理工具)
* @author yiw
* @date 2015-12-28 下午4:17:01 
 */
public class DensityUtil {
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/** 获取手机的密度*/
	public static float getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}
	/** 获取手机的Dpi*/
	public static float getDensityDpi(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.densityDpi;
	}

	/** 获取手机的宽度*/
	public static int getWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/** 获取手机的高度*/
	public static int getHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/** 获取手机的高度*/
	public static int getStatusBarHeight(Context context) {
		Rect outRect = new Rect();
		((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		return outRect.top;
	}
	/*----------------*/
	/**
	 * 将dp转换成对应的像素值
	 *
	 * @param context
	 * @param dp
	 * @return
	 */
	public static float convertDp2Px(Context context, int dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
	}

	/**
	 * 将sp转换成对应的像素值
	 *
	 * @param context
	 * @param sp
	 * @return
	 */
	public static float convertSp2Px(Context context, int sp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
	}
}
