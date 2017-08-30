package com.yizhen.shop.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yizhen.shop.R;
import com.yizhen.shop.model.address.City;
import com.yizhen.shop.model.address.County;
import com.yizhen.shop.model.address.Province;
import com.yizhen.shop.util.DensityUtil;
import com.yizhen.shop.util.T;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/8/2.
 */

public class AddressDialog extends Dialog {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    adapter1.setNewData(provinces);
                    ok.setText("确定");
                    break;
            }
        }
    };
    private Context context;
    private TabLayout tabLayout;
    private RecyclerView rv1, rv2, rv3;
    private BaseQuickAdapter<Province, BaseViewHolder> adapter1;
    private BaseQuickAdapter<City, BaseViewHolder> adapter2;
    private BaseQuickAdapter<County, BaseViewHolder> adapter3;
    TabLayout.Tab tab1;
    TabLayout.Tab tab2;
    TabLayout.Tab tab3;
    List<Province> provinces;
    Province province;
    City city;
    County county;
    TextView ok;

    public AddressDialog(@NonNull Context context) {
        super(context, R.style.MyDialogStyleBottom);
        this.context = context;
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = DensityUtil.getHeight(context) / 2;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        // lp.width = DensityUtil.getWidth(context);
        dialogWindow.setAttributes(lp);
        setContentView(R.layout.dialog_choose_address);
        if (findViewById(R.id.cancel) != null) {
            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        initData();
    }

    private void initData() {

        rv1 = (RecyclerView) findViewById(R.id.rv1);
        rv1.setLayoutManager(new LinearLayoutManager(context));
        rv2 = (RecyclerView) findViewById(R.id.rv2);
        rv2.setLayoutManager(new LinearLayoutManager(context));
        rv3 = (RecyclerView) findViewById(R.id.rv3);
        rv3.setLayoutManager(new LinearLayoutManager(context));
        initAdapter();
        rv1.setAdapter(adapter1);
        rv2.setAdapter(adapter2);
        rv3.setAdapter(adapter3);
        showRv(1);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showRv(1);
                } else if (tab.getPosition() == 1) {
                    showRv(2);
                } else {
                    showRv(3);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tab1 = tabLayout.newTab();
        tab2 = tabLayout.newTab();
        tab3 = tabLayout.newTab();
        tabLayout.addTab(tab1, true);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);
        updateTabUi();
        ok = findViewById(R.id.ok);
        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (province == null) {
                            T.showShort(context, "请选择省份");
                            return;
                        }
                        if (city == null) {
                            T.showShort(context, "请选择城市");
                            return;
                        }
                        if (city.counties != null && city.counties.size() > 0 && county == null) {
                            T.showShort(context, "请选择区县");
                            return;
                        }
                        if (okListener != null) {
                            okListener.ok(province, city, county);
                        }
                        dismiss();
                    }
                }
        );
        loadData();
    }

    private void loadData() {
        ok.setText("加载中...");
        new Thread() {
            public void run() {
                try {
                    String text = getJson(context, "area.json");
                    provinces = JSON.parseArray(text, Province.class);
                    //Logger.e(provinces.get(10).cities.get(0).counties.get(0).county + "");
                    //TODO 这里添加一个网络访问操作
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    //TODO 这里需要关闭一下进度条
                    e.printStackTrace();
                }
            }
        }.start();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRv(int num) {
        rv1.setVisibility(num == 1 ? View.VISIBLE : View.GONE);
        rv2.setVisibility(num == 2 ? View.VISIBLE : View.GONE);
        rv3.setVisibility(num == 3 ? View.VISIBLE : View.GONE);
    }

    private void updateTabUi() {
        tab1.setText(province == null ? "省" : province.province);
        tab2.setText(city == null ? "市" : city.city);
        tab3.setText(county == null ? "区县" : county.county);
    }


    private void initAdapter() {
        adapter1 = new BaseQuickAdapter<Province, BaseViewHolder>(android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(final BaseViewHolder helper, final Province item) {
                ((TextView) helper.itemView).setText(item.province);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        province = item;
                        city = null;
                        county = null;
                        adapter2.setNewData(province.cities);
                        adapter3.setNewData(new ArrayList<County>());
                        tab2.select();
                        updateTabUi();
                    }
                });
            }
        };
        adapter2 = new BaseQuickAdapter<City, BaseViewHolder>(android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(final BaseViewHolder helper, final City item) {
                ((TextView) helper.itemView).setText(item.city);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        city = item;
                        county = null;
                        adapter3.setNewData(city.counties);
                        tab3.select();
                        updateTabUi();
                    }
                });
            }
        };
        adapter3 = new BaseQuickAdapter<County, BaseViewHolder>(android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(final BaseViewHolder helper, final County item) {
                ((TextView) helper.itemView).setText(item.county);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        county = item;
                        updateTabUi();
                        T.showShort(context, province.province + " " + city.city + " " + county.county);
                    }
                });
            }
        };
    }

    public String getJson(Context mContext, String fileName) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }

    @Override
    public void show() {
        if (this == null || isShowing()) {
            return;
        }
        super.show();

    }

    @Override
    public void dismiss() {
        if (this == null || !isShowing()) {
            return;
        }
        super.dismiss();
    }

    private OnOkListener okListener;

    public void setOnOkListener(OnOkListener listener) {
        okListener = listener;
    }

    public interface OnOkListener {
        public void ok(Province province, City city, County county);
    }
}
