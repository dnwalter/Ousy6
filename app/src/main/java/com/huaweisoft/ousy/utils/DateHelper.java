package com.huaweisoft.ousy.utils;


import com.huaweisoft.ousy.helpers.StringHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by baiaj on 2016-05-24.
 */
public class DateHelper {
    /**
     * 日期格式 yyyy-MM-dd
     */
    public static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 日期格式 yyyy-MM-dd H:m:s
     */
    public static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd H:m:s");
    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 日期格式 yyyyMMddHHmmss
     */
    public static SimpleDateFormat format4 = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * 日期格式 yyyy-MM-dd HH:mm
     */
    public static SimpleDateFormat format5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * 日期格式 MM-dd HH:mm
     */
    public static SimpleDateFormat format6 = new SimpleDateFormat("MM-dd HH:mm");
    /**
     * 日期格式 yyyy
     */
    public static SimpleDateFormat format7 = new SimpleDateFormat("yyyy");
    // 时间单位
    public static final int UNIT_HOUR = 0; // 小时
    public static final int UNIT_MIN = 1; // 分钟
    public static final int UNIT_SEC = 2; // 秒
    public static final int UNIT_MM = 3; // 毫秒


    /**
     * 获取当前日期的字符串格式
     *
     * @param format
     * @return
     */
    public static String getNowDateFormateString(SimpleDateFormat format) {
        return format.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 将日期转成指定格式的字符串
     *
     * @param format
     * @param date
     * @return
     */
    public static String getDateFormateString(SimpleDateFormat format, Date date) {
        return format.format(date);
    }

    /**
     * 将指定格式的日期字符串转成日期
     *
     * @param format     日期格式化对象
     * @param dateString 日期字符串
     * @return 日期对象
     */
    public static Date getDateFromFormateString(SimpleDateFormat format, String dateString)
            throws ParseException {
        return format.parse(dateString);
    }

    /**
     * @param dateString 待转换的日期字符串
     * @param srcformat  待转换的日期字符串格式对象
     * @param desformat  转换后的日期字符串格式对象
     * @return
     * @throws ParseException
     */
    public static String ConvertDateString(String dateString, SimpleDateFormat srcformat,
                                           SimpleDateFormat desformat) throws ParseException {
        return getDateFormateString(desformat, getDateFromFormateString(srcformat, dateString));
    }


//    /**
//     * 字符串转日期
//     *
//     * @param str
//     * @return
//     * @throws HWException
//     */
//    public static Date getDate(String str) throws HWException {
//        try {
//            return (str == null || str.trim().isEmpty()) ? getNowDate() : getDateFromFormateString(
//                    format3, str);
//        } catch (ParseException e) {
//            throw new HWException("日期：" + str + "不是\"" + format3.toPattern() + "\"格式", e);
//        }
//    }

    /**
     * 判断一个日期是否在某个时间段内
     *
     * @param srcDate
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean isBetween(Date srcDate, Date startDate, Date endDate) {
        boolean flag = false;
        long d1 = startDate.getTime();
        long d2 = endDate.getTime();
        long d3 = srcDate.getTime();
        if (d3 >= d1 && d3 <= d2) {
            flag = true;
        }
        return flag;
    }

    /**
     * 判断一个日期是否在另一个日期后面
     *
     * @param srcDate
     * @param startDate
     * @return
     */
    public static boolean isAfter(Date srcDate, Date startDate) {
        boolean flag = false;
        long d1 = startDate.getTime();
        long d2 = srcDate.getTime();
        if (d2 > d1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 判断一个日期是否在另一个日期后面
     * @param srcDateStr
     * @param startDateStr
     * @return
     * @throws Exception
     */
    public static boolean isAfter(String srcDateStr, String startDateStr) throws Exception {
        boolean flag = false;
        SimpleDateFormat sdf = format3;
        Date startDate = sdf.parse(startDateStr);
        Date srcDate = sdf.parse(srcDateStr);
        long d1 = startDate.getTime();
        long d2 = srcDate.getTime();
        if (d2 > d1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 判断一个日期是否在另一个日期前面
     *
     * @param srcDate
     * @param startDate
     * @return
     */
    public static boolean isBefore(Date srcDate, Date startDate) {
        boolean flag = false;
        long d1 = startDate.getTime();
        long d2 = srcDate.getTime();
        if (d2 < d1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 判断两个日期是否相等
     *
     * @param srcDate
     * @param startDate
     * @return
     */
    public static boolean isEquals(Date srcDate, Date startDate) {
        boolean flag = false;
        if (srcDate.getYear() == startDate.getYear() && srcDate.getMonth() == startDate.getMonth()
                && srcDate.getDay() == startDate.getDay()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取date1和date2的时间之差
     *
     * @param unit 单位
     * @param date1
     * @param date2
     * @return
     */
    public static int getDeference(int unit, Date date1, Date date2) {
        // 每小时的许毫秒数为：60 * 60 *1000 = 3600000
        Long milliSeconde = date1.getTime() - date2.getTime();
        int intUnit;
        switch (unit)
        {
            case UNIT_HOUR:
                intUnit = 3600000;
                break;
            case UNIT_MIN:
                intUnit = 60000;
                break;
            case UNIT_SEC:
                intUnit = 1000;
                break;
            case UNIT_MM:
                intUnit = 1;
                break;
            default:
                intUnit = 1;
                break;
        }
        Double hour = (double) milliSeconde / intUnit;
        String strHour = hour.toString().substring(0, hour.toString().indexOf("."));
        int deference = StringHelper.getInt(strHour);

        return deference;
    }

    /**
     * 比较两个日期的天数差距，输入都为format3的时间字符串
     *
     * @param date1 日期1 当前时间
     * @param date2 日期2 截止时间
     * @param day   过期天数设置
     * @return
     * @throws Exception
     */
    public static boolean dateCompare(String date1, String date2, int day) throws Exception { // 设定时间的模板
        SimpleDateFormat sdf = format3;
        Date d1 = sdf.parse(date1);
        Date d2 = sdf.parse(date2);
        if (((d1.getTime() - d2.getTime()) / (24 * 3600 * 1000)) >= day) {
            return true;
        }
        return false;
    }



    /**
     * 将分钟转化成小时
     *
     * @param min
     * @return
     */
    public static int minToHour(int min) {
        return min / 60;
    }

    /**
     * 把分钟转化成天、时、分
     *
     * @param min
     * @return
     */
    public static String minToDayHourMin(int min) {
        int day = 0;
        int hour = 0;
        if (min >= 1440) {
            day = min / 1440;
            min = min % 1440;
        }
        if (60 <= min && min < 1440) {
            hour = min / 60;
            min = min % 60;
        }
        return (day > 0 ? day + "天" : "") + (hour > 0 ? hour + "小时" : "")
                + (min > 0 ? min + "分钟" : "");
    }

    /**
     * 获得几天之前或者几天之后的日期
     * @param diff 差值：正的往后推，负的往前推
     * @return
     */
    public static String getOtherDay(int diff) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.DATE, diff);
        return getDateFormat(mCalendar.getTime());
    }

    /**
     * 将date转成yyyy-MM-dd字符串<br>
     * @param date Date对象
     * @return yyyy-MM-dd
     */
    public static String getDateFormat(Date date) {
        return dateSimpleFormat(date, defaultDateFormat.get());
    }

    /**
     * 将date转成字符串
     * @param date Date
     * @param format SimpleDateFormat
     * <br>
     * 注： SimpleDateFormat为空时，采用默认的yyyy-MM-dd HH:mm:ss格式
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
        if (format == null)
            format = defaultDateTimeFormat.get();
        return (date == null ? "" : format.format(date));
    }

    /** yyyy-MM-dd HH:mm:ss格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>()
    {

        @Override
        protected SimpleDateFormat initialValue() {
            return format3;
        }

    };

    /** yyyy-MM-dd格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return format1;
        }

    };
}
