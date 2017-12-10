package com.example.xu.shoppingmallnavigation.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.xu.shoppingmallnavigation.utils.FileUtils;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMModel;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Created by Xu on 2017/12/5.
 */

public abstract class BaseMapActivity extends AppCompatActivity implements OnFMMapInitListener, BaseView {

    protected FMMapView mMapView;
    protected FMMap mFMMap;
    protected FMSearchAnalyser mSearchAnalyser;
    protected FMModelLayer mModelLayer;
    protected FMFacilityLayer mFacilityLayer;
    protected FMModel mClickedModel;
    protected FMFacility mClickedFacility;
    protected FMModel mLastClicked;
    protected HashMap<Integer, FMImageLayer> mImageLayers = new HashMap<>();

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

        //搜索分析
        try {
            mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserById(FileUtils.DEFAULT_MAP_ID);
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
