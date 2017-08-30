package com.yizhen.shop.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.yizhen.shop.R;
import com.yizhen.shop.util.DensityUtil;
import com.yizhen.shop.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shoyu
 * @ClassName MultiImageView.java
 * @Description: 显示1~N张图片的View
 */

public class MultiImageView2 extends LinearLayout {
    public static int MAX_WIDTH = 0;

    // 照片的Url列表
    private List<String> imagesList = new ArrayList<>();

    /**
     * 长度 单位为Pixel
     **/
    private int pxMoreWandH = 0;// 多张图的宽高
    private int pxImagePadding = DensityUtil.dip2px(getContext(), 0);// 图片间的间距
    private boolean canEdit = true;

    private int MAX_PER_ROW_COUNT = 4;// 每行显示最大数

    private LayoutParams morePara, moreParaColumnFirst;
    private LayoutParams rowPara;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public MultiImageView2(Context context) {
        super(context, null);
    }

    public MultiImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setList(List<String> list) {
        if (list == null) {
            Log.e("MultiImageView2", "imageList is null...");
            this.imagesList = new ArrayList<>();
        } else {
            this.imagesList = new ArrayList<>(list);
        }


        if (MAX_WIDTH > 0) {
            //pxMoreWandH = (MAX_WIDTH - pxImagePadding * 2) / MAX_PER_ROW_COUNT; //解决右侧图片和内容对不齐问题
            pxMoreWandH = (MAX_WIDTH - pxImagePadding * (MAX_PER_ROW_COUNT + 1)) / MAX_PER_ROW_COUNT; //解决右侧图片和内容对不齐问题
            initImageLayoutParams();
        }

        initView();
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MAX_WIDTH == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                MAX_WIDTH = width;
                setList(imagesList);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            // result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void initImageLayoutParams() {
        int wrap = LayoutParams.WRAP_CONTENT;
        int match = LayoutParams.MATCH_PARENT;
        moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
        morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
        morePara.setMargins(pxImagePadding, 0, 0, 0);

        rowPara = new LayoutParams(match, wrap);
    }

    // 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (MAX_WIDTH == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
            addView(new View(getContext()));
            return;
        }
        if (canEdit) {
            if (imagesList.size() > 0) {
                if (!imagesList.get(imagesList.size() - 1).equals("add")) {
                    imagesList.add("add");
                }
            } else {
                imagesList.add("add");
            }

        }
        if (imagesList == null || imagesList.size() == 0) {
            return;
        }

        int allCount = imagesList.size();
        MAX_PER_ROW_COUNT = 4;
        int rowCount = allCount / MAX_PER_ROW_COUNT
                + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
        for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            rowLayout.setLayoutParams(rowPara);
            if (rowCursor != 0) {
                rowLayout.setPadding(0, pxImagePadding, 0, 0);
            }

            int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
                    : allCount % MAX_PER_ROW_COUNT;//每行的列数
            if (rowCursor != rowCount - 1) {
                columnCount = MAX_PER_ROW_COUNT;
            }
            addView(rowLayout);

            int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
            for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                int position = columnCursor + rowOffset;
                rowLayout.addView(createImageView(position));
            }
        }

    }

    private View createImageView(final int position) {
        String url = imagesList.get(position);
        //if (position == imagesList.size() - 1)
        if (url.equals("add")) {
            //ImageView imageView = new ColorFilterImageView(getContext());
            ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.imageview_add_icon, this, false);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
            imageView.setId(url.hashCode());
            imageView.setOnClickListener(new ImageOnClickListener(position));
            imageView.setImageResource(R.drawable.ic_add3);
            //imageView.setBackgroundColor(Color.BLUE);
            //Glide.with(getContext()).load(R.drawable.ic_add3).into(imageView);
            //imageView.setBackgroundColor(Color.BLUE);
            return imageView;
        }
       /* ImageView imageView = new ColorFilterImageView(getContext());
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        imageView.setId(url.hashCode());
        imageView.setOnClickListener(new ImageOnClickListener(position));
        Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);*/
        View view = LayoutInflater.from(getContext()).inflate(R.layout.imageview_add, this, false);
        view.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        ImageView imageView = (ImageView) view.findViewById(R.id.imv);
        ImageView imv_delete = (ImageView) view.findViewById(R.id.imv_delete);
        imv_delete.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        view.setId(url.hashCode());
        imageView.setOnClickListener(new ImageOnClickListener(position));
        if (mOnItemClickListener != null) {
            imv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onDeleteItemClick(view, position);
                }
            });

        }
        imageView.setScaleType(ScaleType.CENTER_CROP);
        // Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.super_gray3).into(imageView);
        ImageLoader.load(getContext(), url, imageView);
        //view.setBackgroundColor(Color.RED);
        return view;
    }


    private class ImageOnClickListener implements OnClickListener {

        private int position;

        public ImageOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                if (position == imagesList.size() - 1) {
                    mOnItemClickListener.onLastItemClick(view, position);
                } else {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLastItemClick(View view, int position);

        public void onDeleteItemClick(View view, int position);
    }
}