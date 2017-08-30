package com.yizhen.shop.goods;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;

/**
 * Created by lewis on 2017/7/25.
 */

public class GoodsDetailWebFragment extends BaseFragment {
    private LewisSwipeRefreshLayout swl;
    private WebView webView;
    private int goods_id;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goods_detail_web;
    }


    public static GoodsDetailWebFragment newInstance(int goods_id) {
        GoodsDetailWebFragment fragment = new GoodsDetailWebFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("goods_id", goods_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        goods_id = getArguments().getInt("goods_id");
        webView = (WebView) view.findViewById(R.id.webView);
        swl = (LewisSwipeRefreshLayout) view.findViewById(R.id.swl);
        swl.setOnRefreshListener(new LewisSwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                webView.reload();
            }
        });
        setWebView();
        webView.loadUrl(Constants.GOODS_DETAIL_WEB + goods_id);
        Logger.d(Constants.GOODS_DETAIL_WEB + goods_id);
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void setWebView() {
        WebSettings webSetting = webView.getSettings();
        // 告诉WebView先不要自动加载图片，等页面finish后再发起图片加载。
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        // MyWebChromeClient webChromeClient = new MyWebChromeClient(webView,
        // MainActivity.this);
        webView.setWebChromeClient(new WebChromeClient() {
            public final static String W_TAG = "WebChromeClient";

            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 0) {
                    swl.setRefreshing(true);
                    Log.i(W_TAG, "onProgressChanged mypDialog.show() newProgress = " + newProgress);
                }
                if (newProgress == 100) {
                    swl.setRefreshing(false);
                }
                Log.i(W_TAG, "onProgressChanged newProgress = " + newProgress);
            }


        });
        // 显示进度条等浏览器选项
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);// 设置在当前webview里面跳转
        webView.setLayerType(View.LAYER_TYPE_NONE, null);
        webSetting.setUseWideViewPort(true);// 可任意比例缩放
        webSetting.setJavaScriptEnabled(true);// 支持js
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setSupportZoom(false); // 支持缩放
        webSetting.setAllowFileAccess(true); // 设置可以访问文件
        webSetting.setLoadsImagesAutomatically(true); // 支持自动加载图片
        //webSetting.setAppCacheEnabled(false);
        webSetting.setDomStorageEnabled(true);// 开启 DOM storage 功能
        webSetting.setCacheMode(webSetting.LOAD_DEFAULT);// 设置缓存
        webView.setSaveEnabled(false);

        //https 问题
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAppCacheEnabled(true);
       /* try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

    public class MyWebViewClient extends WebViewClient {
        private final static String TAG = "MyWebViewClient";

        public MyWebViewClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("tel:")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                view.getContext().startActivity(intent);
                return true;
            } else {
                view.loadUrl(url); // 在当前的webview中跳转到新的url
                Log.i(TAG, "loadUrl = " + url);
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // finish之后加载图片
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            swl.setRefreshing(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            swl.setRefreshing(true);
        }

    }

}
