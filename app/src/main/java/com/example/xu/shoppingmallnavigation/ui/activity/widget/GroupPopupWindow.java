package com.example.xu.shoppingmallnavigation.ui.activity.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.xu.shoppingmallnavigation.R;

/**
 * Created by Xu on 2017/12/24.
 * 楼层切换菜单
 */

public class GroupPopupWindow extends PopupWindow {

    private View parentView;
    private ListView lvGroup;
    private ArrayAdapter<String> adapter;
    private ListView.OnItemClickListener listener;

    public GroupPopupWindow(Context context, ArrayAdapter<String> adapter, ListView.OnItemClickListener listener) {
        super(context);
        parentView = LayoutInflater.from(context).inflate(R.layout.group_popup_window_layout, null);
        this.adapter = adapter;
        this.listener = listener;
        initViews(parentView);
        initEvents();
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
                int height = parentView.findViewById(R.id.group_popup_layout).getTop();
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
        lvGroup = parentView.findViewById(R.id.group_lv);
    }

    private void initEvents() {
        lvGroup.setAdapter(adapter);
        lvGroup.setOnItemClickListener(listener);
    }

    public View getParentView() {
        return parentView;
    }

    public void dismissPopupWindow(Activity activity) {
        super.dismiss();
    }

    public void dismissOutSide(final Activity activity) {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    public void dismissOutSide(final Activity activity, OnDismissListener listener) {
        setOnDismissListener(listener);
    }
}
