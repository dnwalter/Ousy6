package com.huaweisoft.ousy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;


/**
 * 监听网络变化广播接收器
 * Created by ousy on 2016-10-20.
 */
public class NetworkChangeReceiver extends BaseReceiver<NetworkChangeReceiver.ChangeCallback>
{
    public NetworkChangeReceiver(Context context)
    {
        super(context);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        register(filter);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {

        String action = intent.getAction();

        //获取网络状态改变的action
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            if (null != mCallback)
            {
                mCallback.changing();
            }
        }
//        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            info = mConnectivityManager.getActiveNetworkInfo();
//            if (info != null && info.isAvailable()) {
//                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
//                    if (mIonBroadcastResultListenner != null) {
//                        mIonBroadcastResultListenner.onBroadcastResult(Eenum.BroadcastType.networkState, info.isConnected(), ConnectivityManager.TYPE_WIFI);
//                    }
//                }
//                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
//                    if (mIonBroadcastResultListenner != null) {
//                        mIonBroadcastResultListenner.onBroadcastResult(Eenum.BroadcastType.networkState, info.isConnected(), ConnectivityManager.TYPE_MOBILE);
//                    }
//                }
//            }else{
//                mIonBroadcastResultListenner.onBroadcastResult(Eenum.BroadcastType.networkState, false, -1);
//            }
//
//        }
    }



    public interface ChangeCallback
    {
        void changing();
    }
}
