package com.yizhen.shop.wxPay;

import java.security.MessageDigest;

public class MD5 {

	private MD5() {
	}

	public final static String getMessageDigest(byte[] buffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(buffer);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * MD5编码
	 * 
	 * @param origin
	 *            原始字符串
	 * @return 经过MD5加密之后的结果
	 */
	// public static String MD5Encode(String origin) {
	// String resultString = null;
	// try {
	// resultString = origin;
	// MessageDigest md = MessageDigest.getInstance("MD5");
	// // resultString =
	// //
	// byteArrayToHexString(md.digest(resultString.getBytes()));//原文件内容，可能原因是：win2003时系统缺省编码为GBK，win7为utf-8
	// resultString =
	// byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));// 正确的写法
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return resultString;
	// }
}
