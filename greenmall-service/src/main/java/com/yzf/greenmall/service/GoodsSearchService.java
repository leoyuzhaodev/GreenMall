package com.yzf.greenmall.service;

import com.yzf.greenmall.bo.GoodsSearch;
import com.yzf.greenmall.common.PageResult;
import com.yzf.greenmall.common.SearchRequest;
import com.yzf.greenmall.entity.Goods;
import com.yzf.greenmall.entity.GoodsDetail;
import com.yzf.greenmall.entity.SalesVolume;
import com.yzf.greenmall.mapper.GoodsDetailMapper;
import com.yzf.greenmall.mapper.GoodsMapper;
import com.yzf.greenmall.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description:GoodsSearchService
 * @author:leo_yuzhao
 * @date:2020/11/18
 */
@Service
@Transactional
public class GoodsSearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SalesVolumeService salesVolumeService;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    /**
     * 初始化索引库
     */
    public void initGoodsIndex() {
        try {
            // 1，查找所有的商品，和商品详情
            List<Goods> allGoodsAndDetail = goodsMapper.findAllGoodsAndDetail();
            // 2，依次遍历，将商品和商品详情封装成为 GoodsSearch
            List<GoodsSearch> goodsSearches = new ArrayList<>();
            for (Goods goods : allGoodsAndDetail) {
                List<String> categoriesName = categoryService.findCategoriesNameByIds(Arrays.asList(goods.getCid1(), goods.getCid2(), goods.getCid3()));
                goods.setCategory(StringUtils.join(categoriesName, " "));
                GoodsSearch goodsSearch = GoodsSearch.generateGoodsSearch(goods);
                // 查询该商品的总销量
                goodsSearch.setSalesVolume(salesVolumeService.getGoodsAllSalesVolume(goods.getId()));
                // 设置该商品的评分 todo:建立评论表
                goodsSearch.setEvaluationScores((int) (Math.random() * 10));
                goodsSearches.add(goodsSearch);
            }
            // 3，将数据存入数据库中
            this.goodsRepository.saveAll(goodsSearches);
        } catch (Exception e) {
            System.out.println("初始化索引库失败...");
            e.printStackTrace();
        }
        System.out.println("初始化索引库成功");
    }

    /**
     * 商品搜索
     *
     * @param request
     * @return
     */
    public PageResult<GoodsSearch> search(SearchRequest request) {

        // 1，判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        String key = request.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 2，构建 elasticSearch 查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 3，根据 key值，all字段 进行全文检索查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));

        // 4，过滤搜索结果 id,images,price,subtitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "images", "price", "subTitle", "salesVolume", "evaluationScores"}, null));

        // 5，分页
        int page = request.getPage();
        int pageSize = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, pageSize));

        // 6，按字段进行排序
        String sortBy = request.getSortBy();
        Boolean isAsc = request.getIsAsc();
        if (!StringUtils.isBlank(sortBy) && !sortBy.equals("default")) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(isAsc ? SortOrder.ASC : SortOrder.DESC));
        }

        // 6，查询获取结果
        Page<GoodsSearch> goodsSearchPage = this.goodsRepository.search(queryBuilder.build());

        // 7，返回结果
        return new PageResult<GoodsSearch>(goodsSearchPage.getTotalElements(), goodsSearchPage.getTotalPages(), goodsSearchPage.getContent());
    }
}
