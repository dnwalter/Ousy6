package com.huaweisoft.ousy.activities.media;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.configs.MediaConfig;
import com.huaweisoft.ousy.helpers.FileHelper;

import java.io.File;

/**
 * Created by ousy on 2016/10/11.
 */

public class AudioActivity extends AppCompatActivity implements View.OnClickListener
{
    // 定义界面上的两个按钮
    Button record, stop;
    // 系统的音频文件
    File soundFile;
    MediaRecorder mRecorder;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        FileHelper.createFolder(MediaConfig.AUDIO_PATH);
        // 获取程序界面中的两个按钮
        record = (Button) findViewById(R.id.record);
        stop = (Button) findViewById(R.id.stop);
        // 为两个按钮的单击事件绑定监听器
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
//        if (null != mRecorder)
//        {
//            // 停止录音
//            mRecorder.stop();
//            // 释放资源
//            mRecorder.release();
//            mRecorder = null;
//        }
    }
    @Override
    public void onClick(View source)
    {
        switch (source.getId())
        {

            // 单击录音按钮
            case R.id.record:
                try
                {
                    // 创建保存录音的音频文件
                    soundFile = new File(MediaConfig.AUDIO_PATH + "/sound.aac");
                    mRecorder = new MediaRecorder();
                    // 设置录音的声音来源
                    mRecorder.setAudioSource(MediaRecorder
                            .AudioSource.MIC);
//                    // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
//                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                    // 设置声音编码的格式
//                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    // 设置文件音频的输出格式为amr
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    // 设置音频的编码格式为amr
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mRecorder.setOutputFile(soundFile.getAbsolutePath());
                    mRecorder.prepare();
                    // 开始录音
                    mRecorder.start();  // ①
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            // 单击停止按钮
            case R.id.stop:
//                if (soundFile != null && soundFile.exists())
//                {
//                    // 停止录音
//                    mRecorder.stop();  // ②
//                    // 释放资源
//                    mRecorder.release();  // ③
//                    mRecorder = null;
//                }
                String str = null;
                if (str.equals("")){}
                break;
        }
    }
}

