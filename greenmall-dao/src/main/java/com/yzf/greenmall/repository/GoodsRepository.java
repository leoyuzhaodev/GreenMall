package com.yzf.greenmall.repository;

import com.yzf.greenmall.bo.GoodsSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @description: elasticsearch 文档操作接口
 * @author:leo_yuzhao
 * @date:2020/11/18
 */
public interface GoodsRepository extends ElasticsearchRepository<GoodsSearch, Long> {
}
