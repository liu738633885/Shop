package com.yizhen.shop.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lewis on 2017/9/30.
 */

public class TimeTextView extends TextView {
    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 在控件被销毁时移除消息
        handler.removeMessages(0);
        handler.removeMessages(1);
    }

    protected void onAttachedToWindow() {
        super.onDetachedFromWindow();
        // 在控件被销毁时移除消息
        handler.removeMessages(1);
        handler.sendEmptyMessage(1);
    }

    long st;
    long et;
    long Time;
    private boolean run = true; // 是否启动了
    @SuppressLint("NewApi")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (run) {
                        long mTime = Time;
                        if (mTime > 0) {
                            String day = "";
                            TimeTextView.this.setText("倒计时    还有" + Time + "s");
                            Time = Time - 1000;
                            handler.sendEmptyMessageDelayed(0, 1000);
                        }
                    } else {
                        TimeTextView.this.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (run) {
                        String str1 = "";
                        String str2 = "";
                        long nowTime = new Date().getTime() / 1000;
                        long time = 0;
                        if (nowTime < st) {
                            str1 = "距开始: ";
                            time = st - nowTime;
                        } else if (nowTime >= st && nowTime <= et) {
                            str1 = "距结束: ";
                            time = et - nowTime;
                        } else {
                            str1 = "已结束";
                        }

                        long t = time;
                        long h = t / (60 * 60);
                        long m = (t - 60 * 60 * h) / 60;
                        long s = t - 60 * 60 * h - 60 * m;
                        String hs = h == 0 ? "" : h + "小时 ";
                        String ms = m == 0 ? "" : m + "分钟 ";
                        String ss = (s > 9 ? s + "" : "0" + s) + "秒";
                        str2 = hs + ms + ss;
                        if (str1.equals("已结束")) {
                            TimeTextView.this.setText(str1);
                        } else {
                            TimeTextView.this.setText(str1 + str2);
                        }
                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        TimeTextView.this.setVisibility(View.GONE);
                    }
                    break;
            }

        }
    };


    public TimeTextView(Context context) {
        super(context);
    }


    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    public void setTimes(long mT) {
        // 标示已经启动
        Date date = new Date();
        long t2 = date.getTime();
        Time = mT - t2;
        date = null;

        if (Time > 0) {
            handler.removeMessages(0);
            handler.sendEmptyMessage(0);
        } else {
            TimeTextView.this.setVisibility(View.GONE);
        }
    }

    public void setTimes(long sT, long eT) {
        this.st = sT;
        this.et = eT;
        handler.removeMessages(1);
        handler.sendEmptyMessage(1);

    }


    public void stop() {
        run = false;
    }
}
