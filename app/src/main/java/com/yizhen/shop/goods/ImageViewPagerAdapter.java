package com.yizhen.shop.goods;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.yizhen.shop.model.goods.Goods;
import com.yizhen.shop.model.goods.Image;
import com.yizhen.shop.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by lewis on 16/7/1.
 */
public class ImageViewPagerAdapter extends LoopPagerAdapter {
    private List<Image> data = new ArrayList<>();
    private Context mContext;

    public void updata(List<Image> list) {
        if (list != null) {
            data = list;
            notifyDataSetChanged();
        }
    }

    public ImageViewPagerAdapter(Context context, RollPagerView viewPager) {
        super(viewPager);
        mContext = context;
        // data = new ArrayList<Banner>();
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //view.setImageResource(imgs[position]);
        ImageLoader.load(mContext, data.get(position).pic_cover_big, view);
       /* view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.goTo(mContext, data.get(position).getBanner_url(), "企业圈");
            }
        });*/
        //view.setImageResource(imgs[position]);
        //ImageLoader.load(mContext, imgs[position], view);
        return view;
    }

    @Override
    public int getRealCount() {
        return data.size();
        //return imgs.length;
    }
}
