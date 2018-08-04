package com.huaweisoft.ousy.utils;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.huaweisoft.ousy.MainApplication;

import java.util.List;

/**
 * 定位帮助类
 * 权限：ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
 * Created by ousy on 2016/8/2.
 */

public class LocationUtil
{
    private Context mContext;
    private LocationCallback mCallback;
    private LocationManager mLocationManager;
    private Location mLocation;
    private MyLocationListener mLocationListener;

    public LocationUtil()
    {
        mContext = MainApplication.getContext();
        mLocationListener = new MyLocationListener();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocation = getLocation();
    }

    private Location getLocation()
    {
        String provider;
        // 获取所有可用的位置提供器
        List<String> providerList = mLocationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER))
        {
            provider = LocationManager.GPS_PROVIDER;
        }
        else if (providerList.contains(LocationManager.NETWORK_PROVIDER))
        {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else
        {
            return null;
        }

        // 这里使用到了Criteria，可根据当前设备情况自动选择哪种location provider
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
//        criteria.setAltitudeRequired(false);//不要求海拔信息
//        criteria.setBearingRequired(false);//不要求方位信息
//        criteria.setCostAllowed(false);//是否允许付费
//        criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
//        provider = mLocationManager.getBestProvider(criteria, true);

        return mLocationManager.getLastKnownLocation(provider);
    }

    public void setCallback(LocationCallback callback)
    {
        mCallback = callback;
    }

    // GPS是否开启
    public boolean isGPS()
    {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 设置LocationListener
    public void setLocationListener()
    {
        showLocation(mLocation);
        if (null != mLocationManager)
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, (float) 1, mLocationListener);
        }
    }

    // 移除LocationListener
    public void removeLocationListener()
    {
        if (null != mLocationManager)
        {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    public void showLocation(Location location)
    {
        if (location != null)
        {
            mCallback.locationInfo(location.getLongitude(), location.getLatitude(), location.getSpeed(), location
                    .getBearing());
        }
    }

    public interface LocationCallback
    {
        /**
         * 位置信息回调
         *
         * @param longitude 经度
         * @param latitude  维度
         * @param speed     速度
         * @param bearing   方向
         */
        void locationInfo(double longitude, double latitude, float speed, float bearing);
    }

    // 自定义locationListener
    private class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(Location location)
        {
            if (null != location)
            {
                mLocation = location;
            }
            showLocation(location);
        }

        public void onProviderDisabled(String provider)
        {
            showLocation(null);
        }

        public void onProviderEnabled(String provider)
        {
            Location location = mLocationManager.getLastKnownLocation(provider);
            if (null != location)
            {
                mLocation = location;
            }
            showLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    }

    /**
     * 强制帮用户打开GPS
     * 系统权限android.permission.WRITE_SECURE_SETTINGS
     * @param context
     */
    public void openGPS(Context context)
    {
//        Intent intent = new Intent("Android.location.GPS_ENABLED_CHANGE");
//        intent.putExtra("enabled", true);
//        context.sendBroadcast(intent);
//
//        if (!isGPS())
//        {
//            final Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//            poke.setData(Uri.parse("3"));
//            context.sendBroadcast(poke);
//        }
        ContentResolver resolver = mContext.getContentResolver();
        Settings.Secure.setLocationProviderEnabled(resolver,
                LocationManager.GPS_PROVIDER, !isGPS());
    }
}
