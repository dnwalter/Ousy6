package com.huaweisoft.ousy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * 方向传感器
 * 以下绕*轴旋转的角度（轴朝上顺时针）
 * x: 0到180，然后-180到0
 * y: 0到90到0到-90到0
 * z：0到359
 * Created by ousy on 2016/8/8.
 */
public class OrientationSensor extends BaseSensor
{
    private OrientationCallback mCallback;
    private boolean mIsOne = true;
    public boolean mFlag = true;

    public OrientationSensor()
    {
        super(Sensor.TYPE_ORIENTATION);
        Thread myThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (mFlag)
                {
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    mIsOne = true;
                }
            }
        });
        myThread.start();
    }

    public void setCallback(OrientationCallback callback)
    {
        mCallback = callback;
    }

    @Override
    protected void sensorChanged(SensorEvent event)
    {
        if (mIsOne)
        {
            if (null != mCallback)
            {
                mCallback.OrientationInfo("\nx:" + (int) event.values[1] + ", y:" + (int) event.values[2] + ", z:" +
                        (int) event.values[0]);
                mIsOne = false;
            }
        }
    }

    // 加速度回调
    public interface OrientationCallback
    {
        void OrientationInfo(String info);
    }
}
