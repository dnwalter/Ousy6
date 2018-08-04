package com.huaweisoft.ousy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ousy on 2016/11/10.
 */

public class SmsUtils
{
    //类加载时就初始化
    private static final SmsUtils sInstance = new SmsUtils();
    private SIMType mSIMType;

    private SmsUtils()
    {
    }

    public static SmsUtils getInstance()
    {
        return sInstance;
    }

    public void setSIMType(SIMType simType)
    {
        mSIMType = simType;
    }

    /**
     * 从字符串中获取网址，进行匹配
     *
     * @param str
     * @return
     */
    public static List<String> matchNetworkFlow(String str)
    {
        List<String> flow = new ArrayList<>();
        Matcher m = Pattern.compile("[0-9\\._\\?%&+\\-=/#]*").matcher(str);
        while (m.find())
        {
            flow.add(m.group());
        }
        return flow;
    }

    public enum SIMType
    {
        /**
         * 中国移动 10086
         * 查询流量：CXLL
         */
        CHINA_MOBILE(1),

        /**
         * 中国联通 10010
         * 查询流量：CXLL
         */
        CHINA_UNICOM(2),

        /**
         * 中国电信 10001
         * 查询流量：1081
         */
        CHINA_TELECOM(3);

        private int value;

        public int getValue()
        {
            return value;
        }

        SIMType(int value)
        {
            this.value = value;
        }
    }
}
