package com.yzf.greenmall.common;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class NumberCalUtil {
    /**
     * @param dividend : 被除数(分子)
     * @param divisor  : 除数(分母)
     * @param digit    : 保留几位小数
     * @return String
     * @Description: 整数相除结果转换成指定位数的百分数
     */
    public static String getPercent(int dividend, int divisor, int digit) {
        Float result = (float) dividend / (float) divisor;
        if (result.isNaN()) {
            return "--";
        } else {
            //获取格式化对象
            NumberFormat nt = NumberFormat.getPercentInstance();
            //设置百分数精确度,即保留几位小数
            nt.setMinimumFractionDigits(digit);
            String finalResult = nt.format(result);
            return finalResult;
        }
    }

    /**
     * 两个数相加
     *
     * @param number1
     * @param number2
     * @return
     */
    public static Double add(Double number1, Double number2) {
        try {
            BigDecimal b1 = new BigDecimal(number1).add(new BigDecimal(number2));
            return b1.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 两个数相乘
     *
     * @param number1
     * @param number2
     * @return
     */
    public static Double multiply(Double number1, Double number2) {
        try {
            BigDecimal b1 = new BigDecimal(number1).multiply(new BigDecimal(number2));
            return b1.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 两个数相减
     *
     * @param number1
     * @param number2
     * @return
     */
    public static Double subtract(Double number1, Double number2) {
        try {
            BigDecimal b1 = new BigDecimal(number1).subtract(new BigDecimal(number2));
            return b1.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 两个数相除
     *
     * @param str1
     * @param str2
     * @return
     */
    public static String divide(String str1, String str2) {
        try {
            return new BigDecimal(str1).divide(new BigDecimal(str2), 5).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 四舍六入五五成双
     *
     * @param str
     */
    public static String FourUpSixInto(String str) {
        BigDecimal b1 = new BigDecimal(str);
        BigDecimal b2 = b1.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return b2.toString();
    }
}