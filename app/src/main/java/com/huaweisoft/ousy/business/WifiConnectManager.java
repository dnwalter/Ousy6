package com.huaweisoft.ousy.business;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.helpers.StringHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * WiFi自动连接业务
 * 权限：ACCESS_WIFI_STATE，CHANGE_WIFI_STATE
 * Created by ousy on 2016/8/5.
 */
public enum  WifiConnectManager
{
    INSTANCE;
    // 加密方式
    // 没密码
    public static final int WIFICIPHER_NOPASS = 0;
    // WEP
    public static final int WIFICIPHER_WEP = 1;
    // WPA
    public static final int WIFICIPHER_WPA = 2;
    private static final String TAG = "WifiConnectManager";
    private WifiManager mWifiManager;
    // 完成连接的回调
    private ConnectCallback mConnectCallback;

    private WifiConnectManager()
    {
        mWifiManager = (WifiManager) MainApplication.getContext().getSystemService(Context.WIFI_SERVICE);
    }

    // 因为WiFiInfo内部getMeteredHint的方法为隐藏的方法
    // 这方法把WiFiinfo的tostring里的Metered hint字段的值切出来
    public boolean getMeteredHint(WifiInfo wifiInfo)
    {
        int index = wifiInfo.toString().indexOf("Metered hint: ");
        String str = wifiInfo.toString().substring(index + 14);
        boolean meteredHint = str.contains("true");

        return meteredHint;
    }

    // 获取wifi网关
    public String getWayIp()
    {
        DhcpInfo di = mWifiManager.getDhcpInfo();
        long wayIpL = di.gateway;
        //网关地址
        String wayIpS = StringHelper.longToIp(wayIpL);

        return wayIpS;
    }

    // 获取wifi子网掩码
    public String getNetMaskIp()
    {
        DhcpInfo di = mWifiManager.getDhcpInfo();
        long netmaskIpL = di.netmask;
        //子网掩码地址
        String netmaskIpS = StringHelper.longToIp(netmaskIpL);

        return netmaskIpS;
    }

    // 获取搜索的WiFi列表
    public List<ScanResult> getWifiList()
    {
        return mWifiManager.getScanResults();
    }

    // 获取wifi信号强度
    public int getWifiLevel(ScanResult scanResult)
    {
        return WifiManager.calculateSignalLevel(scanResult.level, 100);
    }

    // 获取连接的WifiInfo
    public WifiInfo getConnectWifi()
    {
        return mWifiManager.getConnectionInfo();
    }

    public void setConnectCallback(ConnectCallback callback)
    {
        mConnectCallback = callback;
    }

    // 提供一个外部接口，传入要连接的无线网
    public void connect(String ssid, String password, int type)
    {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    // 连接WiFi线程
    private class ConnectRunnable implements Runnable
    {
        private String ssid;

        private String password;

        private int type;

        public ConnectRunnable(String ssid, String password, int type)
        {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        @Override
        public void run()
        {
            // 打开wifi
            openWifi();
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING)
            {
                try
                {
                    // 为了避免程序一直while循环，让它睡个100毫秒检测……
                    Thread.sleep(100);
                } catch (InterruptedException ie)
                {
                    Log.e(TAG, ie.getMessage());
                }
            }

            WifiConfiguration wifiConfig;
            WifiConfiguration tempConfig = isExsits(ssid);
            WifiInfo info = getConnectWifi();
            String maxText = info.getMacAddress();
            String strssid = info.getSSID();
            int networkID = info.getNetworkId();
            int speed = info.getLinkSpeed();
            String str = "mac：" + maxText + "\n\r"
                    + "ssid :" + strssid + "\n\r"
                    + "net work id :" + networkID + "\n\r"
                    + "connection speed:" + speed + "\n\r";
            if (tempConfig != null)
            {
                // 在Android6.0， 若该wifi曾经连接过，在wifi列表中有保存
                // WifiManager.addNetwork()方法会返回-1
                // 导致连不上指定wifi
                mWifiManager.removeNetwork(tempConfig.networkId);
            }

            wifiConfig = createWifiInfo(ssid, password, type);
            if (wifiConfig == null)
            {
                Log.d(TAG, "wifiConfig is null!");
                return;
            }
            int netID = mWifiManager.addNetwork(wifiConfig);
            boolean enabled = mWifiManager.enableNetwork(netID, true);
            Log.d(TAG, "enableNetwork status enable=" + enabled);

            boolean connected = mWifiManager.reconnect();
            Log.d(TAG, "enableNetwork connected=" + connected);
            if (null != mConnectCallback)
            {
                mConnectCallback.finishConnect(enabled);
            }
        }
    }

    // 打开wifi功能
    private boolean openWifi()
    {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled())
        {
            bRet = mWifiManager.setWifiEnabled(true);
        }

        return bRet;
    }

    /**
     * 创建WiFi配置对象
     *
     * @param SSID     WiFi名
     * @param password WiFi密码
     * @param type     WiFi类型
     * @return
     */
    private WifiConfiguration createWifiInfo(String SSID, String password, int type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        switch (type)
        {
            case WIFICIPHER_NOPASS:
                config = setupNoPass(config);
                break;
            case WIFICIPHER_WEP:
                config = setupWEP(config, password);
                break;
            case WIFICIPHER_WPA:
                config = setupWPA(config, password);
                break;
            default:
                break;
        }

        return config;
    }

    // 设置没密码的WiFi
    private WifiConfiguration setupNoPass(WifiConfiguration config)
    {
        config.wepKeys[0] = "";
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.wepTxKeyIndex = 0;

        return config;
    }

    // 设置wep
    private WifiConfiguration setupWEP(WifiConfiguration config, String password)
    {
        if (!StringHelper.isNullOrEmpty(password))
        {
            // 判断该WiFi是否经过wep加密，对密码进行相应处理
            if (isHexWepKey(password))
            {
                config.wepKeys[0] = password;
            }
            else
            {
                config.wepKeys[0] = "\"" + password + "\"";
            }
        }
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.wepTxKeyIndex = 0;

        return config;
    }

    // 设置WPA
    private WifiConfiguration setupWPA(WifiConfiguration config, String password)
    {
        config.preSharedKey = "\"" + password + "\"";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        // 此处需要修改否则不能自动重联
        // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;

        return config;
    }

    private static boolean isHexWepKey(String wepKey)
    {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58)
        {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key)
    {
        for (int i = key.length() - 1; i >= 0; i--)
        {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f'))
            {
                return false;
            }
        }

        return true;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (null != existingConfigs)
        {
            for (WifiConfiguration i : existingConfigs)
            {
                // 发现小米手机i.SSID这个值没有两个双引号
                if (i.SSID.equals("\"" + SSID + "\"") || i.SSID.equals(SSID))
                {
                    return i;
                }
            }
        }

        return null;
    }

    /***
     * ---------------- wifi热点功能 ---------------------
     ***/

    // 判断wifi是否打开
    public boolean isEnable()
    {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 开启wifi热点
     *
     * @param ssid
     * @param passwd
     */
    public void startWifiAp(String ssid, String passwd)
    {
        if (mWifiManager.isWifiEnabled())
        {
            mWifiManager.setWifiEnabled(false);
        }
        if (!isWifiApEnabled())
        {
            stratWifiAp(ssid, passwd);
        }
    }

    private void stratWifiAp(String ssid, String password)
    {
        Method method1 = null;
        try
        {
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = ssid;
            netConfig.preSharedKey = password;
            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            method1.invoke(mWifiManager, netConfig, true);
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        } catch (SecurityException e)
        {
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 关闭wifi热点
     */
    public void closeWifiAp()
    {
        if (isWifiApEnabled())
        {
            try
            {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class,
                        boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断热点是否打开
     *
     * @return
     */
    public boolean isWifiApEnabled()
    {
        try
        {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public interface ConnectCallback
    {
        /**
         * 完成连接
         *
         * @param enabled 是否成功
         */
        void finishConnect(boolean enabled);
    }
}
