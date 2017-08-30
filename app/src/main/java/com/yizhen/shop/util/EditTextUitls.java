package com.yizhen.shop.util;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by lewis on 2016/9/27.
 */

public class EditTextUitls {
    public static void bindLookPasswordToImageView(final EditText edt, final ImageView imv, final int lookId, final int nolookId) {
        edt.setInputType(0x81);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (edt.getInputType() == 0x81) {
                        edt.setInputType(0x90);
                        imv.setImageResource(nolookId);
                    } else {
                        edt.setInputType(0x81);
                        imv.setImageResource(lookId);
                    }
                    // 切换后将EditText光标置于末尾
                    CharSequence charSequence = edt.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText,
                                charSequence.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static void bindCleanToView(final EditText edt, final View v) {
        edt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt.getText().clear();
            }
        });
    }
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }
}
