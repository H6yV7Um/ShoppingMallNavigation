package com.example.xu.shoppingmallnavigation.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

    FMMap mFMMap;
    private MainPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainPresenter(this);
        openMapByPath();
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

    }
}
