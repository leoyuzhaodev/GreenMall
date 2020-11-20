package com.yzf.greenmall.common;

import java.util.Random;

/**
 * @description:生成指定位数的随机数字
 * @author:leo_yuzhao
 * @date:2020/10/26
 */
public class NumberUtils {
    /**
     * 生成指定位数的随机数字，最大支持 8 位数字
     * @param len 随机数的位数
     * @return 生成的随机数
     */
    public static String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(
                Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }
}