package com.yzf.greenmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @description:GreenMallApplication
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@SpringBootApplication
@MapperScan("com.yzf.greenmall.mapper")
public class GreenMallServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenMallServiceApplication.class);
    }
}
