package com.huaweisoft.ousy.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import com.huaweisoft.ousy.MainApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 手机功能
 * Created by ousy on 2016/7/2.
 */

public enum PhoneUtil
{
    INSTANCE;
    private static final String TAG = PhoneUtil.class.getSimpleName();
    private Context mContext;
    private AudioManager mManager;
    private Vibrator mVibrator;
    private ConnectivityManager mConnManager;
    private TelephonyManager mTelephonyManager;

    @SuppressLint("WrongConstant")
    private PhoneUtil()
    {
        mContext = MainApplication.getContext();
        mManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mConnManager = (ConnectivityManager) mContext.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 手机震动
     *
     * @param sec 震动时间（毫秒）
     *            权限：android.permission.VIBRATE"
     */
    public void vibrate(int sec)
    {
        mVibrator.vibrate(sec);
    }

    public int getVolume()
    {
        int volume = mManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        return volume;
    }

    public int getMaxVolume()
    {
        int max = mManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        return max;
    }

    public void setVolume(int index)
    {
        mManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }

    public void upVolume()
    {
        mManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    public void downVolume()
    {
        mManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    public void setMute(boolean isMute)
    {
        mManager.setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
    }

    // 设置GPRS
    public void setMobileData(boolean state)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            openMobileData2(state);
        }
        else
        {
            openMobileData1(state);
        }
    }

    /**
     * android5.0以前可调用这方法，改变GPRS
     * 权限：CHANGE_NETWORK_STATE
     *
     * @param state
     */
    public void openMobileData1(boolean state)
    {
        try
        {
            @SuppressLint("WrongConstant") ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, state);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * android5.0以后
     * 系统权限：MODIFY_PHONE_STATE
     *
     * @param state
     */
    public void openMobileData2(boolean state)
    {
        Method setMobileDataEnabledMethod = null;
        try
        {
            setMobileDataEnabledMethod = mTelephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean
                    .class);
            if (null != setMobileDataEnabledMethod)
            {
                setMobileDataEnabledMethod.invoke(mTelephonyManager, state);
            }
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            Throwable throwable = ((InvocationTargetException) e).getTargetException();
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 是否开启数据连接
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean isMobileDataOn()
    {
        Boolean isOpen = false;

        try
        {
            String methodName = "getMobileDataEnabled";
            Class cmClass = mConnManager.getClass();
            Class[] classes = new Class[0];
            Object[] objects = new Object[0];
            Method method = cmClass.getMethod(methodName, classes);
            isOpen = (Boolean) method.invoke(mConnManager, objects);
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }

        return isOpen;
    }

    // 获得可用的内存
    public long getmem_UNUSED(Context mContext)
    {
        long MEM_UNUSED;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        // 取得剩余的内存空间

        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    // 获得总内存
    public long getmem_TOLAL()
    {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null)
            {
                content = line;
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息

        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }

    /**
     * SDCARD是否存
     */
    public boolean externalMemoryAvailable()
    {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     * MB
     *
     * @return
     */
    public float getAvailableInternalMemorySize()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / (1024f * 1024f);
    }

    /**
     * 获取手机内部总的存储空间
     * MB
     *
     * @return
     */
    public static float getTotalInternalMemorySize()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize / (1024f * 1024f);
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public long getAvailableExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        else
        {
            return -1;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public long getTotalExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        else
        {
            return -1;
        }
    }

}
