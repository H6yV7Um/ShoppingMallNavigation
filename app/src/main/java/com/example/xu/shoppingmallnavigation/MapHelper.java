package com.example.xu.shoppingmallnavigation;

import android.content.Context;

import com.example.xu.shoppingmallnavigation.utils.FileUtils;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapInitListener;

import java.io.FileNotFoundException;

/**
 * Created by zhaoxuzhang on 2017/12/2.
 */

public class MapHelper {

    public static FMMap loadMap(FMMapView mapView, Context context) {
        //加载离线数据
        final String path = FileUtils.getDefaultMapPath(context);
        FMMap fmMap = mapView.getFMMap();
        fmMap.setOnFMMapInitListener(new OnFMMapInitListener() {
            @Override
            public void onMapInitSuccess(String s) {
                try {
                    mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserByPath(path);
                    mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserByPath(path);
                } catch (FileNotFoundException pE) {
                    pE.printStackTrace();
                } catch (FMObjectException pE) {
                    pE.printStackTrace();
                }
            }

            @Override
            public void onMapInitFailure(String s, int i) {

            }

            @Override
            public boolean onUpgrade(FMMapUpgradeInfo fmMapUpgradeInfo) {
                return false;
            }
        });
        fmMap.openMapByPath(path);
        fmMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
        return fmMap;
    }
}
