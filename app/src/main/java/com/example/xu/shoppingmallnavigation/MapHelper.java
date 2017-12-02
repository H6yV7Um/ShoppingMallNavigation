package com.example.xu.shoppingmallnavigation;

import android.content.Context;

import com.example.xu.shoppingmallnavigation.base.BaseView;
import com.example.xu.shoppingmallnavigation.utils.FileUtils;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByKeywordRequest;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by zhaoxuzhang on 2017/12/2.
 */

public class MapHelper {

    private FMMap fmMap;

    private FMSearchAnalyser mSearchAnalyser;

    private FMNaviAnalyser mNaviAnalyser;

    private FMModelLayer mModelLayer;

    /**
     * 定位图层
     */
    protected FMLocationLayer mLocationLayer;

    /**
     * 线图层
     */
    protected FMLineLayer mLineLayer;

    public FMMap loadMap(final BaseView baseView, FMMapView mapView, Context context) {
        //加载离线数据
        baseView.showProgress();
        final String path = FileUtils.getDefaultMapPath(context);
        fmMap = mapView.getFMMap();
        fmMap.setOnFMMapInitListener(new OnFMMapInitListener() {
            @Override
            public void onMapInitSuccess(String s) {
                try {
                    mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserByPath(path);
                    mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserByPath(path);
                    baseView.showFailMsg("initSuccess");
                } catch (FileNotFoundException pE) {
                    pE.printStackTrace();
                    baseView.showFailMsg(pE.getMessage());
                } catch (FMObjectException pE) {
                    pE.printStackTrace();
                    baseView.showFailMsg(pE.getMessage());
                }
            }

            @Override
            public void onMapInitFailure(String s, int i) {
                baseView.hideProgress();
                baseView.showFailMsg(s);
            }

            @Override
            public boolean onUpgrade(FMMapUpgradeInfo fmMapUpgradeInfo) {
                return false;
            }
        });
        fmMap.openMapByPath(path);
        fmMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);

        //添加线图层
        mLineLayer = fmMap.getFMLayerProxy().getFMLineLayer();
        fmMap.addLayer(mLineLayer);

        //定位层
        mLocationLayer = fmMap.getFMLayerProxy().getFMLocationLayer();
        fmMap.addLayer(mLocationLayer);

        int groupId = fmMap.getFocusGroupId();

        //模型图层
        mModelLayer = fmMap.getFMLayerProxy().getFMModelLayer(groupId);
        baseView.hideProgress();
        return fmMap;
    }

    public void searchModelByKeyword(String keyword) {
        //groupIds为地图楼层id集合
        int[] groupIds = fmMap.getMapGroupIds();
        //查找蛋糕关键字模型
        FMSearchModelByKeywordRequest request = new FMSearchModelByKeywordRequest(groupIds, keyword);
        //获取查询返回结果数据
        ArrayList<FMSearchResult> results = mSearchAnalyser.executeFMSearchRequest(request);
        for (FMSearchResult result : results) {
            String fid = (String) result.get("FID");
            FMModel model = fmMap.getFMLayerProxy().queryFMModelByFid(fid);
        }
    }

    public void setOnMapClickListener(OnFMNodeListener mOnModelCLickListener) {
        mModelLayer.setOnFMNodeListener(mOnModelCLickListener);
    }


}
