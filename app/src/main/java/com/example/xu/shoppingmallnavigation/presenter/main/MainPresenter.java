package com.example.xu.shoppingmallnavigation.presenter.main;

import android.content.Context;

import com.example.xu.shoppingmallnavigation.MapHelper;
import com.example.xu.shoppingmallnavigation.base.BaseView;
import com.example.xu.shoppingmallnavigation.base.contract.main.MainContract;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.event.OnFMMapClickListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.layer.FMImageLayer;

/**
 * Created by zhaoxuzhang on 2017/11/27.
 */

public class MainPresenter implements MainContract.Presenter {

    private FMMap fmMap;

    private MainContract.View mView;

    /**
     * 起点图层
     */
    protected FMImageLayer mStartImageLayer;


    private MapHelper mapHelper;


    public MainPresenter(MainContract.View view) {
        mView = view;
        mapHelper = new MapHelper();
    }

    public FMMap loadMap(final BaseView baseView, FMMapView mapView, Context context) {
        return mapHelper.loadMap(baseView, mapView, context);
//        fmMap = mapView.getFMMap();
//        mView.showProgress();
//        fmMap.setOnFMMapInitListener(new OnFMMapInitListener() {
//            @Override
//            public void onMapInitSuccess(String s) {
//                mView.hideProgress();
//                //得到搜索分析器
//                try {
//                    mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserByPath(path);
//                    mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserByPath(path);
//                } catch (FileNotFoundException pE) {
//                    pE.printStackTrace();
//                } catch (FMObjectException pE) {
//                    pE.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onMapInitFailure(String s, int i) {
//                mView.showFailMsg(s);
//            }
//
//            @Override
//            public boolean onUpgrade(FMMapUpgradeInfo fmMapUpgradeInfo) {
//                return false;
//            }
//        });
//        fmMap.openMapByPath(path);
//        fmMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
    }

    //根据关键字搜索模型
    public void searchModelByKeyword(String keyword) {
        mapHelper.searchModelByKeyword(keyword);
    }

    public void setOnFMNodeListener(OnFMNodeListener mOnFMNodeListener) {
        mapHelper.setOnFMNodeListener(mOnFMNodeListener);
    }

    public void setOnMapClickListener(OnFMMapClickListener mOnFMMapClickListener) {
//        mapHelper.setOnMapClickListener(mOnFMMapClickListener);
    }

    /**
     * 根据起始点坐标和起始点楼层id，路径分析，并根据结果绘制路径线。
     *
     * @param stGroupId  起点层id
     * @param stCoord    起点坐标
     * @param endGroupId 终点层id
     * @param endCoord   终点坐标
     */
//    public void analyzeNavigation(int stGroupId, FMMapCoord stCoord, int endGroupId, FMMapCoord endCoord) {
//        int type = mNaviAnalyser.analyzeNavi(stGroupId, stCoord, endGroupId, endCoord,
//                FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
//        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
//            ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
//            // 构建线数据
//            ArrayList segments = new ArrayList<>();
//            for (FMNaviResult r : results) {
//                int groupId = r.getGroupId();
//                FMSegment s = new FMSegment(groupId, r.getPointList());
//                segments.add(s);
//            }
//            //添加LineMarker
//            FMLineMarker lineMarker = new FMLineMarker(segments);
//            mLineLayer.addMarker(lineMarker);
//        }
//    }

}
