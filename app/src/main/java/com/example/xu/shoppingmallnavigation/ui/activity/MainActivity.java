package com.example.xu.shoppingmallnavigation.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xu.shoppingmallnavigation.R;
import com.example.xu.shoppingmallnavigation.base.BaseMapActivity;
import com.example.xu.shoppingmallnavigation.bean.MapCoord;
import com.example.xu.shoppingmallnavigation.helper.ViewHelper;
import com.example.xu.shoppingmallnavigation.ui.activity.adapter.MapSearchAdapter;
import com.example.xu.shoppingmallnavigation.ui.activity.widget.MapPopupWindow;
import com.example.xu.shoppingmallnavigation.ui.activity.widget.NaviEndPopupWindow;
import com.example.xu.shoppingmallnavigation.utils.ConvertUtils;
import com.example.xu.shoppingmallnavigation.utils.KeyBoardUtils;
import com.example.xu.shoppingmallnavigation.utils.MapSearchUtils;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Xu on 2017/11/26.
 */

public class MainActivity extends BaseMapActivity {

    protected double curAngle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.mapview_error)
    TextView tvError;
    @BindView(R.id.walk_around_fab)
    FloatingActionButton fabWalkAround;
    private SearchView mSearchView;
    private MapSearchAdapter mapSearchAdapter;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private MapPopupWindow popupWindow;
    private NaviEndPopupWindow naviEndPopupWindow;
    private WalkAroundPopupWindow walkAroundPopupWindow;
    /**
     * 约束过定位标注
     */
    private FMLocationMarker mHandledMarker;
    /**
     * 记录上一次行走坐标
     */
    private FMMapCoord mLastMoveCoord;
    protected double curAngle;
    protected int curNaviRoutesIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // 设置ActionBar
        setSupportActionBar(toolbar);
    }

    @Override
    public void onMapInitSuccess(String path) {
        super.onMapInitSuccess(path);
        for (FMModelLayer mModelLayer : mModelLayers) {
            mModelLayer.setOnFMNodeListener(mOnFMNodeListener);
        }
        for (FMFacilityLayer mFacilityLayer : mFacilityLayers) {
            mFacilityLayer.setOnFMNodeListener(mOnFacilityClickListener);
        }
        curGroupId = 1;
        hideProgress();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getMapViewId() {
        return R.id.mapview;
    }

    @Override
    public void showProgress() {
//        Toast.makeText(MainActivity.this, "loading...", Toast.LENGTH_LONG).show();
        Log.i("shopping", "loading...");
    }

    @Override
    public void hideProgress() {
//        Toast.makeText(MainActivity.this, "complete...", Toast.LENGTH_LONG).show();
//        presenter.setOnFMNodeListener(mOnFMNodeListener);
        Log.i("shopping", "complete...");
    }

    @Override
    public void showFailMsg(String msg) {
//        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        Log.i("shopping", msg);
        tvError.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.GONE);
    }

    public void navigationEnd(double angle) {
        curAngle = angle;
        // 可记录！！
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createNaviEndPopupWindow();
            }
        });
    }

    private void createNaviEndPopupWindow() {
        if (naviEndPopupWindow != null) {
            naviEndPopupWindow.dismiss();
            naviEndPopupWindow = null;
        }
        naviEndPopupWindow = new NaviEndPopupWindow(MainActivity.this, listener);
        naviEndPopupWindow.showAtLocation(findViewById(R.id.map_main_ll),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        naviEndPopupWindow.dismissOutSide(MainActivity.this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                naviEndPopupWindow.dismiss();
                naviEndPopupWindow = null;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //设置是否显示搜索框展开时的提交按钮
        mSearchView.setSubmitButtonEnabled(true);
        mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        mSearchAutoComplete.setThreshold(1);
        mSearchAutoComplete.setOnItemClickListener(searchItemClickListener);
        // 监听器
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                KeyBoardUtils.closeKeybord(mSearchAutoComplete, MainActivity.this);
//                if (!s.equals("")) {
//                    ArrayList<FMModel> models = queryModelByKeyword(s);
//                    if (mapSearchAdapter == null) {
//                        mapSearchAdapter = new MapSearchAdapter(MainActivity.this, models);
//                        mSearchAutoComplete.setAdapter(mapSearchAdapter);
//                    } else {
//                        mapSearchAdapter.setDatas(models);
//                        mapSearchAdapter.notifyDataSetChanged();
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "请输入搜索词！", Toast.LENGTH_LONG).show();
//                }
//                mapSearchAdapter = null;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.i("shopping-change", s);
                if (!s.equals("")) {
                    ArrayList<FMModel> models = queryModelByKeyword(s);
                    if (mapSearchAdapter == null) {
                        mapSearchAdapter = new MapSearchAdapter(MainActivity.this, models);
                        mSearchAutoComplete.setAdapter(mapSearchAdapter);
                    } else {
                        mapSearchAdapter.setDatas(models);
                        mapSearchAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });

        return true;
    }

    /**
     * 通过关键字查询模型
     *
     * @param keyword 关键字
     */
    private ArrayList<FMModel> queryModelByKeyword(String keyword) {
        //查询楼层集合
        int[] groupIds = {mFMMap.getFocusGroupId()};
        //搜索请求
        return MapSearchUtils.queryModelByKeyword(mFMMap, groupIds, mSearchAnalyser, keyword);
    }

    private void createPopupWindow(String name, MapCoord curStartCoord, MapCoord curEndCoord, final boolean isFacility) {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        stCoord = curStartCoord;
        endCoord = curEndCoord;
        int type = mNaviAnalyser.analyzeNavi(stCoord.getGroupId(), stCoord.getMapCoord(), endCoord.getGroupId(),
                endCoord.getMapCoord(), FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
        double distance = 0;
        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            distance = mNaviAnalyser.getSceneRouteLength();
        }
//        double distance = ConvertUtils.getDistance(stCoord, endCoord);
        DecimalFormat df = new DecimalFormat("#.0");
        popupWindow = new MapPopupWindow(MainActivity.this, listener, name, df.format(distance));
//        popupWindow = new MapPopupWindow(MainActivity.this, listener, name, String.valueOf(distance));
        popupWindow.showAtLocation(findViewById(R.id.map_main_ll),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.dismissOutSide(MainActivity.this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (isFacility) {
                    clearFacilityAll(mClickedFacility);
                } else {
                    clearModelAll(mClickedModel);
                }
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    private void clearFacilityAll(FMFacility facility) {
        if (!facility.equals(mClickedFacility)) {
            if (mClickedFacility != null) {
                mClickedFacility.setSelected(false);
            }
            this.mClickedFacility = facility;
            this.mClickedFacility.setSelected(false);
        }
        mFMMap.updateMap();
    }

    /**
     * 清除模型的聚焦效果
     *
     * @param model 模型
     */
//    private void clearFocus(FMModel model) {
//        if (!model.equals(mLastClicked)) {
//            if (mLastClicked != null) {
//                mLastClicked.setSelected(false);
//            }
//            this.mLastClicked = model;
//            this.mLastClicked.setSelected(true);
//        }
//        mFMMap.updateMap();
//    }

    private void clearModelAll(FMModel model) {
        if (!model.equals(mClickedFacility)) {
            if (mLastClicked != null) {
                mLastClicked.setSelected(false);
            }
            this.mLastClicked = model;
            this.mLastClicked.setSelected(false);
        }
        mFMMap.updateMap();
    }

    @Override
    public void onAnimationStart() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        if (naviEndPopupWindow != null) {
            naviEndPopupWindow.dismiss();
            naviEndPopupWindow = null;
        }
    }

    @Override
    public void onAnimationUpdate(final int mIndex, FMMapCoord mapCoord, double distance, double angle) {
        updateHandledMarker(mapCoord, angle);
        if (curAngle == 0 || curAngle != angle && curNaviRoutesIndex < mNaviDescriptionResults.size()) {
            curAngle = angle;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, mNaviDescriptionResults.get(curNaviRoutesIndex), Toast.LENGTH_SHORT).show();
                    curNaviRoutesIndex++;
                }
            });
        }
    }

    /**
     * 更新处理过定位点
     *
     * @param coord 坐标
     * @param angle 角度
     */
    private void updateHandledMarker(FMMapCoord coord, double angle) {
        if (mHandledMarker == null) {
            mHandledMarker = ViewHelper.buildLocationMarker(mFMMap.getFocusGroupId(),
                    coord);
            mLocationLayer.addMarker(mHandledMarker);
        } else {
            FMMapCoord mapCoord = makeConstraint(coord);
            mHandledMarker.updateAngleAndPosition((float) angle, mapCoord);

            if (angle != 0) {
                animateRotate((float) -angle);
            }
        }

        //上次真实行走坐标
        mLastMoveCoord = coord.clone();
        moveToCenter(mLastMoveCoord);
    }

    /**
     * 路径约束
     *
     * @param mapCoord 地图坐标点
     * @return
     */
    private FMMapCoord makeConstraint(FMMapCoord mapCoord) {
        FMMapCoord currentCoord = mapCoord.clone();
        int groupId = mFMMap.getFocusGroupId();
        //获取当层行走结果集合
        ArrayList<FMMapCoord> coords = ConvertUtils.getMapCoords(mNaviAnalyser.getNaviResults(), groupId);
        //路径约束
        mNaviAnalyser.naviConstraint(groupId, coords, mLastMoveCoord, currentCoord);
        return currentCoord;
    }

    public void navigationEnd(double angle) {
        curAngle = angle;
        curNaviRoutesIndex = 0;
        // 可记录！！
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createNaviEndPopupWindow();
            }
        });
    }


    public void naviEndClosed() {
        clearImageLayer();
        clearImageMarker();
        clearLineLayer();
        clearWalkPoints();
        if (mClickedFacility != null) {
            mClickedFacility.setSelected(false);
        }
        if (mClickedModel != null) {
            mClickedModel.setSelected(false);
        }
        // 移除原来的定位
        mLocationLayer.removeMarker(startLocationMarker);
        // 导航到终点时在当前位置做标记
        startLocationMarker = new FMLocationMarker(curGroupId, stCoord.getMapCoord());
        //设置定位点图片
        startLocationMarker.setActiveImageFromAssets("active.png");
        //设置定位图片宽高
        startLocationMarker.setMarkerWidth(80);
        startLocationMarker.setMarkerHeight(80);
        startLocationMarker.setAngle((float) curAngle);
        mLocationLayer.addMarker(startLocationMarker);
        mFMMap.updateMap();
    }

    @OnClick(R.id.walk_around_fab)
    public void walk_around() {
        createWalkAroundPopupWindow();
    }

    private void createWalkAroundPopupWindow() {
        if (walkAroundPopupWindow != null) {
            walkAroundPopupWindow.dismiss();
            walkAroundPopupWindow = null;
        }
        String[] data = WalkAroundHelper.getData(mFMMap, mSearchAnalyser);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, data);
        walkAroundPopupWindow = new WalkAroundPopupWindow(this, arrayAdapter);
        walkAroundPopupWindow.showAtLocation(findViewById(R.id.map_main_ll),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        walkAroundPopupWindow.dismissOutSide(MainActivity.this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                walkAroundPopupWindow.dismiss();
                walkAroundPopupWindow = null;
            }
        });
    }
}
