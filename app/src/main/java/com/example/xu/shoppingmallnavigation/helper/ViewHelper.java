package com.example.xu.shoppingmallnavigation.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMSegment;

import java.util.ArrayList;

/**
 * Created by Xu on 2017/12/17.
 */

public class ViewHelper {

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
     * 创建线
     *
     * @param segments 线段集合
     * @return
     */
    public static FMLineMarker buildLineMarker(ArrayList<FMSegment> segments) {
        FMLineMarker lineMarker = new FMLineMarker(segments);
        lineMarker.setLineWidth(3.0f);
        return lineMarker;
    }

    /**
     * 创建定位标注
     *
     * @param groupId  楼层id
     * @param mapCoord 坐标点
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord) {
        return buildLocationMarker(groupId, mapCoord, 0f);
    }

    /**
     * 创建定位标注
     *
     * @param groupId  楼层id
     * @param mapCoord 坐标点
     * @param angle    方向
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord, float angle) {
        FMLocationMarker locationMarker = new FMLocationMarker(groupId, mapCoord);
        //设置定位点图片
        locationMarker.setActiveImageFromAssets("active.png");
        //设置定位图片宽高
        locationMarker.setMarkerWidth(90);
        locationMarker.setMarkerHeight(90);
        locationMarker.setAngle(angle);
        return locationMarker;
    }

}
