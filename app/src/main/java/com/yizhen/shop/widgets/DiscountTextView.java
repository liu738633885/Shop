package com.yizhen.shop.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yizhen.shop.R;

import java.util.Date;

/**
 * Created by lewis on 2017/10/17.
 */

public class DiscountTextView extends FrameLayout {
    private TextView tv1, tv2, tv3, tv_over;
    private long et;
    private LinearLayout ll_discount;

    public DiscountTextView(Context context) {
        //super(context);
        this(context, null, 0);
    }

    public DiscountTextView(Context context, AttributeSet attrs) {

        //super(context, attrs);
        this(context, attrs, 0);
    }

    public DiscountTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.textview_discount, this, true);
        initView();
    }

    private void initView() {
        tv1 = this.findViewById(R.id.discount_tv1);
        tv2 = this.findViewById(R.id.discount_tv2);
        tv3 = this.findViewById(R.id.discount_tv3);
        tv_over = this.findViewById(R.id.tv_over);
        ll_discount =this. findViewById(R.id.ll_discount);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 在控件被销毁时移除消息
        handler.removeMessages(0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onDetachedFromWindow();
        // 在控件被销毁时移除消息
        handler.removeMessages(0);
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    long nowTime = new Date().getTime() / 1000;
                    if (et > nowTime) {
                        long time = et - nowTime;
                        long t = time;
                        long h = t / (60 * 60);
                        long m = (t - 60 * 60 * h) / 60;
                        long s = t - 60 * 60 * h - 60 * m;
                        String hs = (h > 9 ? "" : "0") + h;
                        String ms = (m > 9 ? "" : "0") + m;
                        String ss = (s > 9 ? "" : "0") + s;
                        tv1.setText(hs);
                        tv2.setText(ms);
                        tv3.setText(ss);
                        tv_over.setVisibility(View.INVISIBLE);
                        ll_discount.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessageDelayed(0, 1000);
                    } else {
                        tv_over.setVisibility(View.VISIBLE);
                        ll_discount.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    };

    public void setTimes(long eT) {
        this.et = eT;
        handler.removeMessages(0);
        handler.sendEmptyMessage(0);
    }
}
