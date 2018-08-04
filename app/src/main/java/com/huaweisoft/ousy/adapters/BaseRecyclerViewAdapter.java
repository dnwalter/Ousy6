package com.huaweisoft.ousy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huaweisoft.ousy.interfaces.RecyclerViewCallback;

import java.util.List;

/**
 * recyclerview的adapter基类
 * Created by ousy on 2016/8/10.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<ViewHolder>
{
    public Context mContext;
    // 数据源
    public List<T> mList;
    public LayoutInflater mLayoutInflater;
    public int parentWidth;
    // 事件回调
    public RecyclerViewCallback mCallback;

    public BaseRecyclerViewAdapter(Context context)
    {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public BaseRecyclerViewAdapter(Context context, List<T> list)
    {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = createView(parent,viewType);
        ViewHolder viewHolder = createViewHolder(view,viewType);
        parentWidth = parent.getMeasuredWidth();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
//        initClickEvent(holder,position);
        setData(holder,position);
    }

    @Override
    public int getItemCount()
    {
       return mList.size();
    }

    // 设置回调
    public void setCallback(RecyclerViewCallback callback)
    {
        mCallback = callback;
    }

    // 刷新数据
    public void refreshData(List<T> list)
    {
        mList = list;
        sortData();
        notifyDataSetChanged();
    }

    /**
     * 对数据进行排序
     */
    protected void sortData()
    {

    }

    /**
     * 加载item布局
     *
     * @param viewGroup
     * @param viewType
     * @return
     */

    public abstract View createView(ViewGroup viewGroup, int viewType);

    /**
     * 创建viewHolder
     *
     * @param view
     * @return
     */
    public abstract ViewHolder createViewHolder(View view, int viewType);

    /**
     * 展示数据
     *
     * @param viewHolder
     * @param position
     */
    public abstract void setData(ViewHolder viewHolder, int position);
}
