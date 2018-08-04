package com.huaweisoft.ousy.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.ViewConfiguration;

import java.lang.reflect.Method;

/**
 * 手机相关信息工具类
 * <p/>
 * Created by 张源兆(zhangyz@huaweisoft.com) on 2016-04-28 10:50.
 */
public class PhoneHelper
{
    /**
     * 获取虚拟按键栏高度
     *
     * @param context 上下文
     * @return int 虚拟按键栏高度
     */
    public static int getNavigationBarHeight(Context context)
    {
        int result = 0;
        if (hasNavNar(context))
        {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

            if (resourceId > 0)
            {
                result = resources.getDimensionPixelSize(resourceId);
            }
        }

        return result;
    }

    /**
     * 判断是否存在虚拟按键栏
     *
     * @param context 上下文
     * @return boolean 若存在按键栏返回true,若不存在虚拟按键栏返回false
     */
    public static boolean hasNavNar(Context context)
    {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");

        if (0 != resourceId)
        {
            boolean hasNav = resources.getBoolean(resourceId);
            // 检查虚拟按键栏是否重写
            String navBarOverride = getNavBarOverride();
            
            if ("1".equals(navBarOverride))
            {
                hasNav = false;
            } else if ("0".equals(navBarOverride))
            {
                hasNav = true;
            }

            return hasNav;
        } else
        {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按钮栏是否重写
     *
     * @return String 若已重写返回1,若未重写返回0
     */
    private static String getNavBarOverride()
    {
        String navBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                navBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");

            } catch (Throwable e)
            {
                e.printStackTrace();
            }
        }

        return navBarOverride;
    }
}
