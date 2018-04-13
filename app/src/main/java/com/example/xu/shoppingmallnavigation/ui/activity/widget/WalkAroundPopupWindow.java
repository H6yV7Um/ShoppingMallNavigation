package com.example.xu.shoppingmallnavigation.ui.activity.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.xu.shoppingmallnavigation.R;

/**
 * Created by Xu on 2018/4/13.
 *
 * @author Xu
 */
public class WalkAroundPopupWindow extends PopupWindow {

    private View parentView;
    private ListView listView;
    private ArrayAdapter adapter;

    public WalkAroundPopupWindow(Context context, ArrayAdapter adapter) {
        super(context);
        parentView = LayoutInflater.from(context).inflate(R.layout.walk_around_popup_window_layout, null);
        this.adapter = adapter;
        initViews(parentView);
        this.setContentView(parentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = parentView.findViewById(R.id.navi_end_popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void initViews(View parentView) {
        listView = parentView.findViewById(R.id.walk_around_lv);
        listView.setAdapter(adapter);
    }

    public void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = (activity).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    public void dismissPopupWindow(Activity activity) {
        super.dismiss();
    }

    public void dismissOutSide(final Activity activity) {
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    public void dismissOutSide(final Activity activity, OnDismissListener listener) {
        setOnDismissListener(listener);
    }
}

