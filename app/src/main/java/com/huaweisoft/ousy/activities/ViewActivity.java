package com.huaweisoft.ousy.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.views.NumberUpProgressBar;

/**
 * Created by ousy on 2016/8/11.
 */
public class ViewActivity extends AppCompatActivity
{
    private NumberUpProgressBar mProgressBar;
    private EditText etPercent;
    private Button btnSure;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        initView();
    }

    private void initView()
    {
        mProgressBar = (NumberUpProgressBar) findViewById(R.id.numberup_progressbar);
        etPercent = (EditText) findViewById(R.id.view_et_percent);
        btnSure = (Button) findViewById(R.id.view_btn_sure);

        btnSure.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mProgressBar.setPercent(Integer.parseInt(etPercent.getText().toString()));
            }
        });
    }
}
