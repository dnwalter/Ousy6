package com.huaweisoft.ousy.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.huaweisoft.ousy.MainApplication;

/**
 * 传感器基类
 * Created by ousy on 2016/8/8.
 */
public abstract class BaseSensor
{
    private SensorManager mManager;

    public BaseSensor(int... typeSensor)
    {
        mManager = (SensorManager) MainApplication.getContext().getSystemService(Context.SENSOR_SERVICE);
        for(int i : typeSensor)
        {
            Sensor sensor = mManager.getDefaultSensor(i);
            mManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unRegister()
    {
        if (mManager != null)
        {
            mManager.unregisterListener(mListener);
        }
    }

    private SensorEventListener mListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            sensorChanged(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
        }
    };

    // 传感器事件变化
    protected abstract void sensorChanged(SensorEvent event);
}
