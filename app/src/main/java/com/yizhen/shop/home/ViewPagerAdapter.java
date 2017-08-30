package com.yizhen.shop.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.yizhen.shop.base.WebViewActivity;
import com.yizhen.shop.model.home.Adv;
import com.yizhen.shop.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 16/7/1.
 */
public class ViewPagerAdapter extends LoopPagerAdapter {
    private List<Adv> data = new ArrayList<>();
    private Context mContext;

    public void updata(List<Adv> list) {
        if (list != null) {
            data = list;
            notifyDataSetChanged();
        }
    }

    public ViewPagerAdapter(Context context, RollPagerView viewPager) {
        super(viewPager);
        mContext = context;
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageLoader.load(mContext, data.get(position).adv_image, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(data.get(position).adv_url)) {
                    WebViewActivity.goTo(mContext, data.get(position).adv_url, data.get(position).adv_title);
                }
            }
        });
        return view;
    }

    @Override
    public int getRealCount() {
        return data.size();
    }
}
