package com.huaweisoft.ousy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.helpers.HintHelper;

/**
 * 网络功能类
 * 记得设置权限
 * Created by ousy on 2016/5/20.
 */
public enum  NetworkUtil
{
    INSTANCE;
    private Context mContext;

    private NetworkUtil()
    {
        mContext = MainApplication.getContext();
    }

    /**
     * 判断是否连网的方法
     *
     * @return true 为有可用网络
     */
    public boolean isNetworkAvailable()
    {
        boolean isNetwork = getNetType() > NetworkType.NONE.getValue();
        if (!isNetwork)
        {
            HintHelper.getInstance(mContext).toastShort("网络未连接，请连接后重试！");
        }

        return isNetwork;
    }

    /**
     * 判断是否连WiFi的方法
     *
     * @return true 为连接WiFi
     */
    public boolean isWifiAvailable()
    {
        boolean isWifi = getNetType() == NetworkType.WIFI.getValue();

        return isWifi;
    }

    /**
     * 网络类型
     * @return 网络类型值
     */
    private short getNetType()
    {
        Context context = MainApplication.getContext();
        short netType = NetworkType.NONE.getValue();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return netType;
        }
        else
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null)
            {
                // 判断当前网络状态是否为连接状态
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                {
                    switch (networkInfo.getType())
                    {
                        case ConnectivityManager.TYPE_MOBILE:
                            if (networkInfo.getExtraInfo().equalsIgnoreCase("cmnet"))
                            {
                                netType = NetworkType.NET.getValue();
                            }
                            else
                            {
                                netType = NetworkType.WAP.getValue();
                            }
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            netType = NetworkType.WIFI.getValue();
                            break;
                        default:
                            netType = NetworkType.NO_TYPE.getValue();
                            break;
                    }
                }
            }
        }

        return netType;
    }


    /**
     * 网络类型
     */
    public enum NetworkType
    {
        // 没网络
        NONE((short) 0),

        // 没分类的网络类型
        NO_TYPE((short) 1),

        // WAP网络
        WAP((short) 2),

        // NET网络
        NET((short) 3),

        // WIFI网络
        WIFI((short) 4);

        private short value;

        public short getValue()
        {
            return value;
        }

        NetworkType(short value)
        {
            this.value = value;
        }
    }
}

