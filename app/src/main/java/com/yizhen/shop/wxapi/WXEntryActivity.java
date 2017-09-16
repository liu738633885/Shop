package com.yizhen.shop.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yizhen.shop.R;
import com.yizhen.shop.wxPay.WXConstants;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID, true);

        api.handleIntent(getIntent(), this);
    }

    // protected void onNewIntent(Intent intent) {
    // super.onNewIntent(intent);
    // setIntent(intent);
    // api.handleIntent(intent, this);
    // }

    public void onReq(BaseReq req) {
        Logger.i("rep.getType() = " + req.getType());
        finish();
    }

    public void onResp(BaseResp resp) {
        Logger.i("resp.getType() = " + resp.getType() + " errCode = " + resp.errCode + " resp.errStr=" + resp.errStr
                + " resp.transaction" + resp.transaction);
        // 判断返回类型，再进行相应操作
        switch (resp.getType()) {

            case ConstantsAPI.COMMAND_PAY_BY_WX:
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_COMM:
                        // 一般错误
                        break;
                    case BaseResp.ErrCode.ERR_OK:
                        // 正确返回
                        Toast.makeText(this, getString(R.string.pay_success), Toast.LENGTH_SHORT);
//				Intent goToMain = new Intent();
//				goToMain.setClass(WXEntryActivity.this, MainActivity.class);
//				goToMain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				goToMain.putExtra("result", 1);
//				startActivity(goToMain);
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        // 用户取消
                        Toast.makeText(this, getString(R.string.pay_cancel), Toast.LENGTH_SHORT);
//				Intent cancelIntent = new Intent();
//				cancelIntent.setClass(WXEntryActivity.this, MainActivity.class);
//				cancelIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				cancelIntent.putExtra("result", 2);
//				startActivity(cancelIntent);
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        // 认证被否决
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        // 不支持错误
                        break;
                    case BaseResp.ErrCode.ERR_SENT_FAILED:
                        // 发送失败
                        break;
                }
                // AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // builder.setTitle(R.string.app_tip);
                // builder.setMessage(
                // getString(R.string.pay_result_callback_msg, resp.errStr +
                // ";code=" + String.valueOf(resp.errCode)));
                // builder.show();
                break;
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_COMM:
                        // 一般错误
                        Toast.makeText(this, "一般错误", Toast.LENGTH_SHORT).show();
                        break;
                    case BaseResp.ErrCode.ERR_OK:
                        // 正确返回
                        Toast.makeText(this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
                        //EventBus.getDefault().post(new EventRefresh(HuodongDetailActivity.class.getName()+"SHAREOK"));
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        // 用户取消
                        Toast.makeText(getApplicationContext(), getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
                        //EventBus.getDefault().post(new EventRefresh(HuodongDetailActivity.class.getName()+"SHARE"));
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        // 认证被否决
                        Toast.makeText(this, "认证被否决", Toast.LENGTH_SHORT).show();
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        // 不支持错误
                        break;
                    case BaseResp.ErrCode.ERR_SENT_FAILED:
                        // 发送失败
                        Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show();
                        //EventBus.getDefault().post(new EventRefresh(HuodongDetailActivity.class.getName()+"SHARE"));
                        break;
                }
                break;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_COMM:
                // 一般错误
                break;
            case BaseResp.ErrCode.ERR_OK:
                // 正确返回
                // Toast.makeText(WXPayEntryActivity.this, "分享成功！",
                // Toast.LENGTH_SHORT);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                // 用户取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                // 认证被否决
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                // 不支持错误
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                // 发送失败
                break;
        }
        finish();
    }
}