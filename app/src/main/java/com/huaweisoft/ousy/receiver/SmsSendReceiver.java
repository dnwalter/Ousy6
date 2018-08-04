package com.huaweisoft.ousy.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * 监听短信发送状态接收器
 * Created by ousy on 2016/11/10.
 */

public class SmsSendReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            switch (getResultCode())
            {
                    /* 发送短信成功 */
                case Activity.RESULT_OK:
                    break;
                    /* 发送短信失败 */
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                default:

                    break;
            }
        } catch (Exception e)
        {
            e.getStackTrace();
        }
    }
}
