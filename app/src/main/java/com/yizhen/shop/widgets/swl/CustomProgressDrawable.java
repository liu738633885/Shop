package com.yizhen.shop.widgets.swl;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by lbf on 2016/7/24.
 */
public class CustomProgressDrawable extends MaterialProgressDrawable {

    //    旋转因子，调整旋转速度
    private static final int ROTATION_FACTOR = 5 * 360;
    private ValueAnimator animator;
    private Bitmap mBitmap;
    //    旋转角度
    private float rotation;
    private Paint paint;

    public CustomProgressDrawable(Context context, View parent) {
        super(context, parent);
        paint = new Paint();
        setupAnimation();
    }

    private void setupAnimation() {
        animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                //Log.e("mAnimation", t + "");
                setProgressRotation(t + nowRotation);
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(3000);
        animator.setInterpolator(new LinearInterpolator());
    }

    /*获得当前的旋转度数*/
    float nowRotation;

    @Override
    public void start() {
        nowRotation = rotation;
        animator.start();
    }

    @Override
    public void stop() {
        super.stop();
        animator.end();
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }


    @Override
    public void setProgressRotation(float rotation) {
        this.rotation = rotation;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas c) {
        //super.draw(c);
        Rect bound = getBounds();
        c.rotate(rotation * ROTATION_FACTOR, bound.exactCenterX(), bound.exactCenterY());
        Rect src = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        c.drawBitmap(mBitmap, src, bound, paint);
    }

}
