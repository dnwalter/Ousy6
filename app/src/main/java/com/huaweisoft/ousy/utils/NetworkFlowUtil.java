package com.huaweisoft.ousy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import java.util.List;

/**
 * 流量统计工具
 * Created by ousy on 2016/8/6.
 */
public class NetworkFlowUtil
{
    private Context mContext;
    private NetworkFlowCallback mCallback;

    public NetworkFlowUtil(Context context)
    {
        mContext = context;
    }

    // 总上传流量（包括WiFi）
    public long getTotalTx()
    {
        return TrafficStats.getTotalTxBytes();
    }

    // 总下载流量（包括WiFi）
    public long getTotalRx()
    {
        return TrafficStats.getTotalRxBytes();
    }

    // 总上传流量（不包括WiFi）
    public long getMobileTx()
    {
        return TrafficStats.getMobileTxBytes();
    }

    // 总下载流量（不包括WiFi）
    public long getMobileRx()
    {
        return TrafficStats.getMobileRxBytes();
    }

    // 获取某应用程序上传流量
    public long getUidTx(String packagename)
    {
        return TrafficStats.getUidTxBytes(getUid(packagename));
    }

    // 获取某应用程序下载流量
    public long getUidRx(String packagename)
    {
        return TrafficStats.getUidRxBytes(getUid(packagename));
    }

    /**
     * 获取包名应用程序对应的进程uid
     * @param packagename 包名
     * @return uid
     */
    private int getUid(String packagename)
    {
        int uid = -1;
        //1.获取一个包管理器。
        PackageManager packageManager = mContext.getPackageManager();
        //2.遍历手机操作系统 获取所有的应用程序的uid
        List<ApplicationInfo> list = packageManager.getInstalledApplications(0);
        for (ApplicationInfo info : list)
        {
            if (info.packageName.equals(packagename))
            {
                uid = info.uid;
                break;
            }
        }

        return uid;
    }

    public void setCallback(NetworkFlowCallback callback)
    {
        mCallback = callback;
    }

    public interface NetworkFlowCallback
    {
        /**
         * 返回流量统计
         *
         * @param txBytes 上传的流量
         * @param rxBytes 下载的流量
         */
        void NetworkFlowBytes(long txBytes, long rxBytes);
    }
}
