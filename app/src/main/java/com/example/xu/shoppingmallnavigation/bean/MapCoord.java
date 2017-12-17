package com.example.xu.shoppingmallnavigation.bean;

import com.fengmap.android.map.geometry.FMMapCoord;

/**
 * Created by Xu on 2017/12/17.
 */

public class MapCoord {

    private int groupId;
    private FMMapCoord mapCoord;

    public MapCoord(int groupId, FMMapCoord mapCoord) {
        this.groupId = groupId;
        this.mapCoord = mapCoord;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public FMMapCoord getMapCoord() {
        return mapCoord;
    }

    public void setMapCoord(FMMapCoord mapCoord) {
        this.mapCoord = mapCoord;
    }
}