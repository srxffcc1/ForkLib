/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kymjs.common;

import android.annotation.TargetApi;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ZhangTao on 7/1/16.
 */
public class DateUtils {
    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    /**
     * 指定格式返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(new Date());
    }

    /**
     * 返回当前系统时间(格式以HH:mm形式)
     */
    public static String getDataTime() {
        return getDataTime("HH:mm");
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static boolean isToday(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (time.monthDay == thenMonthDay);
    }

    public static String getFriendlyTime2(Date date) {
        String showStr = "";
        if (isToday(date.getTime())) {
            showStr = "今天";
        } else {
            showStr = getFriendlyTime(date);
        }
        return showStr;
    }

    /**
     * 转换日期到指定格式方便查看的描述说明
     *
     * @return 几秒前，几分钟前，几小时前，几天前，几个月前，几年前，很久以前（10年前）,如果出现之后的时间，则提示：未知
     */
    public static String getFriendlyTime(Date date) {
        String showStr = "";
        long yearSeconds = 31536000L;//365 * 24 * 60 * 60;
        long monthSeconds = 2592000L;//30 * 24 * 60 * 60;
        long daySeconds = 86400L;//24 * 60 * 60;
        long hourSeconds = 3600L;//60 * 60;
        long minuteSeconds = 60L;

        long time = (System.currentTimeMillis() - date.getTime()) / 1000;
        if (time <= 50) {
            showStr = "刚刚";
            return showStr;
        }
        if (time / yearSeconds > 0) {
            int year = (int) (time / yearSeconds);
            if (year > 10)
                showStr = "很久以前";
            else {
                showStr = year + "年前";
            }
        } else if (time / monthSeconds > 0) {
            showStr = time / monthSeconds + "个月前";
        } else if (time / daySeconds > 7) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd", Locale.getDefault());
            showStr = formatter.format(date);
        } else if (time / daySeconds > 0) {
            showStr = time / daySeconds + "天前";
        } else if (time / hourSeconds > 0) {
            showStr = time / hourSeconds + "小时前";
        } else if (time / minuteSeconds > 0) {
            showStr = time / minuteSeconds + "分钟前";
        } else if (time > 0) {
            showStr = time + "秒前";
        }
        return showStr;
    }
    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 判断用户的设备时区是否为东八区（中国） 2014年7月31日
     *
     * @return
     */
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
            defaultVaule = true;
        else
            defaultVaule = false;
        return defaultVaule;
    }

    /**
     * 根据不同时区，转换时间 2014年7月31日
     */
    public static Date transformTime(Date date, TimeZone oldZone,
                                     TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;
    }
    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        return toDate(sdate, dateFormater.get());
    }
}
