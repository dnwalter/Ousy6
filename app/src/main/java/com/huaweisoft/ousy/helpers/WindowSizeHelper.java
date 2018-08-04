package com.huaweisoft.ousy.helpers;

import android.content.Context;
import android.view.WindowManager;

import com.huaweisoft.ousy.MainApplication;

/**
 * 屏幕尺寸帮助类
 * Created by ousy on 2016/8/11.
 */
public class WindowSizeHelper
{
    private static Context sContext = MainApplication.getContext();
    /**
     * dp转px
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     * @param pxValue
     * @return
     */
    public static int px2dip( float pxValue) {
        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = sContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕高度
     */
    public static int getWindowHeight()
    {
        WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        int windowHeight = wm.getDefaultDisplay().getHeight();

        return windowHeight;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWindowWidth()
    {
        WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        int windowWidth = wm.getDefaultDisplay().getWidth();

        return windowWidth;
    }
}
