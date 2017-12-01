package com.example.xu.shoppingmallnavigation.presenter.main;

import com.example.xu.shoppingmallnavigation.base.contract.main.MainContract;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByKeywordRequest;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMSegment;

import java.io.FileNotFoundException;
import java.util.ArrayList;

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

    private FMSearchAnalyser mSearchAnalyser;

    private FMNaviAnalyser mNaviAnalyser;

    public MainPresenter(MainContract.View view) {
        mView = view;
    }

    public void loadMap(FMMap fmMap, final String path) {
        mView.showProgress();
        fmMap.setOnFMMapInitListener(new OnFMMapInitListener() {
            @Override
            public void onMapInitSuccess(String s) {
                mView.hideProgress();
                //得到搜索分析器
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

    //根据关键字搜索模型
    public void searchModelByKeyword(FMMap mFMMap, String keyword) {
        //groupIds为地图楼层id集合
        int[] groupIds = mFMMap.getMapGroupIds();
        //查找蛋糕关键字模型
        FMSearchModelByKeywordRequest request = new FMSearchModelByKeywordRequest(groupIds, keyword);
        //获取查询返回结果数据
        ArrayList<FMSearchResult> results = mSearchAnalyser.executeFMSearchRequest(request);
        for (FMSearchResult result : results) {
            String fid = (String) result.get("FID");
            FMModel model = mFMMap.getFMLayerProxy().queryFMModelByFid(fid);
        }
    }

    /**
     * 根据起始点坐标和起始点楼层id，路径分析，并根据结果绘制路径线。
     *
     * @param stGroupId  起点层id
     * @param stCoord    起点坐标
     * @param endGroupId 终点层id
     * @param endCoord   终点坐标
     */
    void analyzeNavigation(int stGroupId, FMMapCoord stCoord, int endGroupId, FMMapCoord endCoord) {
        int type = mNaviAnalyser.analyzeNavi(stGroupId, stCoord, endGroupId, endCoord,
                FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
            // 构建线数据
            ArrayList segments = new ArrayList<>();
            for (FMNaviResult r : results) {
                int groupId = r.getGroupId();
                FMSegment s = new FMSegment(groupId, r.getPointList());
                segments.add(s);
            }
            //添加LineMarker
            FMLineMarker lineMarker = new FMLineMarker(segments);
            mLineLayer.addMarker(lineMarker);
        }
    }

}
