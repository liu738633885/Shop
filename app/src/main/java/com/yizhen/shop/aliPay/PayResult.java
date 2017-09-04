package com.yizhen.shop.aliPay;import android.text.TextUtils;public class PayResult {	private String resultStatus;	private String result;	private String memo;	private String tradeId;	public PayResult(String rawResult) {		if (TextUtils.isEmpty(rawResult))			return;		String[] resultParams = rawResult.split(";");		for (String resultParam : resultParams) {			if (resultParam.startsWith("resultStatus")) {				resultStatus = gatValue(resultParam, "resultStatus");			}			if (resultParam.startsWith("result")) {				result = gatValue(resultParam, "result");				// 取tradeId				if (rawResult.contains("out_trade_no=")) {					String result1[] = rawResult.split("out_trade_no=\"");					String result2[] = result1[1].split("\"");					// int index = result1[1].indexOf("\"");					// tradeId = result1[1].substring(0, index);					tradeId = result2[0];					System.out.println(tradeId);				}			}			if (resultParam.startsWith("memo")) {				memo = gatValue(resultParam, "memo");			}		}	}	@Override	public String toString() {		return "resultStatus={" + resultStatus + "};memo={" + memo + "};result={" + result + "}";	}	private String gatValue(String content, String key) {		String prefix = key + "={";		try{			return content.substring(content.indexOf(prefix) + prefix.length(), content.lastIndexOf("}"));		}catch (Exception e){			e.printStackTrace();			return "";		}	}	/**	 * @return the resultStatus	 */	public String getResultStatus() {		return resultStatus;	}	/**	 * @return the memo	 */	public String getMemo() {		return memo;	}	/**	 * @return the result	 */	public String getResult() {		return result;	}	/**	 * @return the tradeId	 */	public String getTradeId() {		return tradeId;	}}