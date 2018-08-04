package com.huaweisoft.ousy.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.huaweisoft.ousy.MainApplication;
import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.sensor.AccelerometerSensor;
import com.huaweisoft.ousy.sensor.MagneticSensor;
import com.huaweisoft.ousy.sensor.OrientationSensor;

import java.util.List;

/**
 * 传感器界面
 * Created by ousy on 2016/8/8.
 */
public class SensorActivity extends AppCompatActivity implements AccelerometerSensor.AcceCallback, MagneticSensor
        .MagneticCallback, OrientationSensor.OrientationCallback
{
    private TextView tvAShow;
    private TextView tvMShow;
    private TextView tvGShow;
    private AccelerometerSensor mASensor2;
    private MagneticSensor mMSensor;
    private StringBuilder mBuilder = new StringBuilder("");
    private StringBuilder mMBuilder = new StringBuilder("");
    private StringBuilder mGBuilder = new StringBuilder("");
    private long nowTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        tvAShow = (TextView) findViewById(R.id.sensor_ashow);
        tvMShow = (TextView) findViewById(R.id.sensor_mshow);
        tvGShow = (TextView) findViewById(R.id.sensor_gshow);

        initSensor();
    }

    @Override
    protected void onDestroy()
    {
        mASensor2.unRegister();
        mMSensor.unRegister();
        mMSensor.mFlag = false;
        super.onDestroy();
    }

    private void initSensor()
    {
        SensorManager manager = (SensorManager) MainApplication.getContext().getSystemService(Context.SENSOR_SERVICE);

        //  获得当前手机支持的所有传感器
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor:sensors)
        {
            //  输出当前传感器的名称
            Log.e("type", sensor.getName());
        }
        mASensor2 = new AccelerometerSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mASensor2.setCallback(this);
        mMSensor = new MagneticSensor();
        mMSensor.setCallback(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (System.currentTimeMillis() - nowTime > 2000)
            {
                mASensor2.unRegister();
                mMSensor.unRegister();
                nowTime = System.currentTimeMillis();
                return true;
            }
            else
            {
                mASensor2.write();
                finish();
            }
        }
        return false;
    }

    @Override
    public void acceInfo(String info)
    {
        mBuilder.append(info);
        tvAShow.setText(mBuilder.toString());
    }

    @Override
    public void acceInfo2(String info)
    {
        mMBuilder.append(info);
        tvMShow.setText(mMBuilder.toString());
    }

    @Override
    public void magneticInfo(String info)
    {
//        mMBuilder.append(info);
//        tvMShow.setText(mMBuilder.toString());
    }

    @Override
    public void magneticAInfo(String info)
    {
//        mBuilder.append(info);
//        tvAShow.setText(mBuilder.toString());
    }

    @Override
    public void magneticGInfo(String info)
    {
        mGBuilder.append(info);
        tvGShow.setText(mGBuilder.toString());
    }

    @Override
    public void OrientationInfo(String info)
    {
//        mBuilder.append(info);
//        tvAShow.setText(mBuilder.toString());
    }
}
