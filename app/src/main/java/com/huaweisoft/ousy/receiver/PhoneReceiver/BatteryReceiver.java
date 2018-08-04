package com.huaweisoft.ousy.receiver.PhoneReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.huaweisoft.ousy.receiver.BaseReceiver;

/**
 * 获取手机电量接收器
 * Created by ousy on 2016/8/1.
 */
public class BatteryReceiver extends BaseReceiver<BatteryReceiver.BatteryCallback>
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        StringBuilder sb = new StringBuilder();
        int rawlevel = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);
        int status = intent.getIntExtra("status", -1);
        int health = intent.getIntExtra("health", -1);
        int level = -1; // percentage, or -1 for unknown
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale;
        }
        sb.append("The phone");
        if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
            sb.append("'s battery feels very hot!");
        } else {
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    sb.append("no battery.");
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    sb.append("'s battery");
                    if (level <= 33)
                        sb.append(" is charging, battery level is low"
                                + "[" + level + "]");
                    else if (level <= 84)
                        sb.append(" is charging." + "[" + level + "]");
                    else
                        sb.append(" will be fully charged.");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    if (level == 0)
                        sb.append(" needs charging right away.");
                    else if (level > 0 && level <= 33)
                        sb.append(" is about ready to be recharged, battery level is low"
                                + "[" + level + "]");
                    else
                        sb.append("'s battery level is" + "[" + level + "]");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    sb.append(" is fully charged.");
                    break;
                default:
                    sb.append("'s battery is indescribable!");
                    break;
            }
        }
//        sb.append(' ');

        mCallback.battery(level, status);
    }

    public BatteryReceiver(Context context)
    {
        super(context);
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        register(batteryFilter);
    }

    public static String getStrStatus(int status)
    {
        String strStatus = "电量：";
        switch (status)
        {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                strStatus = "充电中：";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                strStatus = "已充满：";
                break;
            default:
                break;
        }

        return strStatus;
    }

    /**
     * 电量信息回调接口
     */
    public interface BatteryCallback
    {
        /**
         * @param level 电量
         * @param status 状态
         */
        void battery(int level, int status);
    }
}

