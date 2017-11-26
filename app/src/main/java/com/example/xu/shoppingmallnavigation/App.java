package com.example.xu.shoppingmallnavigation;

import android.app.Application;

import com.fengmap.android.FMMapSDK;

/**
 * Created by Xu on 2017/11/26.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化FMMap
        FMMapSDK.init(this);
    }
}
