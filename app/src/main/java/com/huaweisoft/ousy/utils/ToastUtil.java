package com.huaweisoft.ousy.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.huaweisoft.ousy.MainApplication;

/**
 * 提示信息功能
 * Created by ousy on 2016/8/11.
 */
public class ToastUtil
{
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        @Override
        public void run() {
            mToast.cancel();
        }
    };

    public static void toastShort( String msg)
    {
        Toast toast = Toast.makeText(MainApplication.getContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static void toastLong( String msg)
    {
        Toast toast = Toast.makeText(MainApplication.getContext(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static void toast( String msg,int time)
    {
        Toast toast = Toast.makeText(MainApplication.getContext(), msg, time);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    // 设置toast只能显示一次
    public static void showToast(Context mContext, String toastMsg, int duration)
    {
        mHandler.removeCallbacks(r);
        if (mToast != null)
        {
            mToast.setText(toastMsg);
        }else
        {
            mToast = Toast.makeText(mContext,toastMsg,Toast.LENGTH_SHORT);
        }
        mHandler.postDelayed(r,duration);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }
}
