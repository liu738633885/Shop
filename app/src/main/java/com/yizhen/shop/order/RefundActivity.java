package com.yizhen.shop.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.ImagePagerActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.T;
import com.yizhen.shop.util.UpLoadManager;
import com.yizhen.shop.util.imageloader.ImageLoader;
import com.yizhen.shop.widgets.MultiImageView2;
import com.yizhen.shop.widgets.TitleBar;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;


public class RefundActivity extends BaseActivity {
    private MultiImageView2 multiImageView;
    private ArrayList<String> imgList = new ArrayList<String>();
    private int order_id;
    private Goods goods;
    private TextView tv_num, tv_title, tv_spec, tv_price;
    private ImageView imv;
    private LinearLayout ll_mode_1, ll_mode_2, ll_content;
    private RadioGroup rg;
    private int mode;//1退款  2退货
    private TitleBar titleBar;
    private Button btn_ok;
    private EditText edt1, edt2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_refund;
    }

    public static void goTo(Context context, Goods goods, int order_id) {
        Intent intent = new Intent(context, RefundActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("goods", goods);
        intent.putExtras(bundle);
        intent.putExtra("order_id", order_id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        goods = (Goods) intent.getSerializableExtra("goods");
        order_id = intent.getIntExtra("order_id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                if (ll_content.getVisibility() == View.VISIBLE) {
                    ll_content.setVisibility(View.GONE);
                    ll_mode_1.setVisibility(View.VISIBLE);
                    ll_mode_2.setVisibility(View.VISIBLE);
                    btn_ok.setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        });
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_spec = (TextView) findViewById(R.id.tv_spec);
        tv_price = (TextView) findViewById(R.id.tv_price);
        imv = (ImageView) findViewById(R.id.imv);
        tv_num.setText(goods.num + "");
        tv_title.setText(goods.goods_name);
        tv_price.setText("¥ " + goods.price);
        tv_spec.setText(goods.sku_name);
        ImageLoader.load(this, goods.goods_picture, imv);
        edt1 = (EditText) findViewById(R.id.edt1);
        edt2 = (EditText) findViewById(R.id.edt2);
        ll_mode_1 = (LinearLayout) findViewById(R.id.ll_mode_1);
        ll_mode_2 = (LinearLayout) findViewById(R.id.ll_mode_2);
        rg = (RadioGroup) findViewById(R.id.rg);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply_refund();
            }
        });

        ll_mode_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = 1;
                ll_mode_1.setVisibility(View.GONE);
                ll_mode_2.setVisibility(View.GONE);
                ll_content.setVisibility(View.VISIBLE);
                rg.setVisibility(View.GONE);
                multiImageView.setList(imgList);
                btn_ok.setVisibility(View.VISIBLE);
            }
        });
        ll_mode_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = 2;
                ll_mode_1.setVisibility(View.GONE);
                ll_mode_2.setVisibility(View.GONE);
                ll_content.setVisibility(View.VISIBLE);
                rg.setVisibility(View.GONE);
                multiImageView.setList(imgList);
                btn_ok.setVisibility(View.VISIBLE);
            }
        });
        multiImageView = (MultiImageView2) findViewById(R.id.multiImageView);
        multiImageView.setOnItemClickListener(new MultiImageView2.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                ImagePagerActivity.startImagePagerActivity(bContext, imgList, position, imageSize);
            }

            @Override
            public void onLastItemClick(View view, int position) {
                if (imgList.size() >= 4) {
                    T.showShort(bContext, "最多4张");
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(4 - imgList.size())
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(RefundActivity.this, PhotoPicker.REQUEST_CODE);


            }

            @Override
            public void onDeleteItemClick(View view, final int position) {
                new AlertDialog.Builder(bContext).setTitle("删除这张?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imgList.remove(position);
                        multiImageView.setList(imgList);
                    }
                }).setNegativeButton("否", null).show();
            }
        });
    }

    private void apply_refund() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.APPLY_REFUND);
        request.add("order_id", order_id);
        request.add("order_goods_id", goods.order_goods_id);
        request.add("refund_type", mode);
        if (TextUtils.isEmpty(edt1.getText().toString())) {
            T.showShort(this, "必须输入原因");
            return;
        }
        request.add("refund_reason", edt1.getText().toString());
        if (TextUtils.isEmpty(edt2.getText().toString())) {
            T.showShort(this, "必须输入金额");
            return;
        }
        request.add("refund_require_money", edt2.getText().toString());
        String str = "";
        for (String s : imgList) {
            str = str + s + ",";
        }
        if (str.length() > 0 && str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
            request.add("refund_imgs", str);
        }
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(bContext, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                UpLoadManager.uploadImgs(this, photos, new UpLoadManager.UpLoadListener() {
                    @Override
                    public void Success(List<String> urls) {
                        imgList.addAll(urls);
                        multiImageView.setList(imgList);
                    }

                    @Override
                    public void Failed(int whereFailed) {
                        Toast("第" + (whereFailed + 1) + "张上传失败");
                    }
                }, Constants.UPDATE_EVALUATE_IMG);
            }
        }
    }

}
