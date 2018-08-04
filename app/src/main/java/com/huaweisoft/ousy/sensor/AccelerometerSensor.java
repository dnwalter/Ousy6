package com.huaweisoft.ousy.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.helpers.HintHelper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 加速度传感器
 * Created by ousy on 2016/8/8.
 */
public class AccelerometerSensor extends BaseSensor
{
    final float alpha = (float) 0.95;
    private AcceCallback mCallback;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private boolean mIsOne = true;
    public boolean mFlag = true;
    private StringBuilder mBuilder = new StringBuilder("acce,time\n");
    private int mTimes = 100;

    public AccelerometerSensor(int... type)
    {
        super(type);
        Thread myThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (mFlag)
                {
                    try
                    {
                        Thread.sleep(100);
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

    public void setCallback(AcceCallback callback)
    {
        mCallback = callback;
    }

    @Override
    protected void sensorChanged(SensorEvent event)
    {
        // 加速度传感器和重力传感器，获取线性加速度
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            // 注意赋值时要调用clone()方法
            gravity = event.values.clone();
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            //            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            //            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            //            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            //
            //            linear_acceleration[0] = event.values[0] - gravity[0];
            //            linear_acceleration[1] = event.values[1] - gravity[1];
            //            linear_acceleration[2] = event.values[2] - gravity[2];
            //            float xValue = (float) (((int) (linear_acceleration[0] * 100 + 0.5)) / 100.0);
            //            float yValue = (float) (((int) (linear_acceleration[1] * 100 + 0.5)) / 100.0);
            //            float zValue = (float) (((int) (linear_acceleration[2] * 100 + 0.5)) / 100.0);
            float xValue = event.values[0];
            float yValue = event.values[1];
            float zValue = event.values[2];
            if (mIsOne)
            {
                if (null != mCallback)
                {
                    mCallback.acceInfo("\nx:" + xValue + ", y:" + yValue + ", z:" + zValue);
                    mIsOne = false;
                }
            }
        }
        // 线性加速度传感器获取线性加速度
        else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            float xValue = (float) (((int) (event.values[0] * 10000 + 0.5)) / 10000.0);
            float yValue = (float) (((int) (event.values[1] * 10000 + 0.5)) / 10000.0);
            float zValue = (float) (((int) (event.values[2] * 10000 + 0.5)) / 10000.0);
            //            float xValue = event.values[0];
            //            float yValue = event.values[1];
            //            float zValue = event.values[2];
            float acce = (getAcce(xValue, yValue, zValue));
            acce = (float) (((int) (acce * 10000 + 0.5)) / 10000.0);
            if (mIsOne)
            {
                if (null != mCallback)
                {

                    mBuilder.append(acce+"," + mTimes + "\n");
                    mTimes+=100;
                    //                mCallback.acceInfo("\nx:" + xValue + ", y:" + yValue + ", z:" + zValue);
                    mCallback.acceInfo("\n" + (getAcce(xValue, yValue, zValue) + SensorManager.GRAVITY_EARTH));
                    mCallback.acceInfo2("\ng:" + getAcce(xValue, yValue, zValue));
                    mIsOne = false;
                }
            }
        }
    }

    // 获取合加速度
    private float getAcce(float ax, float ay, float az)
    {
        // 三个加速度的方向，以正负1标识
        int x;
        int y;
        int z;
        x = ax < 0 ? -1 : 1;
        y = ay < 0 ? -1 : 1;
        z = az < 0 ? -1 : 1;

        float total = (float) (x * Math.pow(ax, 2) + y * Math.pow(ay, 2) + z * Math.pow(az, 2));
        // 合加速度的方向
        int xyz = total < 0 ? -1 : 1;

        float a = (float) (xyz * Math.sqrt(Math.abs(total)));

        return a;
    }

    // 加速度回调
    public interface AcceCallback
    {
        void acceInfo(String info);

        void acceInfo2(String info);
    }

    public void write()
    {
        File sd = Environment.getExternalStorageDirectory();
        File file = null;
        try
        {
            file = new File(sd.getCanonicalPath() + "/acce.txt");
            if (file.exists())
            {
                file.delete();
            }
            file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(mBuilder.toString().getBytes());
            raf.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
