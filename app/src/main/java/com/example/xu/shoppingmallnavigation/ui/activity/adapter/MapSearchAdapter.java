package com.example.xu.shoppingmallnavigation.ui.activity.adapter;

import android.content.Context;
import android.widget.Filter;

import com.example.xu.shoppingmallnavigation.R;
import com.example.xu.shoppingmallnavigation.base.BaseSearchAdapter;
import com.example.xu.shoppingmallnavigation.base.ViewHolder;
import com.fengmap.android.map.marker.FMModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xu on 2017/12/10.
 */

public class MapSearchAdapter extends BaseSearchAdapter<FMModel> {

    private ArrayFilter mArrayFilter;
    //未过滤数据
    private ArrayList<FMModel> mUnfiltered;

    public MapSearchAdapter(Context context, ArrayList<FMModel> mapModels) {
        super(context, mapModels, R.layout.layout_search_item);
    }

    @Override
    public void convert(ViewHolder viewHolder, FMModel item, int position) {
        viewHolder.setText(R.id.model_name, item.getName());
    }

    @Override
    public Filter getFilter() {
        if (mArrayFilter == null) {
            mArrayFilter = new MapSearchAdapter.ArrayFilter();
        }
        return mArrayFilter;
    }

    /**
     * 数据过滤
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mUnfiltered == null) {
                mUnfiltered = new ArrayList(mDatas);
            }

            results.values = mDatas;
            results.count = mDatas.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mDatas = (List) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if(resultValue instanceof FMModel){
                FMModel model = (FMModel) resultValue;
                return model.getName();
            }
            return super.convertResultToString(resultValue);
        }
    }
}
