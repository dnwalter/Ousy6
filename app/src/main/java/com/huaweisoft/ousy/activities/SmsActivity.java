package com.huaweisoft.ousy.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huaweisoft.ousy.R;
import com.huaweisoft.ousy.receiver.SmsSendReceiver;
import com.huaweisoft.ousy.utils.SmsUtils;

/**
 * Created by ousy on 2016/11/10.
 */

public class SmsActivity extends AppCompatActivity
{
    private static final String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private Button btnSend;
    private EditText etContent;
    private SmsUtils mSmsUtils;
    private SmsSendReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        mSmsUtils = SmsUtils.getInstance();
        // 权限 android.permission.READ_PHONE_STATE
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telManager.getSimOperator();
        if (operator != null)
        {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007"))
            {
                Log.e("ousySms", "中国移动");
                mSmsUtils.setSIMType(SmsUtils.SIMType.CHINA_MOBILE);
            }
            else if (operator.equals("46001"))
            {
                Log.e("ousySms", "中国联通");
                mSmsUtils.setSIMType(SmsUtils.SIMType.CHINA_UNICOM);
            }
            else if (operator.equals("46003"))
            {
                Log.e("ousySms", "中国电信");
                mSmsUtils.setSIMType(SmsUtils.SIMType.CHINA_TELECOM);
            }
        }

        btnSend = (Button) findViewById(R.id.btn_send);
        etContent = (EditText) findViewById(R.id.et_content);

        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                send("10086", etContent.getText().toString());
            }
        });

        // 注册广播
        IntentFilter filter = new IntentFilter(SMS_SEND_ACTIOIN);
        mReceiver = new SmsSendReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy()
    {
        if (null != mReceiver)
        {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    /**
     * 发短信
     * 短信权限
     *
     * @param phone
     * @param message
     */
    private void send(String phone, String message)
    {
        SmsManager smsm = SmsManager.getDefault();
        /* 创建自定义Action常数的Intent(给PendingIntent参数之用) */
        Intent itSend = new Intent(SMS_SEND_ACTIOIN);
        /* sentIntent参数为传送后接受的广播信息PendingIntent */
        PendingIntent sendPI = PendingIntent.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(),
                itSend, PendingIntent.FLAG_UPDATE_CURRENT);
        smsm.sendTextMessage(phone, null, message, sendPI, null);
    }
}
