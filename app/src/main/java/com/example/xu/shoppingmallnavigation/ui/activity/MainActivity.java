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
import com.example.xu.shoppingmallnavigation.ui.activity.adapter.MapSearchAdapter;
import com.example.xu.shoppingmallnavigation.ui.activity.widget.MapPopupWindow;
import com.example.xu.shoppingmallnavigation.utils.KeyBoardUtils;
import com.example.xu.shoppingmallnavigation.utils.MapSearchUtils;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMFacilityLayer;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // 设置ActionBar
        setSupportActionBar(toolbar);
    }

    public void initViews() {

    }

    @Override
    public void onMapInitSuccess(String path) {
        super.onMapInitSuccess(path);
        mFacilityLayer.setOnFMNodeListener(mOnFacilityClickListener);
        mModelLayer.setOnFMNodeListener(mOnFMNodeListener);
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
//                presenter.searchModelByKeyword(s);
                Log.i("shopping", s);
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
//
            createPopupWindow(mClickedModel.getName(), "");
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

            createPopupWindow(facility.getName(), "");
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
            popupWindow.dismissOutSide(MainActivity.this);
            switch (v.getId()) {
                case R.id.map_navi_bt:
                    break;
                default:
                    break;
            }
        }
    };

    private void createPopupWindow(String name, String distance) {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        popupWindow = new MapPopupWindow(MainActivity.this, listener, name, distance);
        popupWindow.showAtLocation(findViewById(R.id.map_main_ll),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.dismissOutSide(MainActivity.this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                clearFocus(mClickedModel);
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

            createPopupWindow(model.getName(), "");

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
    private void clearFocus(FMModel model) {
        if (!model.equals(mLastClicked)) {
            if (mLastClicked != null) {
                mLastClicked.setSelected(false);
            }
            this.mLastClicked = model;
            this.mLastClicked.setSelected(true);
        }
        mFMMap.updateMap();
    }


}
