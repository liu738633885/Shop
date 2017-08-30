package com.yizhen.shop.goods;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.IconHintView;
import com.yizhen.shop.Constants;
import com.yizhen.shop.R;
import com.yizhen.shop.base.BaseFragment;
import com.yizhen.shop.model.event.EventRefresh;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.PromotionInfo;
import com.yizhen.shop.model.netmodel.NetBaseBean;
import com.yizhen.shop.net.CallServer;
import com.yizhen.shop.net.HttpListenerCallback;
import com.yizhen.shop.net.NetBaseRequest;
import com.yizhen.shop.net.RequsetFactory;
import com.yizhen.shop.util.DateUtils;
import com.yizhen.shop.util.imageloader.ImageLoader;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by lewis on 2017/7/25.
 */

public class GoodsDetailFragment extends BaseFragment {
    public static final String TAG = GoodsDetailFragment.class.getSimpleName();
    private RollPagerView rollPagerView;
    private ImageViewPagerAdapter pagerAdapter;
    private LinearLayout ll_choose_spec, ll_evaluate, ll_comment_imgs;
    private View ll_consult;
    private TextView tv_qianggou, tv_title, tv_introduction, tv_price, tv_price_yuan, tv_coupon_num, tv_give_point, tv_comment_num, tv_wen, tv_da, tv_comment_nickname, tv_comment_time, tv_comment_content, tv_comment_spec;
    private View wen, da;
    private RecyclerView rv;
    private BaseQuickAdapter<Goods, BaseViewHolder> adapter;
    private View headView;
    private ImageView imv_comment_headimg, imv_comment_level;
    private int goods_id;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goods_detail;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        initAdapter();
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_goods_detail, (ViewGroup) rv.getParent(), false);
        adapter.addHeaderView(headView);
        rv.setAdapter(adapter);
        rollPagerView = headView.findViewById(R.id.roll_view_pager);
        rollPagerView.setHintView(new IconHintView(getActivity(), R.drawable.shape_viewpager_point_focus, R.drawable.shape_viewpager_point_normal, 0));
        pagerAdapter = new ImageViewPagerAdapter(getActivity(), rollPagerView);
        rollPagerView.setAdapter(pagerAdapter);
        ll_choose_spec = headView.findViewById(R.id.ll_choose_spec);
        ll_evaluate = headView.findViewById(R.id.ll_evaluate);
        ll_consult = headView.findViewById(R.id.ll_consult);
        ll_comment_imgs = headView.findViewById(R.id.ll_comment_imgs);
        tv_qianggou = headView.findViewById(R.id.tv_qianggou);
        tv_title = headView.findViewById(R.id.tv_title);
        tv_introduction = headView.findViewById(R.id.tv_introduction);
        tv_price = headView.findViewById(R.id.tv_price);
        tv_price_yuan = headView.findViewById(R.id.tv_price_yuan);
        tv_coupon_num = headView.findViewById(R.id.tv_coupon_num);
        tv_give_point = headView.findViewById(R.id.tv_give_point);
        tv_comment_num = headView.findViewById(R.id.tv_comment_num);
        tv_comment_nickname = headView.findViewById(R.id.tv_comment_nickname);
        tv_comment_time = headView.findViewById(R.id.tv_comment_time);
        tv_comment_content = headView.findViewById(R.id.tv_comment_content);
        tv_comment_spec = headView.findViewById(R.id.tv_comment_spec);
        imv_comment_headimg = headView.findViewById(R.id.imv_comment_headimg);
        imv_comment_level = headView.findViewById(R.id.imv_comment_level);
        tv_wen = headView.findViewById(R.id.tv_wen);
        tv_da = headView.findViewById(R.id.tv_da);
        wen = headView.findViewById(R.id.wen);
        da = headView.findViewById(R.id.da);
        handler = new Handler();
        ll_consult.setBackground(null);
        ((View)ll_consult.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConsultListActivity.goTo(getActivity(), goods_id);
            }
        });
        ll_choose_spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoodsDetailActivity) getActivity()).showChooseSpec(0);
            }
        });
        ((View) tv_coupon_num.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoodsDetailActivity) getActivity()).coupon_list();
            }
        });
        ll_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoodsDetailActivity) getActivity()).move2();
            }
        });
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Goods, BaseViewHolder>(R.layout.item_goods) {
            @Override
            protected void convert(BaseViewHolder helper, final Goods item) {
                helper.setText(R.id.tv_title, item.goods_name);
                helper.setText(R.id.tv_price, "¥ " + item.promotion_price);
                ImageLoader.loadHome(mContext, item.pic_cover_mid, (ImageView) helper.getView(R.id.imv));
                helper.setText(R.id.tv_desc, item.keywords);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GoodsDetailActivity.goTo(mContext, item.goods_id);
                    }
                });
            }
        };
    }


    private void updateUI(Goods goods) {
        goods_id = goods.goods_id;
        pagerAdapter.updata(goods.img_list);
        updateQianggou(goods.promotion_info);
        tv_title.setText(goods.goods_name);
        tv_introduction.setText(goods.introduction);
        tv_price.setText("¥ " + goods.promotion_price);
        tv_price_yuan.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置中划线并加清晰
        tv_price_yuan.setText(goods.price);
        if (goods.coupon_count < 1) {
            ((View) tv_coupon_num.getParent()).setVisibility(View.GONE);
        } else {
            ((View) tv_coupon_num.getParent()).setVisibility(View.VISIBLE);
            tv_coupon_num.setText(goods.coupon_count + "个优惠券");
        }
        if (goods.give_point < 1) {
            ((View) tv_give_point.getParent()).setVisibility(View.GONE);
        } else {
            ((View) tv_give_point.getParent()).setVisibility(View.VISIBLE);
            tv_give_point.setText("可获得" + goods.give_point + "积分");
        }
        if (goods.comment_num < 1) {
            tv_comment_num.setText("用户评价");
        } else {
            tv_comment_num.setText("用户评价 (" + goods.comment_num + ")");

        }
        if (goods.evaluate_info != null) {
            ll_evaluate.setVisibility(View.VISIBLE);
            ImageLoader.loadHeadImage(getActivity(), goods.evaluate_info.user_headimg, imv_comment_headimg, -1);
            tv_comment_nickname.setText(goods.evaluate_info.member_name);
            try {
                tv_comment_time.setText(DateUtils.tenLongToString(goods.evaluate_info.addtime, DateUtils.yyyyMMDD2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_comment_content.setText(goods.evaluate_info.content);
            tv_comment_spec.setText(goods.evaluate_info.goods_name);
            if (goods.evaluate_info.image != null && goods.evaluate_info.image.size() > 0) {
                ((View) ll_comment_imgs.getParent()).setVisibility(View.VISIBLE);
                ll_comment_imgs.removeAllViews();
                for (String img : goods.evaluate_info.image) {
                    ImageView imageView = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.item_comment_img, ll_comment_imgs, false);
                    ImageLoader.load(getActivity(), img, imageView);
                    ll_comment_imgs.addView(imageView);
                }
            } else {
                ((View) ll_comment_imgs.getParent()).setVisibility(View.GONE);
            }
        } else {
            ll_evaluate.setVisibility(View.GONE);
        }
        if (goods.consult == null) {
            ll_consult.setVisibility(View.GONE);
        } else {
            ll_consult.setVisibility(View.VISIBLE);
            tv_wen.setText(goods.consult.consult_content);
            if (!TextUtils.isEmpty(goods.consult.consult_reply)) {
               /* tv_da.setVisibility(View.VISIBLE);
                da.setVisibility(View.VISIBLE);*/
                tv_da.setText(goods.consult.consult_reply);
            } else {
               /* tv_da.setVisibility(View.INVISIBLE);
                da.setVisibility(View.INVISIBLE);*/
                tv_da.setText("还没有回答");
            }
        }
        getTuijian(goods.category_id);
    }

    private Handler handler;

    private void updateQianggou(final PromotionInfo info) {
        if (info == null) {
            tv_qianggou.setVisibility(View.GONE);
            tv_price_yuan.setVisibility(View.GONE);
            return;
        }
        final long start_time = info.start_time;
        final long end_time = info.end_time;
        final String promotion_name = info.promotion_name;
        tv_qianggou.setVisibility(View.VISIBLE);
        tv_price_yuan.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (start_time > System.currentTimeMillis() / 1000) {
                    tv_qianggou.setText("距" + promotion_name + "开始还有: " + getTime(start_time));
                    handler.postDelayed(this, 1000);
                }
                if (start_time <= System.currentTimeMillis() / 1000 && end_time >= System.currentTimeMillis() / 1000) {
                    tv_qianggou.setText("距" + promotion_name + "结束还有: " + getTime(end_time));
                    handler.postDelayed(this, 1000);
                }
                if (end_time < System.currentTimeMillis() / 1000) {
                    tv_qianggou.setText(promotion_name + "已结束");
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.post(runnable);
    }

    private String getTime(long time) {
        long time1 = Math.abs(time - System.currentTimeMillis() / 1000);
        long oneDay = 24 * 60 * 60;
        long oneHour = 60 * 60;
        long oneM = 60;
        if (time1 > oneDay) {
            return Math.floor(time1 / oneDay) + "天";
        } else {
            int h = (int) Math.floor(time1 / oneHour);
            int m = (int) Math.floor((time1 - h * oneHour) / oneM);
            int s = (int) Math.floor(time1 - h * oneHour - m * oneM);
            return h + "小时 " + m + "分钟 " + s + "秒";
        }
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e == null) {
            return;
        }
        if (e.getWhere().equals(TAG) && e.getData() != null) {
            updateUI((Goods) e.getData());

        }
    }

    private void getTuijian(int category_id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GOODS_DETAIL_RECOMMEND);
        request.add("category_id", category_id);
        CallServer.getRequestInstance().add(getActivity(), 0x04, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Goods> goodsList = netBaseBean.parseList(Goods.class);
                    adapter.setNewData(goodsList);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

}
