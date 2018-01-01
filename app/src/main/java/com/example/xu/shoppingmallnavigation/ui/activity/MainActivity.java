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
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.widget.FMSwitchFloorComponent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Xu on 2017/11/26.
 */

public class MainActivity extends BaseMapActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.mapview_error)
    TextView tvError;

    private SearchView mSearchView;
    private MapSearchAdapter mapSearchAdapter;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private MapPopupWindow popupWindow;
    private NaviEndPopupWindow naviEndPopupWindow;
    /**
     * 约束过定位标注
     */
    private FMLocationMarker mHandledMarker;
    /**
     * 记录上一次行走坐标
     */
    private FMMapCoord mLastMoveCoord;

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

        for (FMFacilityLayer mFacilityLayer: mFacilityLayers) {
            mFacilityLayer.setOnFMNodeListener(mOnFacilityClickListener);
        }

        curGroupId = 1;
        fmSwitchFloorComponent = new FMSwitchFloorComponent(this);
        //最多显示10个
        fmSwitchFloorComponent.setMaxItemCount(10);
        fmSwitchFloorComponent.setOnFMSwitchFloorComponentListener(new FMSwitchFloorComponent.OnFMSwitchFloorComponentListener() {
            @Override
            public boolean onItemSelected(final int groupId, final String floorName) {

                mFMMap.setFocusByGroupIdAnimated(groupId, new FMLinearInterpolator(), new OnFMSwitchGroupListener() {
                    @Override
                    public void beforeGroupChanged() {

                    }

                    @Override
                    public void afterGroupChanged() {
                        curGroupId = groupId;
                    }
                });
                return true;
            }
        });
        fmSwitchFloorComponent.setFloorDataFromFMMapInfo(mFMMap.getFMMapInfo(), mFMMap.getFocusGroupId());

        mMapView.addComponent(fmSwitchFloorComponent, 50, 1300);
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
                if (!s.equals("")) {
                    ArrayList<FMModel> models = queryModelByKeyword(s);
                    if (mapSearchAdapter == null) {
                        mapSearchAdapter = new MapSearchAdapter(MainActivity.this, models);
                        mSearchAutoComplete.setAdapter(mapSearchAdapter);
                    } else {
                        mapSearchAdapter.setDatas(models);
                        mapSearchAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请输入搜索词！", Toast.LENGTH_LONG).show();
                }
                mapSearchAdapter = null;
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
     * 模型点击事件
     */
    private OnFMNodeListener mOnFMNodeListener = new OnFMNodeListener() {
        @Override
        public boolean onClick(FMNode node) {
            if (mClickedModel != null) {
                mClickedModel.setSelected(false);
            }
            FMModel model = (FMModel) node;
            mClickedModel = model;

            model.setSelected(true);
            mFMMap.updateMap();

//            FMMapCoord centerMapCoord = model.getCenterMapCoord();
            MapCoord curEndMapCoord = new MapCoord(curGroupId, model.getCenterMapCoord());
            if (!mClickedModel.getName().equals("")) {
                createPopupWindow(mClickedModel.getName(), "", stCoord, curEndMapCoord, false);
            }
//            Toast.makeText(MainActivity.this, model.getName(), Toast.LENGTH_LONG).show();
            return true;
        }

        @Override
        public boolean onLongPress(FMNode node) {
            return false;
        }
    };

    /**
     * 公共设施点击事件
     */
    private OnFMNodeListener mOnFacilityClickListener = new OnFMNodeListener() {
        @Override
        public boolean onClick(FMNode node) {
            if (mClickedFacility != null) {
                mClickedFacility.setSelected(false);
            }
            FMFacility facility = (FMFacility) node;
            mClickedFacility = facility;
            facility.setSelected(true);
            mFMMap.updateMap();
//            FMMapCoord centerMapCoord = facility.getPosition();
            MapCoord curEndMapCoord = new MapCoord(curGroupId, facility.getPosition());
            createPopupWindow(facility.getName(), "", stCoord, curEndMapCoord, true);
            return true;
        }

        @Override
        public boolean onLongPress(FMNode node) {
            return false;
        }
    };

    //为弹出窗口实现监听类
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            switch (v.getId()) {
                case R.id.map_navi_bt:
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                    if (mSearchView != null) {
                        mSearchView.clearFocus();
                    }
                    analyzeNavigation(stCoord, endCoord);
                    // 将当前终点置为起点
                    stCoord = endCoord;
                    endCoord = null;
                    break;
                case R.id.navi_end_close_bt:
                    if (naviEndPopupWindow != null) {
                        naviEndPopupWindow.dismiss();
                        naviEndPopupWindow = null;
                    }
                    naviEndClosed();
                    break;
                default:
                    break;
            }
        }
    };

    private void createPopupWindow(String name, String distance, MapCoord curStartCoord, MapCoord curEndCoord, final boolean isFacility) {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        stCoord = curStartCoord;
        endCoord = curEndCoord;
        popupWindow = new MapPopupWindow(MainActivity.this, listener, name, distance);
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


    private AdapterView.OnItemClickListener searchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            //关闭软键盘
            KeyBoardUtils.closeKeybord(mSearchAutoComplete, MainActivity.this);

            final FMModel model = (FMModel) adapterView.getItemAtPosition(position);
            mClickedModel = model;
            model.setSelected(true);
            mFMMap.updateMap();

            //切换楼层
            int groupId = model.getGroupId();
            if (groupId != mFMMap.getFocusGroupId()) {
                mFMMap.setFocusByGroupId(groupId, null);
            }

            //移动至中心点
            FMMapCoord mapCoord = model.getCenterMapCoord();
            mFMMap.moveToCenter(mapCoord, false);

//            clearImageMarker();
            MapCoord curEndMapCoord = new MapCoord(curGroupId, mapCoord);
            createPopupWindow(model.getName(), "", stCoord, curEndMapCoord, false);

            //添加图片
//            FMImageMarker imageMarker = MapSearchUtils.buildImageMarker(getResources(), mapCoord);
//            mImageLayers.get(model.getGroupId()).addMarker(imageMarker);

        }
    };

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
    public void onAnimationUpdate(FMMapCoord mapCoord, double distance, double angle) {
        updateHandledMarker(mapCoord, angle);
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

    public void navigationEnd() {
        // 可记录！！
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createNaviEndPopupWindow();
            }
        });
    }

    public void naviEndClosed() {
        mFMMap.removeLayer(mStartImageLayer);
        mFMMap.removeLayer(mEndImageLayer);
        mLineLayer.removeMarker(curFMLineMarker);
        mLocationLayer.removeMarker(startLocationMarker);
        if (mClickedFacility != null) {
            mClickedFacility.setSelected(false);
        }
        if (mClickedModel != null) {
            mClickedModel.setSelected(false);
        }
        mFMMap.updateMap();
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

}
