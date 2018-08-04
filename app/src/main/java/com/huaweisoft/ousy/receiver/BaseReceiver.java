package com.huaweisoft.ousy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 广播接收器基类
 * Created by ousy on 2016/8/1.
 */
public abstract class BaseReceiver<T> extends BroadcastReceiver
{
    private Context mContext;
    public T mCallback;

    @Override
    public void onReceive(Context context, Intent intent)
    {

    }

    public BaseReceiver(Context context)
    {
        mContext = context;
    }

    public Intent register(IntentFilter filter)
    {
        return mContext.registerReceiver(this, filter);
    }

    public void unregister()
    {
        if (null != this)
        {
            mContext.unregisterReceiver(this);
        }
    }

    public void setCallback(T callback)
    {
        mCallback = callback;
    }
}
