package com.huaweisoft.ousy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.activities.BlueTooth.BlueToothActivity;
import com.huaweisoft.ousy.activities.SocketAct.ClientActivity;
import com.huaweisoft.ousy.activities.SocketAct.ServerActivity;
import com.huaweisoft.ousy.activities.media.AudioActivity;
import com.huaweisoft.ousy.activities.media.FFmpegAct;
import com.huaweisoft.ousy.helpers.HintHelper;
import com.huaweisoft.ousy.helpers.FileHelper;
import com.huaweisoft.ousy.utils.NetworkUtil;
import com.huaweisoft.ousy.views.BasePopupWindow;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BasePopupWindow.PopupWindowCallback
{
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btnService;
    private Button btnPhone;
    private Button btnSensor;
    private Button btnView;
    private Button btnServer;
    private Button btnClient;
    private Button btnFile;
    private Button btnStopThread;
    private Button btnBluetooth;
    private Button btnFFmpeg;
    private Button btnAudio;
    private Button btnWifi;
    private Button btnSms;
    private Button btnOther;
    private int mPercent = 0;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 0x123)
            {
                HintHelper.getInstance(MainActivity.this).setProgress(mPercent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btnService= (Button) findViewById(R.id.btn_service);
        btnPhone= (Button) findViewById(R.id.btn_phone);
        btnSensor= (Button) findViewById(R.id.btn_sensor);
        btnView= (Button) findViewById(R.id.btn_view);
        btnServer= (Button) findViewById(R.id.btn_server);
        btnClient= (Button) findViewById(R.id.btn_client);
        btnFile= (Button) findViewById(R.id.btn_file);
        btnStopThread= (Button) findViewById(R.id.btn_stopThread);
        btnBluetooth = (Button) findViewById(R.id.btn_bluetooth);
        btnFFmpeg = (Button) findViewById(R.id.btn_ffmpeg);
        btnAudio = (Button) findViewById(R.id.btn_audio);
        btnWifi = (Button) findViewById(R.id.btn_wifi);
        btnSms = (Button) findViewById(R.id.btn_sms);
        btnOther = (Button) findViewById(R.id.btn_other);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btnService.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnSensor.setOnClickListener(this);
        btnView.setOnClickListener(this);
        btnServer.setOnClickListener(this);
        btnClient.setOnClickListener(this);
        btnFile.setOnClickListener(this);
        btnStopThread.setOnClickListener(this);
        btnBluetooth.setOnClickListener(this);
        btnWifi.setOnClickListener(this);
        btnFFmpeg.setOnClickListener(this);
        btnAudio.setOnClickListener(this);
        btnSms.setOnClickListener(this);
        btnOther.setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        Log.e("ousytest","ousytest,MainAct");
        super.onDestroy();
    }

    public void doWork()
    {
        try
        {
            Thread.sleep(100);
            mPercent++;
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        Intent intent=new Intent();
        switch (v.getId())
        {
            case R.id.btn1:
                if (NetworkUtil.INSTANCE.isWifiAvailable())
                {
                    HintHelper.getInstance(this).progressDialogOne("请稍后","正在加载...");
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            SystemClock.sleep(3*1000);
                            HintHelper.getInstance(MainActivity.this).cancelProgressDialog();
                        }
                    }).start();
                }else
                {
                    // 启动WiFi设置界面
                    HintHelper.getInstance(this).askDialog("当前网络没有连接WLAN\n是否要设置", new HintHelper.IDialogAction()
                    {
                        @Override
                        public void action(int which, Object object)
                        {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    },null);
                }
                break;
            case R.id.btn2:
                HintHelper.getInstance(MainActivity.this).progressDialogTwo("任务完成百分比", "耗时任务完成百分比");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        while (mPercent < 100)
                        {
                            doWork();
                            mHandler.sendEmptyMessage(0x123);
                        }
                        if (mPercent >= 100)
                        {
                            HintHelper.getInstance(MainActivity.this).cancelProgressDialog();
                        }
                    }
                }).start();
                break;
            case R.id.btn3:
                if (NetworkUtil.INSTANCE.isNetworkAvailable())
                {
                    View view = LayoutInflater.from(this).inflate(R.layout.dialog_userapp_code, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case R.id.btn4:
                HintHelper.getInstance(MainActivity.this).dateDialog("日期", "1992-02-01", new HintHelper.IDialogAction()
                {
                    @Override
                    public void action(int which, Object object)
                    {

                    }
                });
                break;
            case R.id.btn_service:
                intent.setClass(this,ServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_phone:
                intent.setClass(this,PhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sensor:
                intent.setClass(this,SensorActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_view:
                intent.setClass(this,ViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_server:
                intent.setClass(this,ServerActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_client:
                intent.setClass(this,ClientActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_file:
                List<File> files = FileHelper.getFileSort(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rdCamera/PostImgs");
//                for (File i : files)
//                Log.e("ousy", i.getAbsolutePath());
                FileHelper.deleteFile(files.get(0).getAbsolutePath());
                break;
            case R.id.btn_stopThread:

                break;
            case R.id.btn_bluetooth:
                intent.setClass(this,BlueToothActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_wifi:
                intent.setClass(this,WifiActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_ffmpeg:
                intent.setClass(this, FFmpegAct.class);
                startActivity(intent);
                break;
            case R.id.btn_audio:
                intent.setClass(this, AudioActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sms:
                intent.setClass(this, SmsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_other:
                intent.setClass(this, OtherActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(int id)
    {
        switch (id)
        {
            case R.id.useractivity_llyt_camera:
                HintHelper.getInstance(this).toastShort("aaaa");
                break;
            case R.id.useractivity_llyt_photo:
                HintHelper.getInstance(this).toastShort("bbbb");
                break;
            default:
                break;
        }
    }
}
