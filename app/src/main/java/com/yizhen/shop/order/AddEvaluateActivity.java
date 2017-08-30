package com.yizhen.shop.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseActivity;
import com.yizhen.shop.base.ImagePagerActivity;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.model.order.Order;
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

public class AddEvaluateActivity extends BaseActivity {
    private RecyclerView rv;
    private TitleBar titleBar;
    private Order order;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private int position;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_evaluate;
    }

    public static void goTo(Context context, Order order) {
        Intent intent = new Intent(context, AddEvaluateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        order = (Order) intent.getSerializableExtra("order");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        rv.setAdapter(adapter);
        adapter.setNewData(order.goods_list);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_goods_evaluate();
            }
        });
    }


    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_order_evaluate) {
            @Override
            protected void convert(final BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_spec, item.sku_name);
                helper.setText(R.id.tv_num, item.num + "");
                ImageLoader.load(bContext, item.goods_picture, (ImageView) helper.getView(R.id.imv));
                //ratinbar
                if (item.evaluate_scores == 0) {
                    item.evaluate_scores = 5;
                }

                RatingBar ratingBar = helper.getView(R.id.ratingBar);
                ratingBar.setRating(item.evaluate_scores);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        item.evaluate_scores = (int) v;
                    }
                });

                //edit
                final EditText edt = helper.getView(R.id.edt);
                if (edt.getTag() instanceof TextWatcher) {
                    edt.removeTextChangedListener((TextWatcher) edt.getTag());
                }
                if (TextUtils.isEmpty(item.evaluate_content)) {
                    edt.setText("");
                } else {
                    edt.setText(item.evaluate_content);
                }
                TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        item.evaluate_content = s.toString();
                    }
                };
                edt.addTextChangedListener(watcher);
                edt.setTag(watcher);

                //MultiImageView2
                final MultiImageView2 multiImageView = helper.getView(R.id.multiImageView);
                if (item.evaluate_image_list == null) {
                    item.evaluate_image_list = new ArrayList<String>();
                }
                multiImageView.setList(item.evaluate_image_list);
                multiImageView.setOnItemClickListener(new MultiImageView2.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                        ImagePagerActivity.startImagePagerActivity(bContext, item.evaluate_image_list, position, imageSize);
                    }

                    @Override
                    public void onLastItemClick(View view, int position) {
                        if (item.evaluate_image_list.size() >= 4) {
                            T.showShort(bContext, "最多4张");
                            return;
                        }
                        position = helper.getAdapterPosition();
                        PhotoPicker.builder()
                                .setPhotoCount(4 - item.evaluate_image_list.size())
                                .setShowCamera(true)
                                .setShowGif(false)
                                .setPreviewEnabled(true)
                                .start(AddEvaluateActivity.this, PhotoPicker.REQUEST_CODE);


                    }

                    @Override
                    public void onDeleteItemClick(View view, final int position) {
                        new AlertDialog.Builder(bContext).setTitle("删除这张?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                item.evaluate_image_list.remove(position);
                                multiImageView.setList(item.evaluate_image_list);
                            }
                        }).setNegativeButton("否", null).show();
                    }
                });
            }
        };
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
                        adapter.getData().get(position).evaluate_image_list.addAll(urls);
                        adapter.notifyItemChanged(position);
                    }

                    @Override
                    public void Failed(int whereFailed) {
                        Toast("第" + (whereFailed + 1) + "张上传失败");
                    }
                },Constants.UPDATE_EVALUATE_IMG);
            }
        }
    }

    private void add_goods_evaluate() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_GOODS_EVALUATE);
        List<String> list = new ArrayList<>();
        for (Goods goods : adapter.getData()) {
            String imgs = "";
            for (String img : goods.evaluate_image_list) {
                imgs += img + ",";
            }
            if (imgs.length() > 0 && imgs.endsWith(",")) {
                imgs = imgs.substring(0, imgs.length() - 1);
            }
            if (TextUtils.isEmpty(goods.evaluate_content)) {
                T.showShort(bContext, "请输入评论内容");
                return;
            }
            list.add("{\"order_goods_id\":" + goods.order_goods_id + ",\"scores\":" + goods.evaluate_scores + ",\"content\":\"" + goods.evaluate_content + "\",\"image_list\":\"" + imgs + "\"}");
        }
        Logger.e(list.toString());
        request.add("evaluate_list", list.toString());
        request.add("order_id", order.order_id);
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
}
