package com.example.xu.shoppingmallnavigation.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import java.util.List;

/**
 * @Email hezutao@fengmap.com
 * @Version 2.0.0
 * @Description 普通适配器
 */
public abstract class BaseSearchAdapter<T> extends BaseAdapter implements ListAdapter, Filterable {
    protected final int mItemLayoutId;
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;

    public BaseSearchAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    public List<T> getDatas(){
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();

    }

    /**
     * 适配转换
     *
     * @param viewHolder holder
     * @param item       数据
     * @param position
     */
    public abstract void convert(ViewHolder viewHolder, T item, int position);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

}  