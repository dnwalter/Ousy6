package com.huaweisoft.ousy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * 地磁传感器
 * 角度
 * 以下绕*轴旋转的角度（轴朝上顺时针）
 * x: 0到90到0到-90到0
 * y: 0到-180，然后180到0
 * z：0到180 然后-180到0
 * Created by ousy on 2016/8/8.
 */
public class MagneticSensor extends BaseSensor
{
    private MagneticCallback mCallback;
    float[] accelerometerValues = new float[3];
    float[] magneticValues = new float[3];
    private boolean mIsOne = true;
    public boolean mFlag = true;

    public MagneticSensor()
    {
        super(Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ACCELEROMETER);
        Thread myThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (mFlag)
                {
                    try
                    {
                        Thread.sleep(500);
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

    public void setCallback(MagneticCallback callback)
    {
        mCallback = callback;
    }

    @Override
    protected void sensorChanged(SensorEvent event)
    {
        // 判断当前是加速度传感器还是地磁传感器
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // 注意赋值时要调用clone()方法
            accelerometerValues = event.values.clone();
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            // 注意赋值时要调用clone()方法
            magneticValues = event.values.clone();
        }
        float[] R = new float[9];
        float[] values = new float[3];
        // 三个方向上重力加速度的分加速度
        float gx = accelerometerValues[0];
        float gy = accelerometerValues[1];
        float gz = accelerometerValues[2];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticValues);
        SensorManager.getOrientation(R, values);

        // 弧度
        float ax = values[1];
        float ay = values[2];
        float az = values[0];
        // 角度
        int x = (int) Math.toDegrees(values[1]);
        int y = (int) Math.toDegrees(values[2]);
        int z = (int) Math.toDegrees(values[0]);
        float g = getGAcce(gx, gy, gz, -ay, az);
        if (mIsOne)
        {
            if (null != mCallback)
            {
                mCallback.magneticAInfo("\nx:" + (float) (((int) (gx * 100 + 0.5)) / 100.0) + ", y:" + (float) (((int) (gy * 100 + 0.5)) / 100.0) + ", z:" + (float) (((int) (gz * 100 + 0.5)) / 100.0));
                mCallback.magneticInfo("\nx:" + x + ", y:" + y + ", z:" + z);
                mCallback.magneticGInfo("\ng:" + g);
                mIsOne = false;
            }
        }
    }

    // 获取重力方向上的重力加速度
    private float getGAcce(float gx, float gy, float gz, float ay, float az)
    {
        float g = 0;
//        float h = 0;
//        float l = 0;
//        if (ay <= 0 && az <= 0)
//        {
//            g = (float) Math.sqrt(Math.pow(gy, 2) + Math.pow(gz, 2));
//        }
//        else
//        {
//            h = (float) (gx * Math.tan(Math.PI / 2 - ay));
//            l = (float) (gx * Math.tan(az));
//            g = (float) Math.sqrt(Math.pow(h, 2) + Math.pow(l, 2) + Math.pow(gx, 2));
//        }
        g = (float) Math.sqrt(Math.pow(gy, 2) + Math.pow(gz, 2) + Math.pow(gx, 2));

        return g;
    }

    // 回调
    public interface MagneticCallback
    {
        void magneticInfo(String info);

        void magneticAInfo(String info);
        void magneticGInfo(String info);
    }
}
