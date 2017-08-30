package com.yizhen.shop.util;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yizhen.shop.R;


/**
 * Created by lewis on 2017/6/3.
 */

public class SearchBarUtils {
    public static EditText init(Activity context) {
        return init(context, null);
    }

    public static EditText init(final Activity context, final OnSearchListener listener) {
        final EditText query = (EditText) context.findViewById(R.id.query);
        final ImageButton search_clear = (ImageButton) context.findViewById(R.id.search_clear);
        if (query != null && search_clear != null) {
            query.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        search_clear.setVisibility(View.VISIBLE);
                    } else {
                        search_clear.setVisibility(View.INVISIBLE);
                        if (listener != null) {
                            listener.cleanSearch();
                        }
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                }
            });
            search_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    query.getText().clear();
                }
            });
            query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        CommonUtils.hideSoftInput(context, query);
                        if (listener != null) {
                            listener.onSearch(query.getText().toString());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
        return query;
    }

    public interface OnSearchListener {
        void onSearch(String string);

        void cleanSearch();
    }
}
