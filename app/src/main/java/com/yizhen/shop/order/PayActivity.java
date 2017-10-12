package com.yizhen.shop.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.aliPay.PayResult;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.order.PayInfo;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PayActivity extends BaseActivity {
    private LinearLayout ll_zhifubao, ll_weixin;
    private String no;
    private int backmode;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pay;
    }

    public static void goTo(Context context, String no) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("no", no);
        context.startActivity(intent);
    }

    public static void goTo(Context context, String no, int backmode) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("no", no);
        intent.putExtra("backmode", backmode);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        no = intent.getStringExtra("no");
        backmode = intent.getIntExtra("backmode", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ll_zhifubao = (LinearLayout) findViewById(R.id.ll_zhifubao);
        ll_weixin = (LinearLayout) findViewById(R.id.ll_weixin);
        ll_zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPay(2);
            }
        });
        ll_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast("还没开启微信支付");
                callPay(1);
            }
        });
    }

    private void callPay(final int pay_type) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.PAY_ORDER);
        request.add("pay_type", pay_type);
        request.add("no", no);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    PayInfo payInfo = netBaseBean.parseObject(PayInfo.class);
                    if (pay_type == 2) {
                        payZhiFuBao(payInfo.return_str);
                    } else {
                        weChatPay(payInfo);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private static final int SDK_PAY_FLAG = 11;
    private static final int SDK_CHECK_FLAG = 12;
    private Handler nHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
                        /**
                         * 支付宝支付成功后调接口
                         */
                        payOk();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getApplication(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                        /* 发送信息到服务器说支付结果确认中 */
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getApplication(), "支付失败", Toast.LENGTH_SHORT).show();
                        /* 发送信息到服务器说支付失败 */
                        }
                    }

                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(getApplication(), "检查结果为：" + msg.obj, Toast.LENGTH_LONG);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    private void payOk() {
        if (backmode == 0) {
            //默认
            OrderListActivity.goTo(bContext, 2);
        } else {
            EventBus.getDefault().post(new EventRefresh(EventRefresh.PAY_BACK));
        }
        finish();
    }

    private void payZhiFuBao(String return_str) {
        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = return_str;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                // 处理支付结果
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                nHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void weChatPay(PayInfo post) {
        if (TextUtils.isEmpty(post.appid) || TextUtils.isEmpty(post.noncestr) || TextUtils.isEmpty(post.partnerid) || TextUtils.isEmpty(post.prepayid)
                || TextUtils.isEmpty(post.sign) || post.timestamp == 0) {
            T.showShort(bContext, "数据有误，不能支付！！");
            return;
        }
        IWXAPI msgApi = WXAPIFactory.createWXAPI(bContext, null);
        msgApi.registerApp(post.appid);
        PayReq req = new PayReq();
        req.appId = post.appid;// 微信提供的APPID
        req.partnerId = post.partnerid;
        req.prepayId = post.prepayid;
        req.packageValue = post.packages;
        req.nonceStr = post.noncestr;
        req.timeStamp = post.timestamp + "";
        req.sign = post.sign;
        msgApi.sendReq(req);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh != null && refresh.getAction().equals(EventRefresh.PAY_OK)) {
            payOk();
        }
    }
}
