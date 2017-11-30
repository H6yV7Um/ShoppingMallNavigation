package com.example.xu.shoppingmallnavigation.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.xu.shoppingmallnavigation.R;
import com.example.xu.shoppingmallnavigation.base.contract.main.MainContract;
import com.example.xu.shoppingmallnavigation.presenter.main.MainPresenter;
import com.example.xu.shoppingmallnavigation.utils.FileUtils;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Xu on 2017/11/26.
 */

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.mapview)
    FMMapView mapView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    SearchView mSearchView;

    FMMap mFMMap;
    private MainPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initViews();

        presenter = new MainPresenter(this);
        openMapByPath();
    }

    private void initViews() {
        // 设置ActionBar
        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(onMenuItemClick);
    }

    /**
     * 加载地图数据
     */
    private void openMapByPath() {
        mFMMap = mapView.getFMMap();
        //加载离线数据
        String path = FileUtils.getDefaultMapPath(this);
        presenter.loadMap(mFMMap, path);
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

    @Override
    public void showProgress() {
        Toast.makeText(MainActivity.this, "loading...", Toast.LENGTH_LONG);
    }

    @Override
    public void hideProgress() {
        Toast.makeText(MainActivity.this, "complete...", Toast.LENGTH_LONG);
    }

    @Override
    public void showFailMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG);
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
                presenter.searchModelByKeyword(mFMMap, s);
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

}
