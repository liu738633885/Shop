package com.yizhen.shop.util.upadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.yizhen.shop.R;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.manager.SettingsManager;
import com.yanzhenjie.nohttp.Logger;


/**
 * Created by lewis on 16/8/20.
 */
public class UpdataAppManager {
    public static void updata(final Activity activity, final boolean isSetting, final HaveNewCode haveNewCode) {
        PgyUpdateManager.register(activity, activity.getString(R.string.file_provider),
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        Logger.e("appinf" + result);
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        StringBuffer contentText = new StringBuffer();
                        contentText.append("版本:").append(appBean.getVersionName()).append("\n");
                        contentText.append("更新内容:\n");

                        boolean canCancel = true;
                        try {
                            if (haveNewCode != null) {
                                haveNewCode.haveNewCode(true);
                            }
                            SettingsManager.saveNewAppCode(Integer.parseInt(appBean.getVersionCode()));

                            if (appBean.getReleaseNote().endsWith("mode1")) {
                                contentText.append(appBean.getReleaseNote().replace("mode1", ""));
                                //不在首页提示更新
                                if (!isSetting) {
                                    //不在设置界面 直接 return  不提示弹出框
                                    return;
                                }

                            } else if (appBean.getReleaseNote().endsWith("mode2")) {
                                contentText.append(appBean.getReleaseNote().replace("mode2", ""));
                                //在首页提示更新可以取消
                                canCancel = true;
                            } else if (appBean.getReleaseNote().endsWith("mode3")) {
                                contentText.append(appBean.getReleaseNote().replace("mode3", ""));
                                //在首页提示更新,且不能取消
                                canCancel = false;
                            } else {
                                contentText.append(appBean.getReleaseNote());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(activity).setTitle("有新版本!").setMessage(contentText).setNegativeButton("下载更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startDownloadTask(
                                        activity,
                                        appBean.getDownloadURL());
                            }
                        }).create();
                        alertDialog.setCancelable(canCancel);
                        alertDialog.setCanceledOnTouchOutside(canCancel);
                        alertDialog.show();
                      /*  PromptDialog promptDialog = new PromptDialog(activity);
                        promptDialog.setDialogType(PromptDialog.DIALOG_TYPE_INFO);
                        promptDialog.setAnimationEnable(true);
                        promptDialog.setTitleText("是否更新新版本?");
                        promptDialog.setContentText(contentText);
                        promptDialog.setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                startDownloadTask(
                                        activity,
                                        appBean.getDownloadURL());
                            }
                        });
                        promptDialog.setCancelable(canCancel);
                        promptDialog.setCanceledOnTouchOutside(canCancel);
                        promptDialog.show();*/
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        if (haveNewCode != null) {
                            haveNewCode.haveNewCode(false);
                        }
                        if (isSetting) {
                            T.showShort(activity, "当前已是最新版本");
                        }
                    }
                });
    }

    public static void updata(Activity activity, boolean isSetting) {
        updata(activity, isSetting, null);
    }

    public interface HaveNewCode {
        void haveNewCode(boolean yes);
    }

}