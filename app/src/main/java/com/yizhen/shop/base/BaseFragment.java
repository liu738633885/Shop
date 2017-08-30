package com.yizhen.shop.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static android.R.attr.name;

/**
 * Created by lewis on 16/6/15.
 */
public abstract class BaseFragment extends Fragment {
    protected int num;
    //获取fragment布局文件ID
    protected abstract int getLayoutId();

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected View parentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    //Fragment的View加载完毕的标记
    private boolean isViewCreated;

    //Fragment对用户可见的标记
    private boolean isUIVisible;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }
    }
    private void lazyLoad() {

        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
            loadData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;

            //printLog(mTextviewContent+"可见,加载数据");
        }
    }
    protected  void loadData(){
        //Logger.e("loadData"+getNum());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Logger.e("onCreateView"+getNum());
       /* if (parentView == null) {
            parentView = inflater.inflate(getLayoutId(), container, false);
        }*/
        parentView = inflater.inflate(getLayoutId(), container,false);
        initView(parentView, savedInstanceState);

        //防止嵌套在 viewpager 崩溃
       /* ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }*/
        return parentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Boolean b) {
    }

    public void baseRefresh() {
    }
    public int getNum(){
        return num;
    }

}
