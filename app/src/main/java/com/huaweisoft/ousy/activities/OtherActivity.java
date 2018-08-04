package com.huaweisoft.ousy.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huaweisoft.ousy.R;

/**
 * Created by ousy on 2016/11/24.
 */

public class OtherActivity extends AppCompatActivity
{
    private Button btnTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        btnTest = (Button) findViewById(R.id.test);

        btnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = null;
                if (str.equals(""))
                {
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Log.e("ousytest","ousytest,this");
        super.onDestroy();
    }
}
