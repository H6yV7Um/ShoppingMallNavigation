package com.example.xu.shoppingmallnavigation.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.xu.shoppingmallnavigation.utils.FileUtils;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.event.OnFMMapInitListener;

/**
 * Created by Xu on 2017/12/5.
 */

public abstract class BaseMapActivity extends AppCompatActivity implements OnFMMapInitListener, BaseView {

    protected FMMapView mMapView;
    protected FMMap mFMMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getLayoutId());
        initViews();
        openMapByPath();
    }

    /**
     * 加载地图数据
     */
    private void openMapByPath() {
        mMapView = findViewById(getMapViewId());
        mFMMap = mMapView.getFMMap();
        mFMMap.setOnFMMapInitListener(this);
        //加载离线数据
        String path = FileUtils.getDefaultMapPath(this);
        showProgress();
        mFMMap.openMapByPath(path);
    }

    @Override
    public void onMapInitSuccess(String path) {
        //加载离线主题文件
        mFMMap.loadThemeByPath(FileUtils.getDefaultThemePath(this));
        hideProgress();
    }

    @Override
    public void onMapInitFailure(String path, int errorCode) {
        //TODO 可以提示用户地图加载失败原因，进行地图加载失败处理
        showFailMsg(path);
    }

    @Override
    public boolean onUpgrade(FMMapUpgradeInfo upgradeInfo) {
        //TODO 获取到最新地图更新的信息，可以进行地图的下载操作
        return false;
    }

    /**
     * 地图销毁调用
     */
    @Override
    public void onBackPressed() {
        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        super.onBackPressed();
    }

    public abstract int getLayoutId();

    public abstract int getMapViewId();

    public abstract void initViews();

    public abstract void showProgress();

    public abstract void hideProgress();

    public abstract void showFailMsg(String msg);

}
