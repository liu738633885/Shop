package com.yizhen.shop.util.imageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import static android.R.attr.radius;

/**
 * Created by lewis on 16/7/11.
 */
public class GlideRoundTransform2 extends BitmapTransformation {
    private static float topLeftDp = 0f;
    private static float topRightDp = 0f;
    private static float bottomRightDp = 0f;
    private static float bottomLeftDp = 0f;

    public GlideRoundTransform2(Context context, int topLeftDp, int topRightDp, int bottomRightDp, int bottomLeftDp) {
        super(context);
        this.topLeftDp = Resources.getSystem().getDisplayMetrics().density * topLeftDp;
        this.topRightDp = Resources.getSystem().getDisplayMetrics().density * topRightDp;
        this.bottomRightDp = Resources.getSystem().getDisplayMetrics().density * bottomRightDp;
        this.bottomLeftDp = Resources.getSystem().getDisplayMetrics().density * bottomLeftDp;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawPath(composeRoundedRectPath(rectF, topLeftDp, topRightDp, bottomRightDp, bottomLeftDp), paint);
        return result;
    }

    public static Path composeRoundedRectPath(RectF rect, float topLeftDiameter, float topRightDiameter, float bottomRightDiameter, float bottomLeftDiameter) {
        Path path = new Path();
        topLeftDiameter = topLeftDiameter < 0 ? 0 : topLeftDiameter;
        topRightDiameter = topRightDiameter < 0 ? 0 : topRightDiameter;
        bottomLeftDiameter = bottomLeftDiameter < 0 ? 0 : bottomLeftDiameter;
        bottomRightDiameter = bottomRightDiameter < 0 ? 0 : bottomRightDiameter;

        path.moveTo(rect.left + topLeftDiameter / 2, rect.top);
        path.lineTo(rect.right - topRightDiameter / 2, rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightDiameter / 2);
        path.lineTo(rect.right, rect.bottom - bottomRightDiameter / 2);
        path.quadTo(rect.right, rect.bottom, rect.right - bottomRightDiameter / 2, rect.bottom);
        path.lineTo(rect.left + bottomLeftDiameter / 2, rect.bottom);
        path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bottomLeftDiameter / 2);
        path.lineTo(rect.left, rect.top + topLeftDiameter / 2);
        path.quadTo(rect.left, rect.top, rect.left + topLeftDiameter / 2, rect.top);
        path.close();

        return path;
    }

    @Override
    public String getId() {
        return getClass().getName() + Math.round(radius);
    }
}