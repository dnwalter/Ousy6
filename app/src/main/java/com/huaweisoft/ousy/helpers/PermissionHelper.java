package com.huaweisoft.ousy.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.huaweisoft.ousy.MainApplication;

/**
 * 权限询问帮助类
 * Created by ousy on 2016/6/22.
 */
public class PermissionHelper
{
    public static boolean applyPermissions(Context context, String permission, int requestCode)
    {
        boolean isApply = false;
        //PERMISSION_DENIED表示需要进行申请权限，PERMISSION_GRANTED表示已允许授权
        if (ContextCompat.checkSelfPermission(MainApplication.getContext(), permission)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                // 向用户请求的权限
                ((Activity)context).requestPermissions(new String[]{permission}, requestCode);
            }
        }
        else
        {
           isApply = true;
        }

        return isApply;
    }
}
