package com.example.xu.shoppingmallnavigation.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.xu.shoppingmallnavigation.R;
import com.example.xu.shoppingmallnavigation.bean.MapCoord;
import com.example.xu.shoppingmallnavigation.helper.ViewHelper;
import com.example.xu.shoppingmallnavigation.utils.FileUtils;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMSegment;
import com.fengmap.android.utils.FMMath;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Xu on 2017/12/5.
 */

public abstract class BaseMapActivity extends AppCompatActivity implements OnFMMapInitListener, BaseView {

    protected FMMapView mMapView;
    protected FMMap mFMMap;
    protected FMSearchAnalyser mSearchAnalyser;
    protected FMNaviAnalyser mNaviAnalyser;
    protected FMModelLayer mModelLayer;
    protected FMLineLayer mLineLayer;
    protected FMFacilityLayer mFacilityLayer;
    protected FMModel mClickedModel;
    protected FMFacility mClickedFacility;
    protected FMLocationLayer mLocationLayer;
    protected FMModel mLastClicked;
    protected MapCoord stCoord;
    protected HashMap<Integer, FMImageLayer> mImageLayers = new HashMap<>();
    /**
     * 起点图层
     */
    protected FMImageLayer mStartImageLayer;
    /**
     * 终点图层
     */
    protected FMImageLayer mEndImageLayer;
    /**
     * 导航行走的楼层集合
     */
    protected ArrayList<Integer> mNaviGroupIds;
    /**
     * 导航行走点集合
     */
    protected ArrayList<ArrayList<FMMapCoord>> mNaviPoints;
    /**
     * 导航行走索引
     */
    protected int mCurrentIndex = 0;
    /**
     * 两个点相差最大距离20米
     */
    protected static final double MAX_BETWEEN_LENGTH = 20;
    /**
     * 定位切换楼层
     */
    protected static final int WHAT_LOCATE_SWITCH_GROUP = 1;
    /**
     * 处理UI消息
     */
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LOCATE_SWITCH_GROUP:
                    updateLocateGroupView();
                    break;
            }
        }
    };

    private static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getLayoutId());
        initViews();
        requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                openMapByPath();
            }
        } else {
            openMapByPath();
        }
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

        int groupId = mFMMap.getFocusGroupId();
        //公共设施图层
        mFacilityLayer = mFMMap.getFMLayerProxy().getFMFacilityLayer(groupId);
        mFMMap.addLayer(mFacilityLayer);

        //图片图层
        int groupSize = mFMMap.getFMMapInfo().getGroupSize();
        for (int i = 0; i < groupSize; i++) {
            int tempId = mFMMap.getMapGroupIds()[i];
            FMImageLayer imageLayer = mFMMap.getFMLayerProxy().createFMImageLayer(tempId);
            mFMMap.addLayer(imageLayer);

            mImageLayers.put(groupId, imageLayer);
        }

        //模型图层
        mModelLayer = mFMMap.getFMLayerProxy().getFMModelLayer(groupId);
        mFMMap.addLayer(mModelLayer);

        //添加线图层
        mLineLayer = mFMMap.getFMLayerProxy().getFMLineLayer();
        mFMMap.addLayer(mLineLayer);

        //创建定位图层
        mLocationLayer = mFMMap.getFMLayerProxy().getFMLocationLayer();
        mFMMap.addLayer(mLocationLayer);

        stCoord = new MapCoord(1, new FMMapCoord(12961647.576796599, 4861814.63807118));
        //真实定位返回的地图坐标mapCoord和角度angle
        FMLocationMarker locationMarker = new FMLocationMarker(groupId, stCoord.getMapCoord());
        //设置定位点图片
        locationMarker.setActiveImageFromAssets("active.png");
        //设置定位图片宽高
        locationMarker.setMarkerWidth(30);
        locationMarker.setMarkerHeight(30);
        locationMarker.setAngle(0);
        mLocationLayer.addMarker(locationMarker);


        //搜索分析
        try {
            mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserById(FileUtils.DEFAULT_MAP_ID);
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserByPath(path);
        } catch (FMObjectException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapInitFailure(String path, int errorCode) {
        //TODO 可以提示用户地图加载失败原因，进行地图加载失败处理
        showFailMsg(path + FMErrorMsg.getErrorMsg(errorCode));
    }

    @Override
    public boolean onUpgrade(FMMapUpgradeInfo upgradeInfo) {
        //TODO 获取到最新地图更新的信息，可以进行地图的下载操作
        return false;
    }

    /**
     * 清除图片标志
     */
    protected void clearImageMarker() {
        for (FMImageLayer imageLayer : mImageLayers.values()) {
            imageLayer.removeAll();
        }
    }

    /**
     * 导航分析
     */
    protected void analyzeNavigation(MapCoord stPoint, MapCoord endPoint) {
        clearImageLayer();
        // 添加起点图层
        mStartImageLayer = new FMImageLayer(mFMMap, stPoint.getGroupId());
        mFMMap.addLayer(mStartImageLayer);
        // 标注物样式
        FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(), stPoint.getMapCoord(), R.mipmap.start);
        mStartImageLayer.addMarker(imageMarker);

        // 添加终点图层
        mEndImageLayer = new FMImageLayer(mFMMap, endPoint.getGroupId());
        mFMMap.addLayer(mEndImageLayer);
        // 标注物样式
        imageMarker = ViewHelper.buildImageMarker(getResources(), endPoint.getMapCoord(), R.mipmap.end);
        mEndImageLayer.addMarker(imageMarker);

        analyzeNavigation(stPoint.getGroupId(), stPoint.getMapCoord(), endPoint.getGroupId(), endPoint.getMapCoord());
    }

    /**
     * 根据起始点坐标和起始点楼层id，路径分析，并根据结果绘制路径线。
     *
     * @param startGroupId  起点层id
     * @param startPt    起点坐标
     * @param endGroupId 终点层id
     * @param endPt   终点坐标
     */
    protected void analyzeNavigation(int startGroupId, FMMapCoord startPt, int endGroupId, FMMapCoord endPt) {
        int type = mNaviAnalyser.analyzeNavi(startGroupId, startPt, endGroupId, endPt, FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            fillWithPoints();
            addLineMarker();
        }
    }

    /**
     * 填充导航线段点
     */
    protected void fillWithPoints() {
        clearWalkPoints();
        mNaviGroupIds = new ArrayList<>();
        mNaviPoints = new ArrayList<>();
        //获取路径规划上点集合数据
        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        int focusGroupId = Integer.MIN_VALUE;
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
            ArrayList<FMMapCoord> points = r.getPointList();
            //点数据小于2，则为单个数据集合
            if (points.size() < 2) {
                continue;
            }
            //判断是否为同层导航数据，非同层数据即其他层数据
            if (focusGroupId == Integer.MIN_VALUE || focusGroupId != groupId) {
                focusGroupId = groupId;
                //添加即将行走的楼层与点集合
                mNaviGroupIds.add(groupId);
                mNaviPoints.add(points);
            } else {
                mNaviPoints.get(mNaviPoints.size() - 1).addAll(points);
            }
        }
    }

    /**
     * 清空行走的点集合数据
     */
    private void clearWalkPoints() {
        mCurrentIndex = 0;
        mNaviPoints.clear();
        mNaviGroupIds.clear();
    }

    /**
     * 添加线标注
     */
    protected void addLineMarker() {
        clearLineLayer();

        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        // 填充导航线数据
        ArrayList<FMSegment> segments = new ArrayList<>();
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
            FMSegment s = new FMSegment(groupId, r.getPointList());
            segments.add(s);
        }

        //添加LineMarker
        FMLineMarker lineMarker = ViewHelper.buildLineMarker(segments);
        mLineLayer.addMarker(lineMarker);

    }

    /**
     * 清除线图层
     */
    public void clearLineLayer() {
        if (mLineLayer != null) {
            mLineLayer.removeAll();
        }
    }

    /**
     * 清除图片标注
     */
    protected void clearImageLayer() {
        //清理起点图层
        if (mStartImageLayer != null) {
            mStartImageLayer.removeAll();
            mStartImageLayer = null;
        }

        //清理终点图层
        if (mEndImageLayer != null) {
            mEndImageLayer.removeAll();
            mEndImageLayer = null;
        }
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

    /**
     * 移动至中心点,如果中心与屏幕中心点距离大于20米，将移动
     *
     * @param mapCoord 坐标
     */
    protected void moveToCenter(final FMMapCoord mapCoord) {
        FMMapCoord centerCoord = mFMMap.getMapCenter();
        double length = FMMath.length(centerCoord, mapCoord);
        if (length > MAX_BETWEEN_LENGTH) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mFMMap.moveToCenter(mapCoord, true);
                }
            });
        }
    }


    public abstract int getLayoutId();

    public abstract int getMapViewId();

    public abstract void initViews();

    public abstract void showProgress();

    public abstract void hideProgress();

    public abstract void showFailMsg(String msg);

    /**
     * 切换楼层显示
     */
    public abstract void updateLocateGroupView();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMapByPath();
            } else {
                // Permission Denied
                Toast.makeText(BaseMapActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
