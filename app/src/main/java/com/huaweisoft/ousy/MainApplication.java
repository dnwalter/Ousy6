package com.huaweisoft.ousy;

import android.app.Application;
import android.content.Context;

import com.huaweisoft.ousy.utils.CrashHandler;

/**
 * Created by ousy on 2016/5/20.
 */
public class MainApplication extends Application
{
    private static Context sContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sContext = getApplicationContext();
        CrashHandler.getInstance().init(this);
    }

    public static Context getContext()
    {
        return sContext;
    }
}
