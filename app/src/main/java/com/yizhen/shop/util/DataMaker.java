package com.yizhen.shop.util;

import com.yizhen.shop.model.MyGoods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lewis on 16/7/1.
 */
public class DataMaker {
    public static List<Object> makeObjects(int num) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Object o = new Object();
            objects.add(o);
        }
        return objects;
    }

    public static String[] strings = {"https://img.alicdn.com/bao/uploaded/i4/TB18S1iRFXXXXcSXpXXXXXXXXXX_!!0-item_pic.jpg_430x430q90.jpg",
            "https://img.alicdn.com/bao/uploaded/i4/TB15caOIFXXXXapXVXXXXXXXXXX_!!0-item_pic.jpg_430x430q90.jpg",
            "https://img.alicdn.com/bao/uploaded/i3/TB16292KVXXXXbTapXXXXXXXXXX_!!0-item_pic.jpg_430x430q90.jpg",
            "https://gd3.alicdn.com/imgextra/i3/2403502057/TB2if8.eYJkpuFjy1zcXXa5FFXa_!!2403502057.jpg_400x400.jpg",
            "https://img.alicdn.com/bao/uploaded/i2/TB1eoAiPpXXXXbBXpXXXXXXXXXX_!!0-item_pic.jpg_430x430q90.jpg",
            "https://gd3.alicdn.com/imgextra/i3/0/TB1owi2IVXXXXXDaXXXXXXXXXXX_!!0-item_pic.jpg_400x400.jpg",
            "https://gd2.alicdn.com/imgextra/i4/1772382981/TB2Py.KsXXXXXa6XXXXXXXXXXXX_!!1772382981.jpg_400x400.jpg"
    };


    public static List<MyGoods> makeGoods(int num) {
        List<MyGoods> list = Arrays.asList(new MyGoods("水晶公主黑曜石108颗手链男女款佛珠多层情侣虎眼石多圈正品手串", strings[0], "珠直径约6MM", 24.99, 6),
                new MyGoods("正月星月菩提子高密顺白干磨手链", strings[1], "108颗佛珠", 49.56, 10, "毛感料素珠"),
                new MyGoods("白玉菩提根高密天然顺白手链散珠", strings[2], "素珠佛珠108颗", 59.00, 20),
                new MyGoods("水晶黑曜石多圈多层手链男女式全黑佛珠手串", strings[3], "微胖款", 25.07, 9, "日韩学生简约潮男士"),
                new MyGoods("龙泉青瓷香炉精龙陶瓷佛具手工仿古合金香薰炉", strings[4], "哥粉铁线", 29.80, 5, "线香檀香沉香香炉"),
                new MyGoods("聚宝龙鼎方鼎宝鼎陶瓷供奉摆件", strings[5], "标准款", 69.00, 2, "白瓷香炉开天库招财旺财避邪保平安"),
                new MyGoods("仿古香炉纯铜 居室檀香盘香炉点香盘香托", strings[6], "纯铜樱花案+小水滴香插", 19.90, 300, "用途广泛，适合香道，礼佛。可搭配不同香插使用，实现多种功能！新品特价！")
        );
        List<MyGoods> array = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            MyGoods myGoods = list.get(i % 7);
            array.add(myGoods);
        }
        return array;
    }

    public static List<String> makeObjectsString(int num) {
        List<String> strs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            strs.add("第" + i + "项==");
        }
        return strs;
    }
    /*public static List<Club> makeClubs(int num) {
        List<Club> clubs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Club club = new Club();
            clubs.add(club);
        }
        return clubs;
    }

    public static List<String> makeStrings(int num) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            strings.add("item" + i);
        }
        return strings;
    }

    public static List<ActivityInfo> makeActivities(int num) {
        List<ActivityInfo> activityInfos = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ActivityInfo activityInfo = new ActivityInfo();
            activityInfos.add(activityInfo);
        }
        return activityInfos;
    }*/
}
