package com.example.xu.shoppingmallnavigation.presenter.main;

import com.example.xu.shoppingmallnavigation.base.contract.main.MainContract;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;

/**
 * Created by zhaoxuzhang on 2017/11/27.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    /**
     * 线图层
     */
    protected FMLineLayer mLineLayer;
    /**
     * 起点图层
     */
    protected FMImageLayer mStartImageLayer;
    /**
     * 定位图层
     */
    protected FMLocationLayer mLocationLayer;

    public MainPresenter(MainContract.View view) {
        mView = view;
    }

    public void loadMap(FMMap fmMap, String path) {
        mView.showProgress();
        fmMap.setOnFMMapInitListener(new OnFMMapInitListener() {
            @Override
            public void onMapInitSuccess(String s) {
                mView.hideProgress();
            }

            @Override
            public void onMapInitFailure(String s, int i) {
                mView.showFailMsg(s);
            }

            @Override
            public boolean onUpgrade(FMMapUpgradeInfo fmMapUpgradeInfo) {
                return false;
            }
        });
        fmMap.openMapByPath(path);
        fmMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);

        //线图层
        mLineLayer = fmMap.getFMLayerProxy().getFMLineLayer();
        fmMap.addLayer(mLineLayer);

        //定位层
        mLocationLayer = fmMap.getFMLayerProxy().getFMLocationLayer();
        fmMap.addLayer(mLocationLayer);
    }
}
