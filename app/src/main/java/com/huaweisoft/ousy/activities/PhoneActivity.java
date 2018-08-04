package com.huaweisoft.ousy.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.business.WifiConnectManager;
import com.huaweisoft.ousy.helpers.HintHelper;
import com.huaweisoft.ousy.helpers.PermissionHelper;
import com.huaweisoft.ousy.receiver.PhoneReceiver.BatteryReceiver;
import com.huaweisoft.ousy.utils.LocationUtil;
import com.huaweisoft.ousy.utils.PhoneUtil;
import com.huaweisoft.ousy.utils.ToastUtil;

/**
 * Created by Administrator on 2016/7/3.
 */

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener, BatteryReceiver.BatteryCallback,
        LocationUtil.LocationCallback
{
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private Button btnUp;
    private Button btnDown;
    private Button btnMute;
    private Button btnVibrate;
    private Button btnWifi;
    private Button btnGPS;
    private Button btn4G;
    private Button btnMemory;
    private TextView tvBattery;
    private TextView tvLocation;
    private BatteryReceiver mBatteryReceiver;
    private LocationUtil mLocationUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        btnUp = (Button) findViewById(R.id.btn_up);
        btnDown = (Button) findViewById(R.id.btn_down);
        btnMute = (Button) findViewById(R.id.btn_mute);
        btnVibrate = (Button) findViewById(R.id.btn_vibrate);
        btnWifi = (Button) findViewById(R.id.btn_wifi);
        btnGPS = (Button) findViewById(R.id.btn_gps);
        btn4G = (Button) findViewById(R.id.btn_4g);
        btnMemory = (Button) findViewById(R.id.btn_memory);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnMute.setOnClickListener(this);
        btnVibrate.setOnClickListener(this);
        btnWifi.setOnClickListener(this);
        btnGPS.setOnClickListener(this);
        btn4G.setOnClickListener(this);
        btnMemory.setOnClickListener(this);

        tvBattery = (TextView) findViewById(R.id.phone_battery);
        tvLocation = (TextView) findViewById(R.id.phone_location);
        initReceiver();
        initLocation();
    }

    @Override
    protected void onResume()
    {
        mLocationUtil.setLocationListener();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        mLocationUtil.removeLocationListener();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mBatteryReceiver.unregister();
        super.onDestroy();
    }

    private void initReceiver()
    {
        mBatteryReceiver = new BatteryReceiver(this);
        mBatteryReceiver.setCallback(this);
    }

    // 初始化获取GPS数据
    private void initLocation()
    {
        // 是否有获取位置的权限
        if (PermissionHelper.applyPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION_PERMISSIONS))
        {
            mLocationUtil = new LocationUtil();
            mLocationUtil.setCallback(this);
        }
    }

    @Override
    public void battery(int level, int status)
    {
        tvBattery.setText(BatteryReceiver.getStrStatus(status) + level + "%");
    }

    @Override
    public void locationInfo(double longitude, double latitude, float speed, float bearing)
    {
        tvLocation.setText("经度：" + longitude + "\n纬度：" + latitude + "\n速度：" + speed + "\n方向：" + bearing);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_LOCATION_PERMISSIONS:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    initLocation();
                }
                else
                {
                    HintHelper.getInstance(this).toastShort("需要获取位置信息的权限！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_up:
                PhoneUtil.INSTANCE.upVolume();
                break;
            case R.id.btn_down:
                PhoneUtil.INSTANCE.downVolume();
                break;
            case R.id.btn_mute:
//                PhoneUtil.getPhoneUtil().setMute(true);
                PhoneUtil.INSTANCE.setVolume(1);
                break;
            case R.id.btn_vibrate:
                PhoneUtil.INSTANCE.vibrate(50);
                break;
            case R.id.btn_4g:
//                PhoneUtil.getPhoneUtil().toggleMobileData(!PhoneUtil.getPhoneUtil().isMobileDataOn());
                PhoneUtil.INSTANCE.setMobileData(!PhoneUtil.INSTANCE.isMobileDataOn());
                break;
            case R.id.btn_wifi:
//                WifiConnectManager.getInstance().connect("huawei-guest", "HUAWEIkey", WifiConnectManager
//                        .WIFICIPHER_WPA);
//                WifiConnectManager.getInstance().getWayIp();
                break;
            case R.id.btn_gps:
                mLocationUtil.openGPS(this);
                break;
            case R.id.btn_memory:
                float total = PhoneUtil.INSTANCE.getTotalInternalMemorySize();
                float unUsed = PhoneUtil.INSTANCE.getAvailableInternalMemorySize();
                ToastUtil.toastShort(total + ":" + unUsed);
                break;
        }
    }
}
