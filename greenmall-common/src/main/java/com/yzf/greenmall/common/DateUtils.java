package com.yzf.greenmall.common;

import org.elasticsearch.common.recycler.Recycler;

import java.util.Calendar;

/**
 * @description:日期工具类
 * @author:leo_yuzhao
 * @date:2020/12/10
 */
public class DateUtils {


    /**
     * 获取当前月
     */
    public static int getCurMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前年
     */
    public static int getCurYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

}
