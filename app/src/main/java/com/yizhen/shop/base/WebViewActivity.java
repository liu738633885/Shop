package com.yizhen.shop.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.R;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.order.PayActivity;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.widgets.LewisSwipeRefreshLayout;
import com.yizhen.shop.widgets.TitleBar;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;


public class WebViewActivity extends BaseActivity {
    private TitleBar titleBar;
    private LewisSwipeRefreshLayout swl;
    private WebView webView;
    private String TAG = WebViewActivity.class.getSimpleName();
    private String title, url;
    private boolean share;
    private Handler mHandler = new Handler();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_web;
    }


    public static void goTo(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void goToWithLogIn(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url + "&user_id=" + UserManager.getId() + "&token=" + UserManager.getToken());
        context.startActivity(intent);
    }

    public static void goToShare(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("share", true);
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        share = intent.getBooleanExtra("share", false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        if (!TextUtils.isEmpty(title)) {
            titleBar.setCenterText(title);
        }
        if (share) {
            titleBar.setRight_text("分享");
            titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ShareUtils.ShareWX(WebViewActivity.this, url, "仝君协仝", "仝君协仝-互联网工作平台，轻松实现自由工作！邀您使用：https://m.jingoal.com ", "", ShareUtils.SHARE_TO_SESSION);
                }
            });
        }
        webView = (WebView) findViewById(R.id.webView);
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(new LewisSwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                webView.reload();
            }
        });
        setWebView();
        webView.loadUrl(url);
        //webView.loadUrl("file:///android_asset/javascript.html");
        Logger.d(url);
    }

    /**
     * 左上角返回键的点击事件
     */
    public void back() {
        finish();
        /*if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }*/
    }


    /**
     * 设置webview
     */
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
        webView.setWebChromeClient(defaultWebChromeClient);// 显示进度条等浏览器选项
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
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "putijiaye");
       /* try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (webView != null) {// 移除webview
            swl.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    private ValueCallback<Uri[]> valueCallbacks;
    private ValueCallback<Uri> valueCallback;
    private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {
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

        /* //Android 3.0 以下
         public void openFileChooser(ValueCallback<Uri> valueCallback) {
             mUploadMessage = valueCallback;
             selectImage();//就是上面
         }
         // Android 3~4.1
         public void openFileChooser(ValueCallback valueCallback, String acceptType) {
             mUploadMessage = valueCallback;
             selectImage();
         }*/

        // Android  4.1以上
        public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
            valueCallback = filePathCallback;
            choosePic();
        }

        // Android 5.0以上
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
           /* mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast("选择了照片");
                }
            });*/
            choosePic();
            valueCallbacks = filePathCallback;
            return true;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Uri sourceUri = Uri.fromFile(new File(photos.get(0)));
                if (valueCallback != null) {
                    Uri[] uris = {sourceUri};
                    valueCallbacks.onReceiveValue(uris);
                }
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(sourceUri);
                }

            }
        }
    }

    private void choosePic() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(WebViewActivity.this, PhotoPicker.REQUEST_CODE);
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
                swl.setEnabled(!url.startsWith("http://api.idocv.com"));
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


    final class MyJavaScriptInterface {

        MyJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void Jpay(final String pay_sn) {
            mHandler.post(new Runnable() {
                public void run() {
                    //mWebView.loadUrl("javascript:wave()");
                    //Toast("order_no:" + pay_sn);
                    PayActivity.goTo(bContext, pay_sn, 1);
                }
            });
        }

        @JavascriptInterface
        public void hello(final String order_no) {
            mHandler.post(new Runnable() {
                public void run() {
                    //mWebView.loadUrl("javascript:wave()");
                    Toast("order_no:" + order_no);
                }
            });
        }
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh != null && refresh.getAction().equals(EventRefresh.PAY_BACK)) {
            webView.loadUrl(url);
        }
    }

}
