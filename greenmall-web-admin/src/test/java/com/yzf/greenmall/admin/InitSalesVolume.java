package com.yzf.greenmall.admin;

import com.yzf.greenmall.GreenMallWebAdminApplication;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.SalesVolume;
import com.yzf.greenmall.service.GoodsService;
import com.yzf.greenmall.service.ParamService;
import com.yzf.greenmall.service.SalesVolumeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:生成销量测试数据
 * @author:leo_yuzhao
 * @date:2020/11/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenMallWebAdminApplication.class)
public class InitSalesVolume {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private SalesVolumeService salesVolumeService;

    @Test
    public void initSalesVolume() {
        List<Goods> goodsList = goodsService.findAll();
        Integer year = 2020;
        for (Goods goods : goodsList) {
            for (int i = 1; i <= 10; i++) {
                SalesVolume salesVolume = new SalesVolume(year, i, goods.getId(), Long.parseLong((int) (Math.random() * 1000) + ""));
                salesVolumeService.add(salesVolume);
            }
            System.out.println("添加成功...");
        }
    }

}
