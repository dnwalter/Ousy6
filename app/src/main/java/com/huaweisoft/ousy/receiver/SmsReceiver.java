package com.huaweisoft.ousy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 监听短信接受
 * Created by ousy on 2016/11/10.
 */

public class SmsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle)
        {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj)
            {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                String phone = msg.getOriginatingAddress();
                String body = msg.getDisplayMessageBody();

                //在这里写自己的逻辑
                if (msg.getOriginatingAddress().equals("10086"))
                {
                    Log.e("ousysms",body);
                }
            }
        }
    }

}
