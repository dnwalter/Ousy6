package com.huaweisoft.ousy.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.helpers.StringHelper;

import java.util.List;

/**
 * 蓝牙列表的adapter
 * Created by ousy on 2016/10/13.
 */
public class BluetoothAdapter extends BaseRecyclerViewAdapter<BluetoothDevice> implements View.OnClickListener
{
    private final static int TYPE_BODY = 1;
    private final static int TYPE_FOOTVIEW = 2;
    private Holder mHolder;
    // 加载进度框是否显示
    private boolean mProgressVisiable = true;

    public BluetoothAdapter(Context context, List<BluetoothDevice> list)
    {
        super(context, list);
    }

    public void setProgressVisiable(boolean visiable)
    {
        mProgressVisiable = visiable;
        if (visiable)
        {
            if (null != mList.get(mList.size() - 1))
            {
                mList.add(null);
            }
        }
        else
        {
            if (null == mList.get(mList.size() - 1))
            {
                mList.remove(mList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (null == mList.get(position))
        {
            return TYPE_FOOTVIEW;
        }

        return TYPE_BODY;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType)
    {
        View view = null;
        switch (viewType)
        {
            case TYPE_BODY:
                view = mLayoutInflater.inflate(R.layout.dialog_item_bluetooth, viewGroup, false);
                break;
            case TYPE_FOOTVIEW:
                view = mLayoutInflater.inflate(R.layout.dialog_search_more_bluetooth, viewGroup, false);
                break;
            default:
                break;
        }

        return view;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case TYPE_BODY:
                viewHolder = new Holder(view);
                break;
            case TYPE_FOOTVIEW:
                viewHolder = new FooterViewHolder(view);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void setData(RecyclerView.ViewHolder viewHolder, int position)
    {
        switch (getItemViewType(position))
        {
            case TYPE_BODY:
                setBody(viewHolder, position);
                break;
            case TYPE_FOOTVIEW:
                setFootView(viewHolder);
                break;
            default:
                break;
        }
    }

    private void setBody(RecyclerView.ViewHolder viewHolder, int position)
    {
        mHolder = (Holder) viewHolder;
        BluetoothDevice device = mList.get(position);
        mHolder.llytRoot.setTag(position);
        mHolder.llytRoot.setOnClickListener(this);
        String deviceName = device.getName();
        if (StringHelper.isNullOrEmpty(deviceName))
        {
            mHolder.tvName.setText(device.getAddress());
        }
        else
        {
            mHolder.tvName.setText(deviceName);
        }
    }

    // 设置底部加载view
    private void setFootView(RecyclerView.ViewHolder viewHolder)
    {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        if (mProgressVisiable)
        {
            footerViewHolder.itemView.setVisibility(View.VISIBLE);
        }
        else
        {
            footerViewHolder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void sortData()
    {
        super.sortData();
        int i = 0;
        for (; i < mList.size(); i++)
        {
            if (null == mList.get(i))
            {
                mList.remove(i);
                break;
            }
        }
        if (i <= mList.size())
        {
            mList.add(null);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (null != mCallback)
        {
            mCallback.onItemViewClick(v.getId(), (Integer) v.getTag());
        }
    }

    private class Holder extends RecyclerView.ViewHolder
    {
        private LinearLayout llytRoot;
        private TextView tvName;

        public Holder(View itemView)
        {
            super(itemView);
            llytRoot = (LinearLayout) itemView.findViewById(R.id.bluetooth_root);
            tvName = (TextView) itemView.findViewById(R.id.bluetooth_dialog_tv_item_name);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
