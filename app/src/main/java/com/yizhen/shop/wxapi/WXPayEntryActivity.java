package com.yizhen.shop.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yizhen.shop.R;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.util.T;
import com.yizhen.shop.wxPay.WXConstants;

import org.greenrobot.eventbus.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
    private String isWho;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Logger.d("onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_COMM:
                    // 一般错误
                    T.showShort(WXPayEntryActivity.this, "支付失败");
                    break;
                case BaseResp.ErrCode.ERR_OK:
                    // 正确返回
                    T.showShort(WXPayEntryActivity.this, "支付成功");
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.PAY_OK));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    T.showShort(WXPayEntryActivity.this, "取消付款");
                    //EventBus.getDefault().post(new EventRefresh(WXPayEntryActivity.class.getName()+"ERR_USER_CANCEL"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    T.showShort(WXPayEntryActivity.this, "认证被否决");
                    // 认证被否决
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    T.showShort(WXPayEntryActivity.this, "不支持错误");
                    // 不支持错误
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    T.showShort(WXPayEntryActivity.this, "发送失败");
                    // 发送失败
                    break;
            }
        }
        finish();
    }

    class Fail extends Thread {
        public void run() {
//            SharedPreferences share1 = getSharedPreferences("wxpay", 0);
//            String order_id1 = (String) SPUtils.get(WXPayEntryActivity.this, SPConfig.SHARED_KEY_WX_ORDER_ID, "");//share1.getString("order_id", "");
//            String price1 = (String) SPUtils.get(WXPayEntryActivity.this, SPConfig.SHARED_KEY_WX_PRICE, "");//share1.getString("price", "");
//            String wx_order_id1 = (String) SPUtils.get(WXPayEntryActivity.this, SPConfig.SHARED_KEY_WX_PREPAY_ID, "");//share1.getString("wx_order_id", "");// name1=value1&
//            String post1 = "order_id=" + order_id1 + "&pay_status=" + "0" + "&wx_order_id=" + wx_order_id1 + "&price="
//                    + price1 + "";
//            NetWork.sendPost(notify_url, post1);
        }
    }

    class Success extends Thread {
        public void run() {
//			SharedPreferences share = getSharedPreferences("wxpay", 0);
//            String order_id1 = (String) SPUtils.get(WXPayEntryActivity.this, SPConfig.SHARED_KEY_WX_ORDER_ID, "");//share.getString("order_id", "");
//            String price1 = (String) SPUtils.get(WXPayEntryActivity.this, SPConfig.SHARED_KEY_WX_PRICE, ""); //share.getString("price", "");
//            String wx_order_id1 = (String) SPUtils.get(WXPayEntryActivity.this, SPConfig.SHARED_KEY_WX_PREPAY_ID, "");//share.getString("wx_order_id", "");
//            String post1 = "order_id=" + order_id1 + "&pay_status=" + "1" + "&wx_order_id=" + wx_order_id1 + "&price="
//                    + price1 + "";
//            NetWork.sendPost(notify_url, post1);
            // SharedPreferences.Editor shareEditor = share.edit();
            // shareEditor.commit();
        }
    }

}