package com.yizhen.shop.wxPay;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WXPay {
	private Activity mActivity;
//	private Context mContext;

	public WXPay(Activity a) {
		mActivity = a;
		msgApi = WXAPIFactory.createWXAPI(mActivity, null);
	}

//	public WXPay(Context a) {
//		mContext = a;
//		msgApi = WXAPIFactory.createWXAPI(mContext, null);
//	}

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";
	private String[] order;
	private String outTradeNo;// 交易订单号
	PayReq req;
	// 调用API前，需要先向微信注册您的APPID
	IWXAPI msgApi;
	TextView show;
	Map<String, String> resultunifiedorder;
	StringBuffer sb;

	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(WXConstants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("genPackageSign", packageSign);
		return packageSign;
	}

	public String genAppSign(List<NameValuePair> params) {
		StringBuilder sbappsign = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sbappsign.append(params.get(i).getName());
			sbappsign.append('=');
			sbappsign.append(params.get(i).getValue());
			sbappsign.append('&');
		}
		sbappsign.append("key=");
		sbappsign.append(WXConstants.API_KEY);

		// this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sbappsign.toString().getBytes()).toUpperCase();
		Log.e("oriongenAppSign", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("oriontoXml", sb.toString());
		// 原来的
		// return sb.toString();

		// 中文签名错误后修改
		return new String(sb.toString().getBytes(), "ISO8859-1");
	}

	// 000
	private String genXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb2.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><xml>");
		for (int i = 0; i < params.size(); i++) {
			// sb是用来计算签名的
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
			// sb2是用来做请求的xml参数
			sb2.append("<" + params.get(i).getName() + ">");
			sb2.append(params.get(i).getValue());
			sb2.append("</" + params.get(i).getName() + ">");
		}
		sb.append("key=");
		sb.append(WXConstants.API_KEY);
		String packageSign = null;
		// 生成签名
		packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		sb2.append("<sign><![CDATA[");
		sb2.append(packageSign);
		sb2.append("]]></sign>");
		sb2.append("</xml>");

		// 这一步最关键 我们把字符转为 字节后,再使用“ISO8859-1”进行编码，得到“ISO8859-1”的字符串
		try {
			return new String(sb2.toString().getBytes(), "ISO8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orionException1", e.toString());
		}
		return null;

	}

	public String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	public long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	/**
	 * 获取订单
	 * 
	 * @param name
	 *            商品名
	 * @param detail
	 *            商品详情
	 * @param price
	 *            商品价格
	 * @param tradeId
	 *            订单id
	 * @return
	 */
	public String genProductArgs(String name, String detail, String price, String tradeId) {
		StringBuffer xml = new StringBuffer();
		try {
			String nonceStr = genNonceStr();
			// outTradeNo = genOutTradNo();
			// outTradeNo = getIntent().getStringExtra("tradeId");
			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", WXConstants.APP_ID));
			packageParams.add(new BasicNameValuePair("body", name));// 内容

			packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));

			packageParams.add(new BasicNameValuePair("mch_id", WXConstants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", "http://m.ainana.com/index.php"));
			packageParams.add(new BasicNameValuePair("out_trade_no", tradeId));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee",
					price));/*
							 * 价格order[2] 单位：分 整数
							 */
			// 统一支付接口，可接叐JSAPI/NATIVE/APP下预支付订单，返回预支付订单号。
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));// APP
																			// NATIVE

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);
			// String xmlstring = genXml(packageParams);// 测试
			return xmlstring;

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	private void genPayReq() {

		req.appId = WXConstants.APP_ID;
		req.partnerId = WXConstants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		show.setText(sb.toString());

		Log.e("oriongenPayReq", signParams.toString());

	}

	private void sendPayReq() {
		msgApi.registerApp(WXConstants.APP_ID);
		msgApi.sendReq(req);
	}

}
