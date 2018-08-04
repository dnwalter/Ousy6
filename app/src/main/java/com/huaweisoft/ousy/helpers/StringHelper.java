package com.huaweisoft.ousy.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ousy on 2016/6/2.
 */
public class StringHelper
{
    /**
     * 字符串是否为NULL或者为空字符串
     *
     * @param str 目标字符串
     * @return 返回结果
     */
    public static boolean isNullOrEmpty(String str)
    {
        return isNullOrEmpty(str, false);
    }

    /**
     * 字符串是否为NULL或者为仅包含空白字符的字符串或者空字符串
     *
     * @param str       目标字符串
     * @param afterTrim 是否经过去掉末尾空格
     * @return 返回结果
     */
    public static boolean isNullOrEmpty(String str, boolean afterTrim)
    {
        boolean flag = false;
        if (str == null || str.isEmpty())
        {
            flag = true;
        }
        else
        {
            if (afterTrim && str.trim().isEmpty())
            {
                flag = true;
            }
        }

        return flag;
    }

    /**
     * 转成int型，默认值0
     *
     * @param str
     * @return
     */
    public static int getInt(String str) {
        int i;
        try
        {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e)
        {
            i = 0;
        }

        return i;
    }

    /**
     * 转成float型，默认值0
     *
     * @param str
     * @return
     */
    public static float getFloat(String str) {
        float i;
        try
        {
            i = Float.parseFloat(str);
        } catch (NumberFormatException e)
        {
            i = 0;
        }

        return i;
    }


    /**
     * 转成double型，默认值0
     *
     * @param str
     * @return
     */
    public static double getDouble(String str) {
        double i;
        try
        {
            i = Double.parseDouble(str);
        } catch (NumberFormatException e)
        {
            i = 0;
        }

        return i;
    }

    /**
     * 从字符串中获取网址
     *
     * @param str
     * @return
     */
    public static List<String> getUrl(String str)
    {
        List<String> url = new ArrayList<>();
        Matcher m = Pattern.compile("(?i)http://[^[A-Za-z0-9\\._\\?%&+\\-=/#]]*").matcher(str);
        while (m.find())
        {
            url.add(m.group());
        }

        return url;
    }

    public static Date getDate(String strDate)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try
        {
            date = dateFormat.parse(strDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date;
    }

    public static String getStrDate(Date date)
    {
        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        return strDate;
    }

    // long型转成IP地址格式
    public static String longToIp(long ip)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }
}
