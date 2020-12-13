package com.yzf.greenmall.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description:日期工具类
 * @author:leo_yuzhao
 * @date:2020/12/12
 */
public class CalendarUtils {

    private static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 格式化日期
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getDateStr(Date date) {
        return defaultDateFormat.format(date);
    }

    /**
     * 将日期，向前或向后计算n天
     *
     * @param date    传入的日期
     * @param isAfter 向后计算ture，向前计算false
     * @param day     计算的天数
     * @return 字符串日期
     * @throws Exception
     */
    public static String getDateBeforeOrAfterDay(Date date, boolean isAfter, int day) {
        Date oldDate = date;
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(oldDate);
        if (isAfter) {
            rightNow.add(Calendar.DATE, day);//要加的日期
        } else {
            rightNow.add(Calendar.DATE, -day);//要减的日期
        }
        return defaultDateFormat.format(rightNow.getTime());
    }

    /**
     * 将日期，向前或向后计算n天
     *
     * @param date      传入的日期
     * @param oldFormat 传入的日期格式
     * @param isAfter   向后计算ture，向前计算false
     * @param day       计算的天数
     * @param newFormat 返回的日期字符串格式
     * @return 字符串日期
     * @throws Exception
     */
    public static String getDateBeforeOrAfterDay(String date, String oldFormat, boolean isAfter, int day, String newFormat) throws Exception {
        SimpleDateFormat osdf = new SimpleDateFormat(oldFormat);
        SimpleDateFormat nsdf = new SimpleDateFormat(newFormat);
        Date oldDate = osdf.parse(date);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(oldDate);
        if (isAfter)
            rightNow.add(Calendar.DATE, day);//要加的日期
        else
            rightNow.add(Calendar.DATE, -day);//要减的日期
        return nsdf.format(rightNow.getTime());
    }


    /**
     * 某月最后一天
     *
     * @param year
     * @param month
     * @param day
     * @param formate
     * @return
     */
    public static String getLastDay(int year, int month, int day, String formate) {
        SimpleDateFormat df = new SimpleDateFormat(formate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return df.format(calendar.getTime());
    }


    /**
     * 某个日期的下N个月
     *
     * @return
     */
    public static String getNextMonth_N(String gscstr, int N) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(gscstr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, N);

        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s).append(" 23:59:59");
        return str.toString();


    }

    /**
     * 当前日期的下一天
     */
    public static String getNextDay(String gscstr) throws Exception {
        return getNextDay_N(gscstr, 1);
    }

    /**
     * 当前日期的下N天
     */
    public static String getNextDay_N(String gscstr, int N) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(gscstr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, N);

        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s).append(" 00:00:00");
        return str.toString();
    }

    /**
     * 当前日期的上一天
     */
    public static String getForwardDay(String gscstr) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(gscstr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s).append(" 00:00:00");
        return str.toString();
    }

    /**
     * 当前日期的下N年
     */
    public static String getNextYear_N(String gscstr, int N) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(gscstr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, N);

        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s).append(" 00:00:00");
        return str.toString();
    }
}
