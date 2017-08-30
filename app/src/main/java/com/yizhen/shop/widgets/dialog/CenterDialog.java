package com.yizhen.shop.widgets.dialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.yizhen.shop.R;


/**
 * Created by lewis on 16/6/29.
 */
public class CenterDialog extends Dialog {
    private Context context;
    private boolean isAnimator;
    private ValueAnimator animator;
    private boolean restart;

    public CenterDialog(Context context) {
        this(context, R.style.MyDialogStyleBottom);
    }

    public CenterDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public void setContentView(int layoutResID) {
        setContentView(layoutResID, false, false);
    }

    public void setContentView(int layoutResID, boolean isfull) {
        setContentView(layoutResID, isfull, false);
    }

    public void setContentView(int layoutResID, boolean isfull, final boolean isAnimator) {
        super.setContentView(layoutResID);
        initView(isfull);
        if (findViewById(R.id.cancel) != null) {
            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        try {
            Activity activity = (Activity) context;
            if (activity == null) {
                return;
            }
            final View viewContent = activity.findViewById(android.R.id.content);
            this.isAnimator = isAnimator;
            if (isAnimator) {
                ((View) viewContent.getParent()).setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(300);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float t = (float) valueAnimator.getAnimatedValue();
                        Log.e("哈哈哈", t + "");
                        if (restart) {
                            viewContent.setScaleX(0.85f + t * 0.15f);
                            viewContent.setScaleY(0.85f + t * 0.15f);
                        } else {
                            if (t < 0.5) {
                                viewContent.setRotationX(t * 10);
                            } else {
                                viewContent.setRotationX(-(t - 1) * 10);
                            }
                            viewContent.setScaleX(1.0f - t * 0.15f);
                            viewContent.setScaleY(1.0f - t * 0.15f);
                        }
                    }
                });
            }
            setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (isAnimator && animator != null) {
                        if (animator.isRunning()) {
                            animator.end();
                        }
                        restart = true;
                        animator.start();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView(boolean isfull) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        if (isfull) {
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        // lp.width = DensityUtil.getWidth(context);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void show() {
        if (this == null || isShowing()) {
            return;
        }
        if (isAnimator && animator != null && !animator.isRunning()) {
            restart = false;
            animator.start();
        }
        super.show();

    }

    @Override
    public void dismiss() {
        if (this == null || !isShowing()) {
            return;
        }
        if (isAnimator && animator != null && !animator.isRunning()) {
            restart = true;
            animator.start();
        }
        super.dismiss();
    }
}
