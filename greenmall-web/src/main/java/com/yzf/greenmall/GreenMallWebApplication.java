package com.yzf.greenmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @description: GreenMallWebApplication 启动器
 * @author:leo_yuzhao
 * @date:2020/11/2
 */
@SpringBootApplication
@MapperScan("com.yzf.greenmall.mapper")
public class GreenMallWebApplication {
    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(GreenMallWebApplication.class);
    }
}
