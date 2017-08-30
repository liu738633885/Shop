package com.yizhen.shop.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.soundcloud.android.crop.Crop;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.model.UserInfo;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.BitmapUtils;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.UpLoadManager;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.util.manager.UserManager;
import com.yizhen.shop.widgets.TitleBar;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;


public class UserProfileActivity extends BaseActivity {
    private ImageView imv;
    private EditText edt;
    private RadioButton rb0, rb1, rb2;
    private String user_headimg;
    private TitleBar titleBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_user_info();
            }
        });
        imv = (ImageView) findViewById(R.id.imv);
        edt = (EditText) findViewById(R.id.edt);
        rb0 = (RadioButton) findViewById(R.id.rb0);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        ((View) imv.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(UserProfileActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
        getInfo();
    }


    private void getInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_INFO);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfo userInfo = netBaseBean.parseObject(UserInfo.class);
                    updateUI(userInfo);
                    UserManager.saveUserInfo(userInfo);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void updateUI(UserInfo userInfo) {
        user_headimg = userInfo.user_headimg;
        ImageLoader.loadHeadImage(this, userInfo.user_headimg, imv, -1);
        edt.setText(userInfo.nick_name);
        if (userInfo.sex == 0) {
            rb0.setChecked(true);
        } else if (userInfo.sex == 1) {
            rb1.setChecked(true);
        } else if (userInfo.sex == 2) {
            rb2.setChecked(true);
        }
    }

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
                beginCrop(sourceUri);

            }
        }
        if (requestCode == Crop.REQUEST_CROP) {
            UpLoadManager.uploadImg(this, Crop.getOutput(data).getPath(), new UpLoadManager.SingleUpLoadListener() {
                @Override
                public void onComplete(boolean isSuccess, String path_message) {
                    if (isSuccess) {
                        user_headimg = path_message;
                        ImageLoader.loadHeadImage(bContext, user_headimg, imv, -1);
                    }
                }
            }, 500, 500, Constants.UPLOAD_IMGES);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(BitmapUtils.getSaveRealPath(), System.currentTimeMillis() + "cropped.jpg"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void update_user_info() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.UPDATE_USER_INFO);
        request.add("user_headimg", user_headimg);
        if (TextUtils.isEmpty(edt.getText().toString())) {
            T.showShort(bContext, "请输入会员名");
            return;
        }
        request.add("nick_name", edt.getText().toString());
        if (rb1.isChecked()) {
            request.add("sex", 1);
        } else if (rb2.isChecked()) {
            request.add("sex", 2);
        } else {
            request.add("sex", 0);
        }
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(bContext, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    getInfo();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

}
