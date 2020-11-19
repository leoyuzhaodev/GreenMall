package com.yzf.greenmall.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzf.greenmall.GreenMallWebAdminApplication;
import com.yzf.greenmall.bo.GoodsBo;
import com.yzf.greenmall.entity.Param;
import com.yzf.greenmall.service.GoodsService;
import com.yzf.greenmall.service.ParamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:初始化测试数据
 * @author:leo_yuzhao
 * @date:2020/11/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenMallWebAdminApplication.class)
public class InitGoods {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ParamService paramService;

    @Test
    public void InitGoodsData() throws Exception {

        int categories[][] = new int[][]
                {
                        {1, 2, 3},
                        {1, 2, 4},
                        {1, 2, 5},
                        {1, 6, 7},
                        {1, 6, 8},
                        {1, 6, 9},
                        {1, 10, 11},
                        {1, 10, 12},
                        {1, 10, 13},
                        {1, 14, 15},
                        {1, 14, 16},
                        {1, 14, 17},
                        {1, 14, 18},
                };
        GoodsBo goodsBo = new GoodsBo();
        for (int i = 1; i <= 1000; i++) {

            // 1，设置商品基本信息
            goodsBo.setTitle("测试商品名称：" + i);
            goodsBo.setSubTitle("测试商品售卖标题：" + i);
            int cIndex = (int) (Math.random() * categories.length);
            int category[] = categories[cIndex];
            goodsBo.setCid1(Long.parseLong(category[0] + ""));
            goodsBo.setCid2(Long.parseLong(category[1] + ""));
            goodsBo.setCid3(Long.parseLong(category[2] + ""));

            // 2，设置商品详情
            goodsBo.setImages("http://image.greenmall.com/group1/M00/00/00/wKjmgF-2E9OAMzq0AABiHYHHMos838.jpg");
            goodsBo.setPrice(((Math.random() * 1000)));
            goodsBo.setParams(generateParamsJson(goodsBo.getCid3(), i));
            goodsBo.setDescription("<p><img src=\"http://image.greenmall.com/group1/M00/00/00/wKjmgF-2E9mAMEM_AABiHYHHMos502.jpg\" style=\"max-width:100%;\"><br></p>");
            goodsBo.setPackingList("测试商品打包");
            goodsBo.setAfterService("测试售后服务");
            goodsService.update(goodsBo);
            System.out.println("添加成功：" + i);
        }
    }

    private String generateParamsJson(Long cid3, int i) throws JsonProcessingException {
        List<Param> params = paramService.findParamsByCid(cid3);
        List<Map<String, String>> pValues = new ArrayList<>();
        if (CollectionUtils.isEmpty(params)) {
            return "[]";
        } else {
            for (Param param : params) {
                Map<String, String> pValue = new HashMap<>();
                pValue.put("id", param.getId() + "");
                if (param.getNumeric()) {
                    pValue.put("value", (int) (Math.random() * 1000) + "");
                } else {
                    pValue.put("value", "测试" + i);
                }
                pValues.add(pValue);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(pValues);
        return jsonStr;
    }

}
