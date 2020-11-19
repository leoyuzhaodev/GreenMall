package com.yzf.greenmall.web;

import com.yzf.greenmall.GreenMallWebApplication;
import com.yzf.greenmall.bo.GoodsSearch;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.repository.GoodsRepository;
import com.yzf.greenmall.service.GoodsSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:ElasticSearchTest
 * @author:leo_yuzhao
 * @date:2020/11/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenMallWebApplication.class)
public class ElasticSearchTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsSearchService goodsSearchService;

    /**
     * 创建索引库：数据库
     * 创建映射：表
     */
    @Test
    public void creatIndex() {
        this.template.createIndex(GoodsSearch.class);
        this.template.putMapping(GoodsSearch.class);
    }

    /**
     * 删除索引库中的数据
     */
    @Test
    public void deleteAll(){
        goodsRepository.deleteAll();
    }

    /**
     * 初始化索引库数据
     */
    @Test
    public void initIndex() {
        goodsSearchService.initGoodsIndex();
    }


}
