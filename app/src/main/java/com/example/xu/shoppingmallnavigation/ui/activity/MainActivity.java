package com.example.xu.shoppingmallnavigation.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.xu.shoppingmallnavigation.R;
import com.example.xu.shoppingmallnavigation.base.BaseMapActivity;
import com.example.xu.shoppingmallnavigation.base.contract.main.MainContract;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Xu on 2017/11/26.
 */

public class MainActivity extends BaseMapActivity implements MainContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    SearchView mSearchView;

    private FMModelLayer mModelLayer;
    private int mGroupId = 1;
    private FMModel mClickedModel;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
//        presenter = new MainPresenter(this);
        //地图点击事件
//        mFMMap.setOnFMMapClickListener(this);
        //模拟导航需要的起始点坐标和起始点楼层id
//        int stGroupId = 1;
//        FMMapCoord stCoord = new FMMapCoord(12961573.57171745, 4861851.492463955);
//        int endGroupId = 1;
//        FMMapCoord endCoord = new FMMapCoord(12961699.79823795, 4861826.46384646);
//        presenter.analyzeNavigation(stGroupId, stCoord, endGroupId, endCoord);
    }

    public void initViews() {
        // 设置ActionBar
        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(onMenuItemClick);
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
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        Log.i("shopping", msg);
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

        return super.onCreateOptionsMenu(menu);
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

            Toast.makeText(MainActivity.this, model.getName(), Toast.LENGTH_LONG).show();
            return true;
        }

        @Override
        public boolean onLongPress(FMNode node) {
            return false;
        }
    };


}
