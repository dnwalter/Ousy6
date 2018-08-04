package com.huaweisoft.ousy.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.business.WifiConnectManager;
import com.huaweisoft.ousy.helpers.PermissionHelper;
import com.huaweisoft.ousy.utils.ToastUtil;
import com.huaweisoft.ousy.views.WifiDialog;

import java.util.List;

/**
 * Created by ousy on 2016/10/14.
 */

public class WifiActivity extends AppCompatActivity implements WifiConnectManager.ConnectCallback
{
    private static final int PERMISSIONS_ACCESS_COARSE_LOCATION = 0;
    private WifiDialog mWifiDialog;
    private Button btnConnect;
    private Button btnWifiAp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        // 设置wifi连接完成时的回调接口
        WifiConnectManager.INSTANCE.setConnectCallback(this);

        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 是否有获取位置权限
                if (PermissionHelper.applyPermissions(WifiActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION,
                        PERMISSIONS_ACCESS_COARSE_LOCATION))
                {
                    // 不在连接热点时执行
                    showWifiDialog();
                }
            }
        });
        btnWifiAp = (Button) findViewById(R.id.btn_wifi_ap);
        btnWifiAp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                WifiConnectManager.INSTANCE.startWifiAp("ousyhelmet", "@@helmet");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 不在连接热点时执行
                    showWifiDialog();
                }
                break;
            default:
                break;
        }
    }

    // 显示WiFi对话框
    private void showWifiDialog()
    {
        List<ScanResult> list = WifiConnectManager.INSTANCE.getWifiList();
        WifiInfo connectInfo = WifiConnectManager.INSTANCE.getConnectWifi();
        if (null != list)
        {
            // 把正在连接的wifi排在第一位
            list = moveWifiTop(list, connectInfo);
        }
        // 判断是否打开wifi
        if (null != connectInfo.getBSSID())
        {
            if (list.size() > 0)
            {
                mWifiDialog = new WifiDialog(this);
                mWifiDialog.setList(list);
                mWifiDialog.show();
            }
            else
            {
                //                ToastUtil.toastShort(getString(R.string.main_hint_nowifi));
            }
        }
        else
        {
            //            ToastUtil.toastShort(getString(R.string.main_hint_no_openwifi));
        }
    }

    // 正在连接的热点移到第一位
    private List<ScanResult> moveWifiTop(List<ScanResult> list, WifiInfo connectInfo)
    {
        int i = 0;
        for (; i < list.size(); i++)
        {
            if (list.get(i).BSSID.equals(connectInfo.getBSSID()))
            {
                break;
            }
        }

        if (i < list.size())
        {
            ScanResult connectScan = list.get(i);
            list.remove(i);
            list.add(0, connectScan);
        }
        return list;
    }

    @Override
    public void finishConnect(boolean enabled)
    {
        if (enabled)
        {
            while (true)
            {
                WifiInfo wifiInfo = WifiConnectManager.INSTANCE.getConnectWifi();
                // 一直等到wifi连接成功，才跳出循环
                if (wifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED) && WifiConnectManager.INSTANCE.getMeteredHint(wifiInfo))
                {
                    break;
                }
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    //                    Log.e(TAG, e.getMessage());
                }
            }

        }
    }
}
