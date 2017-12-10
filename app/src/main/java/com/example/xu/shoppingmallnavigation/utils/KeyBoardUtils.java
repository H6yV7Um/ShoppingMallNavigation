package com.example.xu.shoppingmallnavigation.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Xu on 2017/12/10.
 */

public class KeyBoardUtils {

    /**
     * 关闭软键盘
     *
     * @param editText 输入框
     * @param context  上下文
     */
    public static void closeKeybord(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
