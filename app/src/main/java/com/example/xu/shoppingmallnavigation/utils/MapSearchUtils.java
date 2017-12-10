package com.example.xu.shoppingmallnavigation.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.xu.shoppingmallnavigation.R;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByKeywordRequest;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;

import java.util.ArrayList;

/**
 * Created by Xu on 2017/12/10.
 */

public class MapSearchUtils {

    /**
     * 通过关键字查询模型
     *
     * @param searchAnalyser 搜素控制
     * @param map            地图
     * @param keyword        关键字
     */
    public static ArrayList<FMModel> queryModelByKeyword(FMMap map, FMSearchAnalyser searchAnalyser,
                                                         String keyword) {
        int[] groupIds = map.getMapGroupIds();
        return queryModelByKeyword(map, groupIds, searchAnalyser, keyword);
    }

    /**
     * 通过关键字查询模型
     *
     * @param searchAnalyser 搜素控制
     * @param map            地图
     * @param keyword        关键字
     * @param groupIds       楼层集合
     */
    public static ArrayList<FMModel> queryModelByKeyword(FMMap map, int[] groupIds, FMSearchAnalyser searchAnalyser,
                                                         String keyword) {
        ArrayList<FMModel> list = new ArrayList<FMModel>();
        FMSearchModelByKeywordRequest request = new FMSearchModelByKeywordRequest(groupIds, keyword);
        ArrayList<FMSearchResult> result = searchAnalyser.executeFMSearchRequest(request);
        for (FMSearchResult r : result) {
            String fid = (String) r.get("FID");
            FMModel model = map.getFMLayerProxy().queryFMModelByFid(fid);
            list.add(model);
        }
        Log.i("shopping-list", "" + list);
        return list;
    }

    /**
     * 创建定位标注
     *
     * @param groupId  定位标注所在楼层
     * @param mapCoord 定位标注坐标
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord) {
        FMLocationMarker mLocationMarker = new FMLocationMarker(groupId, mapCoord);
        //设置定位点图片
        mLocationMarker.setActiveImageFromAssets("active.png");
        //设置定位图片宽高
        mLocationMarker.setMarkerWidth(90);
        mLocationMarker.setMarkerHeight(90);
        return mLocationMarker;
    }

    /**
     * 添加图片标注
     *
     * @param resources 资源
     * @param mapCoord  坐标
     * @param resId     资源id
     */
    public static FMImageMarker buildImageMarker(Resources resources, FMMapCoord mapCoord, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
        FMImageMarker imageMarker = new FMImageMarker(mapCoord, bitmap);
        //设置图片宽高
        imageMarker.setMarkerWidth(90);
        imageMarker.setMarkerHeight(90);
        //设置图片在模型之上
        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_MODEL_ABOVE);
        return imageMarker;
    }

    /**
     * 添加图片标注
     *
     * @param resources 资源
     * @param mapCoord  坐标点
     */
    public static FMImageMarker buildImageMarker(Resources resources, FMMapCoord mapCoord) {
        return buildImageMarker(resources, mapCoord, R.mipmap.ic_marker_blue);
    }
}
