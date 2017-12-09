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
import android.widget.TextView;

import com.example.xu.shoppingmallnavigation.R;
import com.example.xu.shoppingmallnavigation.base.BaseMapActivity;
import com.example.xu.shoppingmallnavigation.ui.activity.widget.MapPopupWindow;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;

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

    SearchView mSearchView;

    private FMModelLayer mModelLayer;
    private FMModel mClickedModel;

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

        int groupId = mFMMap.getFocusGroupId();
        //公共设施图层
//        mFacilityLayer = mFMMap.getFMLayerProxy().getFMFacilityLayer(groupId);
//        mFacilityLayer.setOnFMNodeListener(mOnFacilityClickListener);
//        mFMMap.addLayer(mFacilityLayer);

        //模型图层
        mModelLayer = mFMMap.getFMLayerProxy().getFMModelLayer(groupId);
        mModelLayer.setOnFMNodeListener(mOnFMNodeListener);
        mFMMap.addLayer(mModelLayer);
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
        SearchView.SearchAutoComplete mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);

        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));

        //设置搜索栏适配器
//        mSearchView.setSuggestionsAdapter();

        // 监听器
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                presenter.searchModelByKeyword(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                Cursor cursor = TextUtils.isEmpty(s) ? null : queryData(s);
//                // 不要频繁创建适配器，如果适配器已经存在，则只需要更新适配器中的cursor对象即可。
//                if (mSearchView.getSuggestionsAdapter() == null) {
//                    mSearchView.setSuggestionsAdapter(new SimpleCursorAdapter(SearchViewActivity2.this, R.layout.item_layout, cursor, new String[]{"name"}, new int[]{R.id.text1}));
//                } else {
//                    mSearchView.getSuggestionsAdapter().changeCursor(cursor);
//                }
//
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
//            String content = getString(R.string.event_click_content, "模型", mGroupId, centerMapCoord.x, centerMapCoord.y);
            //TODO 弹窗！！！
            createPopupWindow(model.getName(), "");
//            Toast.makeText(MainActivity.this, model.getName(), Toast.LENGTH_LONG).show();
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
        popupWindow.dismissOutSide(MainActivity.this);

    }

}
