package com.huaweisoft.ousy.activities.media;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huaweisoft.ffmpegcmd.FFmpegUtils;
import com.huaweisoft.ousy.R;

/**
 * FFmpeg命令行
 * Created by ousy on 2016/10/11.
 */

public class FFmpegAct extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);

        final EditText cmdEdittext = (EditText) this.findViewById(R.id.editText_cmd);
        Button startButton = (Button) this.findViewById(R.id.button_start);

        startButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                String cmdline = cmdEdittext.getText().toString();
                String[] argv = cmdline.split(" ");
                Integer argc = argv.length;
                int ret = FFmpegUtils.getInstance().ffmpegCmd(argc, argv);
                Log.e("ousy", "ret=" + ret);
            }
        });
    }
}
