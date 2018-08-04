package com.huaweisoft.ousy.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * 蓝牙状态改变接收器
 * Created by ousy on 2016/10/13.
 */

public class BluetoothChangeReceiver extends BaseReceiver<BluetoothChangeReceiver.BluetoothCallback>
{
    private static String TAG = BluetoothChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction())
        {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState)
                {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e(TAG, "onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "onReceive---------STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e(TAG, "onReceive---------STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG, "onReceive---------STATE_OFF");
                        break;
                }
                break;
        }
    }

    public BluetoothChangeReceiver(Context context)
    {
        super(context);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        register(filter);
    }

    public interface BluetoothCallback
    {
        void state(int type);
    }
}
