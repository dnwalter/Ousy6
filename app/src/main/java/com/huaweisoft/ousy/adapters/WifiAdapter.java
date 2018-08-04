package com.huaweisoft.ousy.adapters;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.business.WifiConnectManager;

import java.util.List;

/**
 * WiFi列表的adapter
 * Created by ousy on 2016/8/10.
 */
public class WifiAdapter extends BaseRecyclerViewAdapter<ScanResult> implements View.OnClickListener
{
    private Holder mHolder;
    // 当前连接的WiFi
    private WifiInfo mWifiInfo;

    public WifiAdapter(Context context, List<ScanResult> list)
    {
        super(context, list);
        mWifiInfo = WifiConnectManager.INSTANCE.getConnectWifi();
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType)
    {
        View view = mLayoutInflater.inflate(R.layout.dialog_item_wifi, viewGroup, false);

        return view;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view, int viewType)
    {
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void setData(RecyclerView.ViewHolder viewHolder, int position)
    {
        mHolder = (Holder) viewHolder;
        ScanResult scanResult = mList.get(position);
        mHolder.llytRoot.setTag(position);
        mHolder.tvName.setText(scanResult.SSID);
        if (0 == position && mWifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED))
        {
            mHolder.tvState.setVisibility(View.VISIBLE);
            mHolder.llytRoot.setOnClickListener(null);
        }
        else
        {
            mHolder.tvState.setVisibility(View.INVISIBLE);
            mHolder.llytRoot.setOnClickListener(this);
        }
        int level = WifiConnectManager.INSTANCE.getWifiLevel(scanResult);
        mHolder.ivLevel.setImageResource(getLevleResId(level));
    }

    // 根据WiFi信号等级获取图标资源
    private int getLevleResId(int level)
    {
        if (level > 75)
        {
            return R.mipmap.ic_wifi_four;
        }
        else if (level > 50)
        {
            return R.mipmap.ic_wifi_three;
        }
        else if (level > 25)
        {
            return R.mipmap.ic_wifi_two;
        }
        else
        {
            return R.mipmap.ic_wifi_one;
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
        private TextView tvState;
        private ImageView ivLevel;

        public Holder(View itemView)
        {
            super(itemView);
            llytRoot = (LinearLayout) itemView.findViewById(R.id.wifidialog_root);
            tvName = (TextView) itemView.findViewById(R.id.wifidialog_tv_item_name);
            tvState = (TextView) itemView.findViewById(R.id.wifidialog_tv_item_state);
            ivLevel = (ImageView) itemView.findViewById(R.id.wifidialog_tv_item_level);
        }
    }
}
